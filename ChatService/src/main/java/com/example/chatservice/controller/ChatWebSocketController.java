package com.example.chatservice.controller;

import com.example.chatservice.dto.MessageDTO;
import com.example.chatservice.service.impl.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
    private final ChatService chatService;

    @MessageMapping("/chat.send")
    public void receiveMessage(MessageDTO messageDTO) {
        chatService.sendMessageToRoom(messageDTO.roomId(), messageDTO.senderId(), messageDTO);
    }

    @GetMapping("/chat/{chatId}")
    public MessageDTO getMessageFromRoom(@PathVariable Long chatId, @RequestParam int page, @RequestParam int size) {
        return chatService.getMessageFromRoom(chatId, page, size);
    }
}
