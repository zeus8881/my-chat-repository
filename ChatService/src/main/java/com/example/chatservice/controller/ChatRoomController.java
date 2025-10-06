package com.example.chatservice.controller;

import com.example.chatservice.dto.ChatRoomDTO;
import com.example.chatservice.model.ChatRoom;
import com.example.chatservice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/room")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/create")
    public ResponseEntity<ChatRoomDTO> createRoom(@RequestBody ChatRoomDTO chatRoomDTO) {
        return chatRoomService.createChatRoom(chatRoomDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatRoomDTO> getChatRoomInfo(@PathVariable Long id) {
        return chatRoomService.getChatRoomInfo(id);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<List<ChatRoomDTO>> getAllRoomsByUser(@PathVariable Long id) {
        return chatRoomService.getAllRoomsByUserId(id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUserInRoom(@PathVariable Long id) {
        chatRoomService.deleteChatRoom(id);
        return ResponseEntity.noContent().build();
    }

}
