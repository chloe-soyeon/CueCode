package kopo.userservice.service.impl;

import kopo.userservice.model.PatientDocument;
import kopo.userservice.model.ManagerDocument;
import kopo.userservice.repository.PatientRepository;
import kopo.userservice.repository.ManagerRepository;
import kopo.userservice.service.PatientManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PatientManagerServiceImpl implements PatientManagerService {
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private ManagerRepository managerRepository;

    /** 보호자가 환자를 관리 목록에 추가 (양방향 관계 업데이트) */
    @Transactional
    public boolean addPatientToManager(String managerId, String patientId) {
        Optional<PatientDocument> patientOpt = patientRepository.findById(patientId);
        Optional<ManagerDocument> managerOpt = managerRepository.findById(managerId);
        if (patientOpt.isEmpty() || managerOpt.isEmpty()) return false;

        PatientDocument patient = patientOpt.get();
        ManagerDocument manager = managerOpt.get();

        // 환자 문서에 보호자 ID 추가
        List<String> managerIds = patient.getManagerIds();
        if (managerIds == null) managerIds = new ArrayList<>();
        if (!managerIds.contains(managerId)) managerIds.add(managerId);
        patient.setManagerIds(managerIds);
        patientRepository.save(patient);

        // 보호자 문서에 환자 ID 추가
        List<String> patientIds = manager.getPatientIds();
        if (patientIds == null) patientIds = new ArrayList<>();
        if (!patientIds.contains(patientId)) patientIds.add(patientId);
        manager.setPatientIds(patientIds);
        managerRepository.save(manager);

        return true;
    }
}
