package kopo.userservice.service.impl;

import kopo.userservice.dto.PatientDTO;
import kopo.userservice.dto.ManagerDTO;
import kopo.userservice.service.IUserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {
    @Override
    public int insertPatient(PatientDTO dto) {
        // TODO: 실제 구현
        return 0;
    }
    @Override
    public int insertManager(ManagerDTO dto) {
        // TODO: 실제 구현
        return 0;
    }
    @Override
    public PatientDTO getPatient(PatientDTO dto) throws Exception {
        // TODO: 실제 구현
        return null;
    }
    @Override
    public ManagerDTO getManager(ManagerDTO dto) throws Exception {
        // TODO: 실제 구현
        return null;
    }
    @Override
    public Object login(String userId, String password) {
        // TODO: 실제 구현
        return null;
    }
}

