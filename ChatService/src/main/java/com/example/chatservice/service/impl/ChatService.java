package com.example.chatservice.service.impl;

import com.example.chatservice.client.MessageClient;
import com.example.chatservice.client.UserClient;
import com.example.chatservice.dto.MessageDTO;
import com.example.chatservice.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageClient messageClient;
    private final UserClient userClient;

    public void sendMessageToRoom(Long roomId, Long senderId, MessageDTO messageDTO) {
        UserDTO userDTO = userClient.getUserById(senderId);
        MessageDTO dtoWithSender = new MessageDTO(messageDTO.id(),
                messageDTO.roomId(),
                senderId,
                messageDTO.content(),
                messageDTO.createdAt(),
                userDTO.name());
        MessageDTO saved = messageClient.sendMessage(roomId, senderId, dtoWithSender);


        simpMessagingTemplate.convertAndSend("/topic/room" + roomId, saved);
    }

    public MessageDTO getMessageFromRoom(Long roomId, int page, int size) {
        return messageClient.getMessages(roomId, page, size);
    }
}
