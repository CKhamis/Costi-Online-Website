package com.costi.csw9.Repository;

import com.costi.csw9.Model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByEnable(boolean enable);

    @Modifying
    @Transactional
    @Query("UPDATE Announcement a SET a.enable = :enable WHERE a.id = :id")
    void setEnableById(@Param("id") Long id, @Param("enable") boolean enable);


}
