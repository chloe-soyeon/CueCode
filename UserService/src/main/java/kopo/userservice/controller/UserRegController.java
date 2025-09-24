package kopo.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kopo.userservice.dto.MsgDTO;
import kopo.userservice.dto.PatientDTO;
import kopo.userservice.service.IUserService;
import kopo.userservice.util.CmmUtil;
import kopo.userservice.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원가입을 위한 API", description = "회원가입을 위한 API 설명입니다.")
@Slf4j
@RequestMapping(value = "/reg")
@RequiredArgsConstructor
@RestController
public class UserRegController {
    private final IUserService userService;
    private final PasswordEncoder bCryptPasswordEncoder;

    @Operation(summary = "환자 회원가입 API", description = "환자 회원가입 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Page Not Found!"),
            }
    )
    @PostMapping(value = "insertPatient")
    public MsgDTO insertPatient(HttpServletRequest request) {
        log.info(this.getClass().getName() + ".insertPatient start!");
        int res = 0;
        String msg = "";
        MsgDTO dto;
        PatientDTO pDTO;
        try {
            String userId = CmmUtil.nvl(request.getParameter("user_id"));
            String userName = CmmUtil.nvl(request.getParameter("user_name"));
            String password = CmmUtil.nvl(request.getParameter("password"));
            String email = CmmUtil.nvl(request.getParameter("email"));
            String detectionAreaType = CmmUtil.nvl(request.getParameter("detectionAreaType")); // hand/face/both
            log.info("userId : " + userId);
            log.info("userName : " + userName);
            log.info("password : " + password);
            log.info("email : " + email);
            log.info("detectionAreaType : " + detectionAreaType);
            pDTO = PatientDTO.builder()
                    .id(userId)
                    .pw(bCryptPasswordEncoder.encode(password))
                    .email(EncryptUtil.encAES128CBC(email))
                    .name(userName)
                    .managerIds(java.util.Collections.emptyList())
                    .detectionAreaType(detectionAreaType)
                    .build();
            res = userService.insertPatient(pDTO);
            log.info("회원가입 결과(res) : " + res);
            if (res == 1) {
                msg = "회원가입되었습니다.";
            } else if (res == 2) {
                msg = "이미 가입된 아이디입니다.";
            } else {
                msg = "오류로 인해 회원가입이 실패하였습니다.";
            }
        } catch (Exception e) {
            msg = "실패하였습니다. : " + e;
            res = 2;
            log.info(e.toString());
        } finally {
            dto = MsgDTO.builder().result(res).msg(msg).build();
            log.info(this.getClass().getName() + ".insertPatient End!");
        }
        return dto;
    }

    @Operation(summary = "관리자 회원가입 API", description = "관리자 회원가입 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Page Not Found!"),
            }
    )
    @PostMapping(value = "insertManager")
    public MsgDTO insertManager(HttpServletRequest request) {
        log.info(this.getClass().getName() + ".insertManager start!");
        int res = 0;
        String msg = "";
        MsgDTO dto;
        kopo.userservice.dto.ManagerDTO mDTO;
        try {
            String userId = CmmUtil.nvl(request.getParameter("user_id"));
            String userName = CmmUtil.nvl(request.getParameter("user_name"));
            String password = CmmUtil.nvl(request.getParameter("password"));
            String email = CmmUtil.nvl(request.getParameter("email"));
            log.info("userId : " + userId);
            log.info("userName : " + userName);
            log.info("password : " + password);
            log.info("email : " + email);
            mDTO = kopo.userservice.dto.ManagerDTO.builder()
                    .id(userId)
                    .pw(bCryptPasswordEncoder.encode(password))
                    .email(EncryptUtil.encAES128CBC(email))
                    .name(userName)
                    .patientIds(java.util.Collections.emptyList())
                    .build();
            res = userService.insertManager(mDTO);
            log.info("관리자 회원가입 결과(res) : " + res);
            if (res == 1) {
                msg = "관리자 회원가입되었습니다.";
            } else if (res == 2) {
                msg = "이미 가입된 아이디입니다.";
            } else {
                msg = "오류로 인해 관리자 회원가입이 실패하였습니다.";
            }
        } catch (Exception e) {
            msg = "실패하였습니다. : " + e;
            res = 2;
            log.info(e.toString());
        } finally {
            dto = MsgDTO.builder().result(res).msg(msg).build();
            log.info(this.getClass().getName() + ".insertManager End!");
        }
        return dto;
    }

    @Operation(summary = "환자 정보 조회 API", description = "환자 정보 조회 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Page Not Found!"),
            }
    )
    @PostMapping("patientInfo")
    public MsgDTO patientInfo(HttpServletRequest request) {
        log.info(this.getClass().getName() + ".patientInfo start!");
        int res = 0;
        String msg = "";
        MsgDTO dto;
        try {
            String userId = CmmUtil.nvl(request.getParameter("user_id"));
            // 실제 환자 정보 조회 로직 구현 필요
            msg = "환자 정보 조회 성공: " + userId;
            res = 1;
        } catch (Exception e) {
            msg = "실패하였습니다. : " + e;
            res = 2;
            log.info(e.toString());
        } finally {
            dto = MsgDTO.builder().result(res).msg(msg).build();
            log.info(this.getClass().getName() + ".patientInfo End!");
        }
        return dto;
    }

    @Operation(summary = "관리자 정보 조회 API", description = "관리자 정보 조회 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Page Not Found!"),
            }
    )
    @PostMapping("managerInfo")
    public MsgDTO managerInfo(HttpServletRequest request) {
        log.info(this.getClass().getName() + ".managerInfo start!");
        int res = 0;
        String msg = "";
        MsgDTO dto;
        try {
            String userId = CmmUtil.nvl(request.getParameter("user_id"));
            // 실제 관리자 정보 조회 로직 구현 필요
            msg = "관리자 정보 조회 성공: " + userId;
            res = 1;
        } catch (Exception e) {
            msg = "실패하였습니다. : " + e;
            res = 2;
            log.info(e.toString());
        } finally {
            dto = MsgDTO.builder().result(res).msg(msg).build();
            log.info(this.getClass().getName() + ".managerInfo End!");
        }
        return dto;
    }

    @Operation(summary = "환자 정보 조회 API (/user/patientInfo)", description = "환자 정보 조회 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Page Not Found!"),
            }
    )
    @PostMapping("/user/patientInfo")
    public MsgDTO userPatientInfo(HttpServletRequest request) {
        log.info(this.getClass().getName() + ".userPatientInfo start!");
        int res = 0;
        String msg = "";
        MsgDTO dto;
        try {
            String userId = CmmUtil.nvl(request.getParameter("user_id"));
            msg = "(user) 환자 정보 조회 성공: " + userId;
            res = 1;
        } catch (Exception e) {
            msg = "실패하였습니다. : " + e;
            res = 2;
            log.info(e.toString());
        } finally {
            dto = MsgDTO.builder().result(res).msg(msg).build();
            log.info(this.getClass().getName() + ".userPatientInfo End!");
        }
        return dto;
    }

    @Operation(summary = "관리자 정보 조회 API (/user/managerInfo)", description = "관리자 정보 조회 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Page Not Found!"),
            }
    )
    @PostMapping("/user/managerInfo")
    public MsgDTO userManagerInfo(HttpServletRequest request) {
        log.info(this.getClass().getName() + ".userManagerInfo start!");
        int res = 0;
        String msg = "";
        MsgDTO dto;
        try {
            String userId = CmmUtil.nvl(request.getParameter("user_id"));
            msg = "(user) 관리자 정보 조회 성공: " + userId;
            res = 1;
        } catch (Exception e) {
            msg = "실패하였습니다. : " + e;
            res = 2;
            log.info(e.toString());
        } finally {
            dto = MsgDTO.builder().result(res).msg(msg).build();
            log.info(this.getClass().getName() + ".userManagerInfo End!");
        }
        return dto;
    }
}
