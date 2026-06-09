package com.proyecto.volticfit.repository;

import com.proyecto.volticfit.entity.UserNotification;
import com.proyecto.volticfit.entity.UserNotificationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, UserNotificationId> {

    List<UserNotification> findByUserIdUser(Long userId);

    void deleteByNotificationIdAndUserIdUser(Long notificationId, Long userId);
}
