package se.fulkopinglibraryweb.security.audit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class AuditLogger {
    private static final Logger logger = Logger.getLogger("SecurityAudit");
    private static final BlockingQueue<AuditEvent> eventQueue = new LinkedBlockingQueue<>();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static volatile boolean isRunning = true;
    
    static {
        try {
            FileHandler fileHandler = new FileHandler("security_audit.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            
            Thread processingThread = new Thread(() -> processEvents());
            processingThread.setDaemon(true);
            processingThread.start();
        } catch (Exception e) {
            logger.severe("Failed to initialize audit logger: " + e.getMessage());
        }
    }
    
    public static void logEvent(String userId, String action, String details) {
        try {
            AuditEvent event = new AuditEvent(userId, action, details);
            eventQueue.offer(event);
        } catch (Exception e) {
            logger.severe("Failed to log audit event: " + e.getMessage());
        }
    }
    
    private static void processEvents() {
        while (isRunning) {
            try {
                AuditEvent event = eventQueue.take();
                String logMessage = String.format("%s | User: %s | Action: %s | Details: %s",
                    LocalDateTime.now().format(formatter),
                    event.userId,
                    event.action,
                    event.details);
                logger.info(logMessage);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private static class AuditEvent {
        private final String userId;
        private final String action;
        private final String details;
        private final LocalDateTime timestamp;
        
        public AuditEvent(String userId, String action, String details) {
            this.userId = userId;
            this.action = action;
            this.details = details;
            this.timestamp = LocalDateTime.now();
        }
    }
    
    public static void shutdown() {
        isRunning = false;
    }
}