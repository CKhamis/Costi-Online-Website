package com.costi.csw9.Service;

import com.costi.csw9.Model.FriendEvent;
import com.costi.csw9.Repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendEventService {
    @Autowired
    EventRepository eventRepository;

    public FriendEventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    FriendEvent findById(Long id){
        return eventRepository.findById(id);
    }
    List<FriendEvent> findByOrganizer(Long id){
        return eventRepository.findByOrganizer(id);
    }
    List<FriendEvent> findByInvited(Long id){
        return eventRepository.findByInvited(id);
    }
    List<FriendEvent> findByCategory(String category){
        return eventRepository.findByCategory(category);
    }
    List<FriendEvent> getByApproval(boolean isEnabled){
        return eventRepository.getByApproval(isEnabled);
    }
    List<FriendEvent> getByUniversal(boolean isUniversal){
        return eventRepository.getByUniversal(isUniversal);
    }
    List<FriendEvent> findAll(){
        return eventRepository.findAll();
    }
}
