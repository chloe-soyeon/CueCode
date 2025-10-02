package kopo.motionservice.service;

import kopo.motionservice.dto.MotionRecordRequestDTO;

public interface IMotionService {

    /**
     * 녹화된 동작 데이터를 DB에 저장하는 메서드
     * @param requestDTO 프론트엔드로부터 받은 동작 데이터 묶음
     */
    void saveRecordedMotion(MotionRecordRequestDTO requestDTO);

}
