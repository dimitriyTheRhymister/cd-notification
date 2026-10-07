package ru.checkdev.notification.kafka;

/**
 * Константы имён Kafka-топиков сервиса notification.
 * Используются в @KafkaListener-классах для приёма событий.
 */
public final class KafkaTopics {

    private KafkaTopics() {
    }

    public static final String FEEDBACK_INTERVIEW = "notification.feedback.interview";
    public static final String WISHER_APPROVED = "notification.wisher.approved";
    public static final String WISHER_DISMISSED = "notification.wisher.dismissed";
    public static final String INTERVIEW_TOPIC = "notification.interview.topic";
    public static final String INTERVIEW_PARTICIPATE = "notification.interview.participate";
    public static final String INTERVIEW_CANCEL = "notification.interview.cancel";
    public static final String NEW_INTERVIEW = "notification.newInterview";
    public static final String MESSAGE = "notification.message";
}