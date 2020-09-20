package cn.nihility.boot.redis;

public interface PublishService {
    void publish(String topicName, String message);
}
