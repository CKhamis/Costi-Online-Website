package com.costi.csw9.Service;

import com.costi.csw9.Model.Announcement;
import com.costi.csw9.Repository.AnnouncementRepository;
import com.costi.csw9.Util.LogicTools;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AnnouncementService {
    private final AnnouncementRepository repository;


    public AnnouncementService(AnnouncementRepository repository) {
        this.repository = repository;
    }

    public Announcement findById(Long id) throws Exception{
        Optional<Announcement> optionalAnnouncement = repository.findById(id);
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
        return repository.findByEnable(true);
    }
}
