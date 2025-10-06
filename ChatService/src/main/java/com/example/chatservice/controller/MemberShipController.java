package com.example.chatservice.controller;

import com.example.chatservice.dto.MemberShipDTO;
import com.example.chatservice.dto.UserDTO;
import com.example.chatservice.service.MemberShipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberShipController {
    private final MemberShipService memberShipService;

    @PostMapping("/create/{userId}/{roomId}")
    public ResponseEntity<MemberShipDTO> createMember(@PathVariable Long userId, @PathVariable Long roomId, @RequestBody MemberShipDTO memberShipDTO) {
        return memberShipService.addUserToRoom(userId, roomId, memberShipDTO);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<List<UserDTO>> getAllUsersInRoom(@PathVariable Long roomId) {
        return memberShipService.getAllUsersInRoom(roomId);
    }

    @DeleteMapping("/delete/{userId}/{roomId}")
    public ResponseEntity<Void> deleteUserInRoom(@PathVariable Long userId, @PathVariable Long roomId) {
        return memberShipService.deleteUserInRoom(userId, roomId);
    }

    @GetMapping("/isUserInRoom/{userId}/{roomId}")
    public boolean isUserInRoom(@PathVariable Long userId, @PathVariable Long roomId) {
        return memberShipService.isUserInRoom(userId, roomId);
    }
}
