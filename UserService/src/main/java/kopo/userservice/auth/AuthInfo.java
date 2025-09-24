package kopo.userservice.auth;

import kopo.userservice.dto.UserDTO;
import kopo.userservice.util.CmmUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security 인증 과정에서 사용자의 상세 정보를 저장하는 클래스
 * - UserDTO(ManagerDTO, PatientDTO)를 감싸서 Security의 UserDetails 형태로 변환한다.
 * - SecurityContext에 저장되어 인증/인가 과정에서 사용된다.
 */
@Slf4j
public record AuthInfo(UserDTO user) implements UserDetails {

    /**
     * 사용자의 권한(Role)을 반환한다.
     * - UserDTO의 role() 값에 따라 "ROLE_MANAGER" 또는 "ROLE_PATIENT" 부여
     * - Security에서 인가(authorization) 시 해당 권한을 사용한다.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = user.authority(); // e.g. "ROLE_MANAGER" or "ROLE_PATIENT"
        log.info("getAuthorities / role : {}", role);
        return List.of(new SimpleGrantedAuthority(role));
    }

    /**
     * 사용자의 고유 ID(username)를 반환한다.
     * - Spring Security에서 username은 인증의 기준 값
     * - 여기서는 UserDTO.id()를 username으로 사용한다.
     */
    @Override
    public String getUsername() {
        return CmmUtil.nvl(user.id());
    }

    /**
     * 사용자의 비밀번호를 반환한다.
     * - Spring Security가 로그인 시 입력받은 비밀번호와 비교하는 값
     * - 반드시 암호화(BCrypt 등)된 값이어야 한다.
     */
    @Override
    public String getPassword() {
        return CmmUtil.nvl(user.pw());
    }

    /**
     * 계정 만료 여부를 반환한다.
     * - true이면 계정이 만료되지 않은 상태
     * - 필요 시 DB에 만료일을 두고 체크하도록 확장할 수 있다.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠금 여부를 반환한다.
     * - true이면 계정이 잠금되지 않은 상태
     * - 필요 시 DB에 잠금 여부 컬럼을 두고 체크할 수 있다.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 자격 증명(비밀번호) 만료 여부를 반환한다.
     * - true이면 비밀번호가 만료되지 않은 상태
     * - 필요 시 비밀번호 변경 주기를 두고 체크할 수 있다.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정 사용 가능 여부를 반환한다.
     * - true이면 계정이 활성화된 상태
     * - 필요 시 탈퇴 여부, 정지 여부를 체크하도록 확장할 수 있다.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
