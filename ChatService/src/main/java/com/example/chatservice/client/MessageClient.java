package com.example.chatservice.client;

import com.example.chatservice.dto.MessageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "messageservice", url = "http://localhost:8091/messages")
public interface MessageClient {
    @PostMapping("/{chatId}/send/{senderId}")
    MessageDTO sendMessage(@PathVariable("chatId") Long chatId,
                           @PathVariable("senderId") Long senderId,
                           @RequestBody MessageDTO messageDTO);

    @GetMapping("/{chatId}")
    MessageDTO getMessages(@PathVariable Long chatId,
                           @RequestParam int page,
                           @RequestParam int size);
}
