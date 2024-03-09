package com.costi.csw9.Service;

import com.costi.csw9.Model.DTO.UserLightRequest;
import com.costi.csw9.Model.Light;
import com.costi.csw9.Model.LightLog;
import com.costi.csw9.Repository.LightLogRepository;
import com.costi.csw9.Repository.LightRepository;
import org.springframework.stereotype.Service;

import java.rmi.ConnectIOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class LightService {
    private final LightRepository lights;
    private final LightLogRepository lightLogRepository;

    public LightService(LightRepository lights, LightLogRepository lightLogRepository) {
        this.lights = lights;
        this.lightLogRepository = lightLogRepository;
    }

    public List<Light> findAllLights(){
        return lights.findAllByIsEnabledTrueAndIsPublicTrueOrderByDateAddedDesc();
    }

    public String syncUp(Light light){
        String currentStatus = light.sendLightRequest();
        LightLog log = new LightLog(light, currentStatus);
        light.getLogs().add(log);

        lightLogRepository.save(log);
        lights.save(light);

        return currentStatus;
    }

    public String saveLight(UserLightRequest request) throws Exception{
        // Check if valid
        if(request.getId() == null){
            throw new NullPointerException("ID field cannot be null");
        }

        // Check if exists
        Optional<Light> optionalLight = lights.findById(request.getId());

        if(optionalLight.isEmpty()){
            throw new NoSuchElementException("Light could not be found");
        }

        // Check if editable by public
        Light originalLight = optionalLight.get();

        if(!originalLight.isEnabled() || !originalLight.isPublic()){
            throw new NoSuchElementException("Light could not be found");
        }

        // Transfer fields
        originalLight.setColor(request.getColor());
        originalLight.setPattern(request.getPattern());
        originalLight.setLastModified(LocalDateTime.now());

        //Upload data to light
        String status = syncUp(originalLight);

        if(status.charAt(0) != 'C'){
            throw new ConnectIOException("Error sending data: " + status);
        }

        return status;
    }
}
