package com.costi.csw9.Service;

import com.costi.csw9.Model.Announcement;
import com.costi.csw9.Repository.AnnouncementRepository;
import com.costi.csw9.Util.LogicTools;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class COMTService {
    private final AnnouncementRepository announcementRepository;

    public COMTService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    /*
        Announcements
     */

    public Announcement findAnnouncementById(Long id) throws Exception{
        Optional<Announcement> optionalAnnouncement = announcementRepository.findById(id);

        // Check if announcement exists
        if(optionalAnnouncement.isPresent()){
            return optionalAnnouncement.get();
        }else{
            throw new Exception("Announcement" + LogicTools.NOT_FOUND_MESSAGE);
        }
    }

    public List<Announcement> findAllAnnouncements(){
        return announcementRepository.findAll();
    }

    public void saveAnnouncement(Announcement announcement){
        announcement.setDate(LocalDateTime.now());
        announcementRepository.save(announcement);
    }

    public void deleteAnnouncement(Long id){
        announcementRepository.deleteById(id);
    }

}
