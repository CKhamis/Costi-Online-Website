package com.costi.csw9.Service;

import com.costi.csw9.Model.Light;
import com.costi.csw9.Model.LightLog;
import com.costi.csw9.Repository.LightLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LightLogService {
    private final LightLogRepository lightLogRepository;

    public LightLogService(LightLogRepository lightLogRepository) {
        this.lightLogRepository = lightLogRepository;
    }

    public List<LightLog> findAllByLightOrderByDateCreated(Light light) {
        return lightLogRepository.findAllByLightOrderByDateCreated(light);
    }

    public List<LightLog> findByIpOrderByDateCreated(String ip) {
        return lightLogRepository.findByIpOrderByDateCreated(ip);
    }
}
