package com.costi.csw9.Repository;

import com.costi.csw9.Model.Announcement;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository {
    Announcement findById(Long id);
    List<Announcement> getByApproval(boolean enabled);
    List<Announcement> findAll();
    @Modifying
    void save(Announcement announcement);
    @Modifying
    void delete(Long id);
    @Modifying
    void enable(Long id, boolean enable);
}
