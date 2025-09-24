package kopo.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kopo.userservice.auth.JwtTokenProvider;
import kopo.userservice.dto.TokenDTO;
import kopo.userservice.dto.PatientDTO;
import kopo.userservice.dto.ManagerDTO;
import kopo.userservice.service.IUserService;
import kopo.userservice.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@CrossOrigin(origins = {"http://localhost:13000", "http://localhost:14000"},
        allowedHeaders = {"POST,GET"},
        allowCredentials = "true")
@Tag(name = "로그인된 사용자들이 접근하는 API", description = "로그인된 사용자들이 접근하는 API 설명입니다.")
@Slf4j
@RequestMapping(value = "/user")
@RequiredArgsConstructor
@RestController
public class UserInfoController {

    private final JwtTokenProvider jwtTokenProvider;
    private final IUserService userService;

    @Operation(summary = "토큰에 저장된 회원정보 가져오기 API", description = "토큰에 저장된 회원정보 가져오기 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Page Not Found!"),
            }
    )
    @PostMapping(value = "getTokenInfo")
    public TokenDTO getTokenInfo(@RequestHeader("Authorization") String bearerToken) {
        log.info(this.getClass().getName() + ".getTokenInfo Start!");
        log.info("Authorization Header: {}", bearerToken);
        String jwtAccessToken = bearerToken.replace("Bearer ", "").trim();
        TokenDTO dto = Optional.ofNullable(jwtTokenProvider.getTokenInfo(jwtAccessToken)).orElse(null);
        log.info("TokenDTO : {}", dto);
        log.info(this.getClass().getName() + ".getTokenInfo End!");
        return dto;
    }

    @Operation(summary = "환자 회원정보 상세보기 API", description = "환자 회원정보 상세보기 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Page Not Found!"),
            }
    )
    @PostMapping(value = "patientInfo")
    public PatientDTO patientInfo(@RequestHeader("Authorization") String bearerToken) throws Exception {
        log.info(this.getClass().getName() + ".patientInfo Start!");
        String jwtAccessToken = bearerToken.replace("Bearer ", "").trim();
        String userId = CmmUtil.nvl(jwtTokenProvider.getTokenInfo(jwtAccessToken).userId());
        PatientDTO paramDTO = PatientDTO.builder().id(userId).build();
        PatientDTO rDTO = Optional.ofNullable(userService.getPatient(paramDTO)).orElse(null);
        log.info(this.getClass().getName() + ".patientInfo End!");
        return rDTO;
    }

    @Operation(summary = "관리자 회원정보 상세보기 API", description = "관리자 회원정보 상세보기 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Page Not Found!"),
            }
    )
    @PostMapping(value = "managerInfo")
    public ManagerDTO managerInfo(@RequestHeader("Authorization") String bearerToken) throws Exception {
        log.info(this.getClass().getName() + ".managerInfo Start!");
        String jwtAccessToken = bearerToken.replace("Bearer ", "").trim();
        String userId = CmmUtil.nvl(jwtTokenProvider.getTokenInfo(jwtAccessToken).userId());
        ManagerDTO paramDTO = ManagerDTO.builder().id(userId).build();
        ManagerDTO rDTO = Optional.ofNullable(userService.getManager(paramDTO)).orElse(null);
        log.info(this.getClass().getName() + ".managerInfo End!");
        return rDTO;
    }
}
