package com.costi.csw9.Model;

import com.costi.csw9.Model.DTO.AccountNotificationRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name="account_notification")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private String notificationType;

    @Column(nullable = false)
    private LocalDateTime dateCreated = LocalDateTime.now();

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;

    public AccountNotification(String title, String body, String notificationType, User user) {
        this.title = title;
        this.body = body;
        this.notificationType = notificationType;
        this.user = user;
    }

    public AccountNotification(AccountNotificationRequest request, User user){
        this.title = request.getTitle();
        this.body = request.getBody();
        this.notificationType = request.getNotificationType();
        this.user = user;
    }

    public Long getDestinationId(){
        return this.user.getId();
    }

    public String getTimeSinceCreated() {
        String unit = "";
        LocalDateTime now = LocalDateTime.now();
        long diff;
        if((diff = ChronoUnit.SECONDS.between(dateCreated,now)) < 60){
            unit = "s";
        } else if ((diff = ChronoUnit.MINUTES.between(dateCreated,now)) < 60) {
            unit = "m";
        } else if ((diff = ChronoUnit.HOURS.between(dateCreated,now)) < 24) {
            unit = "h";
        } else if ((diff = ChronoUnit.DAYS.between(dateCreated,now)) < 30) {
            unit = "d";
        } else if ((diff = ChronoUnit.MONTHS.between(dateCreated,now)) < 12) {
            unit = "mo";
        } else{
            diff = ChronoUnit.YEARS.between(dateCreated,now);
        }
        return String.format("%d%s",diff,unit);
    }
}
