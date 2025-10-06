package com.example.messageservice.controller;

import com.example.messageservice.dto.MessageDTO;
import com.example.messageservice.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class    MessageController {
    public final MessageService messageService;

    @PostMapping("/{chatId}/send/{senderId}")
    public ResponseEntity<MessageDTO> sendMessage(@PathVariable("chatId") Long chatId,
                                                  @PathVariable("senderId") Long senderId,
                                                  @RequestBody MessageDTO messageDTO) {
        return messageService.createMessage(chatId, senderId, messageDTO);
    }

    @GetMapping("/by-id/{id}")
    public ResponseEntity<MessageDTO> getMessageById(@PathVariable Long id) {
        return messageService.getMessageById(id);
    }

    @GetMapping("/by-room/{roomId}")
    public ResponseEntity<MessageDTO> getMessageByChatId(@PathVariable Long roomId,
                                                         @RequestParam int page,
                                                         @RequestParam int size) {
        return messageService.getMessageByChatId(roomId, page, size);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMessageById(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MessageDTO> updateMessages(@PathVariable Long id,
                                                     @RequestParam String newContent) {
        return messageService.updateMessage(id, newContent);
    }
}
