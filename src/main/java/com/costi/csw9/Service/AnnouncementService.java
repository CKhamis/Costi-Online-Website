package com.costi.csw9.Service;

import com.costi.csw9.Model.Announcement;
import com.costi.csw9.Model.User;
import com.costi.csw9.Repository.AnnouncementRepository;
import com.costi.csw9.Util.LogicTools;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;


    public AnnouncementService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    public Announcement findById(Long id) throws Exception{
        Optional<Announcement> optionalAnnouncement = announcementRepository.findById(id);
        if(optionalAnnouncement.isPresent()){
            Announcement announcement = optionalAnnouncement.get();
            if(announcement.isEnable()){
                return announcement;
            }
            throw new AccessDeniedException(LogicTools.INVALID_PERMISSIONS_MESSAGE);

        }else{
            throw new NoSuchElementException("Announcement" + LogicTools.NOT_FOUND_MESSAGE);
        }
    }

    public List<Announcement> findAllAnnouncements(){
        return announcementRepository.findByEnable(true);
    }
}
