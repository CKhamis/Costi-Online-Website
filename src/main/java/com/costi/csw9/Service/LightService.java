package com.costi.csw9.Service;

import com.costi.csw9.Model.Light;
import com.costi.csw9.Model.LightLog;
import com.costi.csw9.Repository.LightLogRepository;
import com.costi.csw9.Repository.LightRepository;
import com.costi.csw9.Util.LogicTools;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.rmi.ConnectIOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class LightService {
    private final LightRepository lightRepository;
    private final LightLogRepository lightLogRepository;

    public LightService(LightRepository lightRepository, LightLogRepository lightLogRepository) {
        this.lightRepository = lightRepository;
        this.lightLogRepository = lightLogRepository;
    }

    public List<Light> findAllLights(){
        return lightRepository.findAllByIsEnabledTrueAndIsPublicTrueOrderByDateAddedDesc();
    }

    public String syncUp(Light light){
        String currentStatus = light.sendLightRequest();
        LightLog log = new LightLog(light, currentStatus);
        light.getLogs().add(log);

        lightLogRepository.save(log);
        lightRepository.save(light);

        return currentStatus;
    }

    public String saveLight(Light light) throws Exception{
        // Check if valid
        if(light.getId() == null){
            throw new NullPointerException("ID field cannot be null");
        }

        // Check if exists
        Optional<Light> optionalLight = lightRepository.findById(light.getId());

        if(optionalLight.isEmpty()){
            throw new NoSuchElementException("Light could not be found");
        }

        // Check if editable by public
        Light originalLight = optionalLight.get();

        if(!originalLight.isEnabled() || !originalLight.isPublic()){
            throw new NoSuchElementException("Light could not be found");
        }

        // Transfer fields
        originalLight.setColor(light.getColor());
        originalLight.setPattern(light.getPattern());
        originalLight.setLastModified(LocalDateTime.now());

        //Upload data to light
        String status = syncUp(originalLight);

        if(status.charAt(0) != 'C'){
            throw new ConnectIOException("Error sending data: " + status);
        }

        return status;
    }
}
