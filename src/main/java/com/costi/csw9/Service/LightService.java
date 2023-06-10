package com.costi.csw9.Service;

import com.costi.csw9.Model.Light;
import com.costi.csw9.Repository.LightRepository;
import com.costi.csw9.Util.LogicTools;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LightService {
    private final LightRepository lightRepository;
    public LightService(LightRepository lightRepository) {
        this.lightRepository = lightRepository;
    }

    public List<Light> getEnabledLights(boolean isEnabled) {
        return lightRepository.findByIsEnabledOrderByDateAddedDesc(isEnabled);
    }

    public List<Light> getAllLights() {
        return lightRepository.findAllByOrderByDateAddedDesc();
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