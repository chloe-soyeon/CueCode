package kopo.userservice.service.impl;

import kopo.userservice.dto.PatientDTO;
import kopo.userservice.dto.ManagerDTO;
import kopo.userservice.repository.PatientRepository;
import kopo.userservice.repository.ManagerRepository;
import kopo.userservice.repository.DetectionAreaRepository;
import kopo.userservice.model.PatientDocument;
import kopo.userservice.model.ManagerDocument;
import kopo.userservice.repository.document.DetectionAreaDocument;
import kopo.userservice.service.IUserService;
import kopo.userservice.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService implements IUserService {
    private final PatientRepository patientRepository;
    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;
    private final DetectionAreaRepository detectionAreaRepository;

    @Override
    public Object login(String userId, String password) {
        log.info("[login] 로그인 시도: userId={}, password={}", userId, password != null ? "***" : null);
        if (userId == null || userId.isBlank()) {
            log.warn("[login] userId가 null 또는 빈 문자열입니다.");
            return null;
        }
        // 1. 환자 인증 시도
        Optional<PatientDocument> patientOpt = patientRepository.findById(userId);
        if (patientOpt.isPresent()) {
            log.info("[login] 환자(userId={}) 정보 조회 성공", userId);
            PatientDocument patient = patientOpt.get();
            if (patient.getPw() != null && passwordEncoder.matches(password, patient.getPw())) {
                log.info("[login] 환자(userId={}) 비밀번호 일치, 인증 성공", userId);
                return PatientDTO.builder()
                        .id(patient.getId())
                        .pw(patient.getPw())
                        .email(patient.getEmail())
                        .name(patient.getName())
                        .managerIds(patient.getManagerIds())
                        .build();
            } else {
                log.info("[login] 환자(userId={}) 비밀번호 불일치", userId);
            }
        } else {
            log.info("[login] 환자(userId={}) 정보 없음", userId);
        }
        // 2. 관리자 인증 시도
        Optional<ManagerDocument> managerOpt = managerRepository.findById(userId);
        if (managerOpt.isPresent()) {
            log.info("[login] 관리자(userId={}) 정보 조회 성공", userId);
            ManagerDocument manager = managerOpt.get();
            if (manager.getPw() != null && passwordEncoder.matches(password, manager.getPw())) {
                log.info("[login] 관리자(userId={}) 비밀번호 일치, 인증 성공", userId);
                return ManagerDTO.builder()
                        .id(manager.getId())
                        .pw(manager.getPw())
                        .email(manager.getEmail())
                        .name(manager.getName())
                        .patientIds(manager.getPatientIds())
                        .build();
            } else {
                log.info("[login] 관리자(userId={}) 비밀번호 불일치", userId);
            }
        } else {
            log.info("[login] 관리자(userId={}) 정보 없음", userId);
        }
        log.info("[login] 인증 실패: userId={}", userId);
        // 인증 실패 시 null 반환
        return null;
    }

    @Override
    public int insertPatient(PatientDTO dto) {
        log.info("insertPatient Start! 입력값: {}", dto);
        String id = CmmUtil.nvl(dto.id());
        String pw = CmmUtil.nvl(dto.pw());
        String email = CmmUtil.nvl(dto.email());
        String name = CmmUtil.nvl(dto.name());
        String detectionAreaType = CmmUtil.nvl(dto.detectionAreaType());
        log.info("id : {}", id);
        log.info("pw : {}", pw);
        log.info("email : {}", email);
        log.info("name : {}", name);
        log.info("detectionAreaType : {}", detectionAreaType);
        Optional<PatientDocument> rEntity = patientRepository.findById(id);
        if (rEntity.isPresent()) {
            log.info("중복 아이디: {}", id);
            return 2;
        }
        PatientDocument entity = PatientDocument.builder()
                .id(id)
                .pw(pw)
                .email(email)
                .name(name)
                .managerIds(dto.managerIds())
                .build();
        patientRepository.save(entity);
        log.info("insertPatient 저장 결과: 성공");
        // detection_area 저장 로직 추가
        boolean hand = false, face = false, both = false;
        if ("hand".equalsIgnoreCase(detectionAreaType)) hand = true;
        else if ("face".equalsIgnoreCase(detectionAreaType)) face = true;
        else if ("both".equalsIgnoreCase(detectionAreaType)) both = true;
        DetectionAreaDocument detectionArea = DetectionAreaDocument.builder()
                .patientId(id)
                .hand(hand)
                .face(face)
                .both(both)
                .build();
        detectionAreaRepository.save(detectionArea);
        log.info("detection_area 저장 결과: patientId={}, hand={}, face={}, both={}", id, hand, face, both);
        return 1;
    }

    @Override
    public int insertManager(ManagerDTO dto) {
        log.info("insertManager Start! 입력값: {}", dto);
        String id = CmmUtil.nvl(dto.id());
        String pw = CmmUtil.nvl(dto.pw());
        String email = CmmUtil.nvl(dto.email());
        String name = CmmUtil.nvl(dto.name());
        log.info("id : {}", id);
        log.info("pw : {}", pw);
        log.info("email : {}", email);
        log.info("name : {}", name);
        Optional<ManagerDocument> rEntity = managerRepository.findById(id);
        if (rEntity.isPresent()) {
            log.info("중복 아이디: {}", id);
            return 2;
        }
        ManagerDocument entity = ManagerDocument.builder()
                .id(id)
                .pw(pw)
                .email(email)
                .name(name)
                .patientIds(dto.patientIds())
                .build();
        managerRepository.save(entity);
        boolean saved = managerRepository.findById(id).isPresent();
        log.info("insertManager 저장 결과: {}", saved ? "성공" : "실패");
        return saved ? 1 : 0;
    }

    @Override
    public PatientDTO getPatient(PatientDTO dto) {
        log.info("getPatient Start! 조회 아이디: {}", dto.id());
        String id = CmmUtil.nvl(dto.id());
        Optional<PatientDocument> entity = patientRepository.findById(id);
        if (entity.isPresent()) {
            PatientDocument e = entity.get();
            log.info("getPatient 조회 성공: {}", e);
            return PatientDTO.builder()
                    .id(e.getId())
                    .pw(e.getPw())
                    .email(e.getEmail())
                    .name(e.getName())
                    .managerIds(e.getManagerIds())
                    .build();
        }
        log.info("getPatient 조회 실패: 아이디 {} 없음", id);
        return null;
    }

    @Override
    public ManagerDTO getManager(ManagerDTO dto) {
        log.info("getManager Start! 조회 아이디: {}", dto.id());
        String id = CmmUtil.nvl(dto.id());
        Optional<ManagerDocument> entity = managerRepository.findById(id);
        if (entity.isPresent()) {
            ManagerDocument e = entity.get();
            log.info("getManager 조회 성공: {}", e);
            return new ManagerDTO(e.getId(), e.getPw(), e.getEmail(), e.getName(), e.getPatientIds());
        }
        log.info("getManager 조회 실패: 아이디 {} 없음", dto.id());
        return null;
    }
}

