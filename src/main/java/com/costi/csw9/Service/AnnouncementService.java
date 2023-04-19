package com.costi.csw9.Service;

import com.costi.csw9.Model.Announcement;
import com.costi.csw9.Model.User;
import com.costi.csw9.Repository.AnnouncementRepository;
import com.costi.csw9.Util.LogicTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;


    public AnnouncementService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    public Announcement findById(Long id) throws Exception{
        Optional<Announcement> optionalAnnouncement = announcementRepository.findById(id);

        // Check if announcement exists
        if(optionalAnnouncement.isPresent()){
            return optionalAnnouncement.get();

        }else{
            throw new Exception("Announcement" + LogicTools.NOT_FOUND_MESSAGE);
        }
    }

    public List<Announcement> findByApproval(boolean enabled){
        return announcementRepository.findByEnable(enabled);
    }

    public List<Announcement> findAll(){
        return announcementRepository.findAll();
    }

    public void save(Announcement announcement, User current) throws Exception {
        //Check if right permissions
        if(current.isAdmin() || current.isOwner()){
            announcement.setDate(LocalDateTime.now());
            announcementRepository.save(announcement);
        }else{
            throw new Exception(LogicTools.INVALID_PERMISSIONS_MESSAGE);
        }
    }

    public void delete(Long id, User current) throws Exception{
        //Check if right permissions
        if(current.isAdmin() || current.isOwner()){
            announcementRepository.deleteById(id);
        }else{
            throw new Exception(LogicTools.INVALID_PERMISSIONS_MESSAGE);
        }
    }

    public void enable(Long id, boolean enable, User current) throws Exception{
        Optional<Announcement> optionalAnnouncement = announcementRepository.findById(id);
        // Check if announcement exists
        if(optionalAnnouncement.isPresent()){
            Announcement announcement = optionalAnnouncement.get();

            //Check if right permissions
            if(current.isAdmin() || current.isOwner()){
                announcementRepository.setEnableById(announcement.getId(), enable);
            }else{
                throw new Exception(LogicTools.INVALID_PERMISSIONS_MESSAGE);
            }

        }else {
            throw new Exception("Announcement" + LogicTools.NOT_FOUND_MESSAGE);
        }
    }
}
