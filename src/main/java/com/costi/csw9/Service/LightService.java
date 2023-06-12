package com.costi.csw9.Service;

import com.costi.csw9.Model.Light;
import com.costi.csw9.Model.LightLog;
import com.costi.csw9.Repository.LightLogRepository;
import com.costi.csw9.Repository.LightRepository;
import com.costi.csw9.Util.LogicTools;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LightService {
    private final LightRepository lightRepository;
    private final LightLogRepository lightLogRepository;
    private final int INTERVAL = 50000;
    public LightService(LightRepository lightRepository, LightLogRepository lightLogRepository) {
        this.lightRepository = lightRepository;
        this.lightLogRepository = lightLogRepository;
    }

    public List<Light> getEnabledLights(boolean isEnabled) {
        return lightRepository.findByIsEnabledOrderByDateAddedDesc(isEnabled);
    }

    public List<Light> getAllLights() {
        return lightRepository.findAllByOrderByDateAddedDesc();
    }

    @Scheduled(fixedRate = INTERVAL)
    public void updateCurrentStatus() {
        List<Light> lights = lightRepository.findAll();
        for (Light light : lights) {
            String currentStatus = light.getCurrentStatus();
            LightLog log = new LightLog(light, currentStatus);
            light.getLogs().add(log);

            lightLogRepository.save(log);
            lightRepository.save(light);
        }
    }

    public List<Light> getPublicLights(boolean isPublic) {
        return lightRepository.findByIsPublicOrderByDateAddedDesc(isPublic);
    }

    public List<Light> getFavoriteLights(boolean isFavorite) {
        return lightRepository.findByIsFavoriteOrderByDateAddedDesc(isFavorite);
    }

    public Light getLightById(Long id) throws Exception {
        try{
            return lightRepository.findById(id).get();
        }catch (Exception e){
            throw new Exception("Post" + LogicTools.NOT_FOUND_MESSAGE);
        }
    }

    public Light saveLight(Light light) {
        light.setLastModified(LocalDateTime.now());
        return lightRepository.save(light);
    }

    public void deleteLightById(Long id) {
        lightRepository.deleteById(id);
    }
}
