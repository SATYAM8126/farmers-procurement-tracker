package com.sih.procurement.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sih.procurement.dto.QueueEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueEventListener implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody());
            QueueEvent event = objectMapper.readValue(json, QueueEvent.class);

            // This is the line that actually pushes the update to every
            // farmer/admin browser subscribed to this centre's channel
            messagingTemplate.convertAndSend(
                    "/topic/queue/" + event.getCentreId(),
                    event
            );
        } catch (Exception e) {
            log.error("Failed to process queue event from Redis", e);
        }
    }
}
