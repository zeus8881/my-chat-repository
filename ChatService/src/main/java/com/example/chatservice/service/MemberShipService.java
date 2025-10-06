package com.example.chatservice.service;

import com.example.chatservice.dto.MemberShipDTO;
import com.example.chatservice.dto.UserDTO;
import com.example.chatservice.model.MemberShip;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MemberShipService {
    ResponseEntity<MemberShipDTO> addUserToRoom(Long userId, Long roomId, MemberShipDTO memberShipDTO);

    ResponseEntity<List<UserDTO>> getAllUsersInRoom(Long roomId);

    ResponseEntity<Void> deleteUserInRoom(Long userId, Long roomId);

    boolean isUserInRoom(Long userId, Long roomId);
}
