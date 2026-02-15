package com.example.EcoBazaar_module2.service;

import com.example.EcoBazaar_module2.model.AuditLog;
import com.example.EcoBazaar_module2.model.User;
import com.example.EcoBazaar_module2.repository.AuditLogRepository;
import com.example.EcoBazaar_module2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void log(Long actorId, String action, String entityType, Long entityId, String metadata) {
        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new RuntimeException("Actor not found"));

        AuditLog log = new AuditLog();
        log.setActor(actor);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setMetadata(metadata);

        auditLogRepository.save(log);
    }

    public List<AuditLog> getLogsForEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }
}