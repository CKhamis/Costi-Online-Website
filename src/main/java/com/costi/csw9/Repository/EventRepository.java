package com.costi.csw9.Repository;

import com.costi.csw9.Model.FriendEvent;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository {
    FriendEvent findById(Long id);
    List<FriendEvent> findByOrganizer(Long id);
    List<FriendEvent> findByInvited(Long id);
    List<FriendEvent> findByCategory(String category);
    List<FriendEvent> getByApproval(boolean isEnabled);
    List<FriendEvent> getByUniversal(boolean isUniversal);
    List<FriendEvent> findAll();
    @Modifying
    void save(FriendEvent event);
    @Modifying
    void enable(Long id, boolean enable);
}
