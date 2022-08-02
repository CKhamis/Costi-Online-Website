package com.costi.csw9.Service;

import com.costi.csw9.Model.Announcement;
import com.costi.csw9.Repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

public class AnnouncementService {
    @Autowired
    private final AnnouncementRepository announcementRepository;


    public AnnouncementService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    public Announcement findById(Long id){
        return announcementRepository.findById(id);
    }

    public List<Announcement> getByApproval(boolean enabled){
        return announcementRepository.getByApproval(enabled);
    }

    public List<Announcement> findAll(){
        return announcementRepository.findAll();
    }

    public void save(Announcement announcement){
        announcement.setDate(LocalDateTime.now());
        announcementRepository.save(announcement);
    }

    public void delete(Announcement announcement){
        announcementRepository.delete(announcement.getId());
    }

    public void enable(Announcement announcement, boolean enable){
        announcementRepository.enable(announcement.getId(), enable);
    }
}
