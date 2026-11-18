package com.checkticket.backend.repository;

import com.checkticket.backend.entity.NotificationLog;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface NotificationLogRepository extends CrudRepository<NotificationLog, Long> {

    List<NotificationLog> findByAlert_User_Id(Long userId);
}
