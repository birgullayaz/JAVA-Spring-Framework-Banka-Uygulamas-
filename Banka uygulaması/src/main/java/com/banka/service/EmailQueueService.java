package com.banka.service;

import com.banka.dto.EmailTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class EmailQueueService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailQueueService.class);
    private final DelayQueue<EmailTask> emailQueue = new DelayQueue<>();
    private ExecutorService executorService;
    
    @Autowired
    private EmailService emailService;
    
    @PostConstruct
    public void init() {
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(this::processQueue);
        logger.info("Email Queue Service başlatıldı");
    }
    
    @PreDestroy
    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        logger.info("Email Queue Service durduruldu");
    }
    
    public void enqueueEmail(String to, String subject, String body, long delayMinutes) {
        EmailTask task = new EmailTask(to, subject, body, delayMinutes);
        emailQueue.offer(task);
        logger.info("Email kuyruğa eklendi: {} - {} dakika sonra gönderilecek", to, delayMinutes);
    }
    
    private void processQueue() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                EmailTask task = emailQueue.take();
                logger.info("Email gönderiliyor: {} (Deneme: {})", task.getTo(), task.getRetryCount() + 1);
                
                boolean success = emailService.sendEmail(task.getTo(), task.getSubject(), task.getBody());
                
                if (success) {
                    logger.info("Email başarıyla gönderildi: {}", task.getTo());
                } else {
                    logger.warn("Email gönderilemedi: {} - Retry kontrolü yapılıyor...", task.getTo());
                    
                    if (task.canRetry()) {
                        task.incrementRetryCount();
                        EmailTask retryTask = task.createRetryTask();
                        emailQueue.offer(retryTask);
                        logger.info("Email tekrar kuyruğa eklendi: {} - {} dakika sonra tekrar denenecek (Deneme: {})", 
                                   task.getTo(), EmailTask.getRetryDelayMinutes(), retryTask.getRetryCount());
                    } else {
                        logger.error("Email gönderilemedi ve maksimum retry sayısına ulaşıldı: {}", task.getTo());
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.info("Email queue işlemi kesildi");
                break;
            } catch (Exception e) {
                logger.error("Email gönderme hatası", e);
            }
        }
    }
    
    public int getQueueSize() {
        return emailQueue.size();
    }
}
