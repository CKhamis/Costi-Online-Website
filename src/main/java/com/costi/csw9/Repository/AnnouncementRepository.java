package com.costi.csw9.Repository;

import com.costi.csw9.Model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnnouncementRepository{

    List<Announcement> findByEnable(boolean enable);

    List<Announcement> findAll();

    Optional<Announcement> findById(Long id);

    void save(Announcement announcement);

    void deleteById(Long id);
}
