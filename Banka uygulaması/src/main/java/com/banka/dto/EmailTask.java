package com.banka.dto;

import java.time.LocalDateTime;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class EmailTask implements Delayed {
    private final String to;
    private final String subject;
    private final String body;
    private final LocalDateTime createdTime;
    private final long delayMinutes;
    private int retryCount;
    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY_MINUTES = 3;

    public EmailTask(String to, String subject, String body, long delayMinutes) {
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.createdTime = LocalDateTime.now();
        this.delayMinutes = delayMinutes;
        this.retryCount = 0;
    }

    public EmailTask(String to, String subject, String body, long delayMinutes, int retryCount) {
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.createdTime = LocalDateTime.now();
        this.delayMinutes = delayMinutes;
        this.retryCount = retryCount;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long delay = TimeUnit.MINUTES.toMillis(delayMinutes);
        return unit.convert(delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return Long.compare(this.getDelay(TimeUnit.MILLISECONDS), 
                           o.getDelay(TimeUnit.MILLISECONDS));
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public boolean canRetry() {
        return retryCount < MAX_RETRIES;
    }

    public EmailTask createRetryTask() {
        return new EmailTask(to, subject, body, RETRY_DELAY_MINUTES, retryCount + 1);
    }

    public static long getRetryDelayMinutes() {
        return RETRY_DELAY_MINUTES;
    }
}
