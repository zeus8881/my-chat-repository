package com.example.chatservice.service.impl;

import com.example.chatservice.client.UserClient;
import com.example.chatservice.dto.ChatRoomDTO;
import com.example.chatservice.dto.MemberShipDTO;
import com.example.chatservice.dto.UserDTO;
import com.example.chatservice.mapper.ChatRoomMapper;
import com.example.chatservice.mapper.MemberShipMapper;
import com.example.chatservice.model.ChatRoom;
import com.example.chatservice.model.MemberShip;
import com.example.chatservice.repository.ChatRoomRepository;
import com.example.chatservice.repository.MemberShipRepository;
import com.example.chatservice.service.MemberShipService;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.json.HTTP;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class MemberShipServiceImpl implements MemberShipService {
    private final MemberShipRepository memberShipRepository;
    private final MemberShipMapper memberShipMapper;
    private static final Logger logger = Logger.getLogger(MemberShipServiceImpl.class.getName());
    private final UserClient userClient;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMapper chatRoomMapper;

    @Override
    public ResponseEntity<MemberShipDTO> addUserToRoom(Long senderId, Long roomId, MemberShipDTO memberShipDTO) {
        if (senderId == null || roomId == null) {
            logger.warning("Error");

            return ResponseEntity.badRequest().build();
        }

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        chatRoomMapper.toDTO(chatRoom);

        MemberShip memberShip = memberShipMapper.toModel(memberShipDTO);

        memberShip.setChat(chatRoom);
        memberShip.setUserId(senderId);
        memberShip.setChatRoomId(roomId);

        logger.info("Member created!");

        UserDTO userDTO = userClient.getUserById(memberShip.getUserId());
        MemberShip save = memberShipRepository.save(memberShip);

        MemberShipDTO dto = memberShipMapper.toDTO(save, userDTO);

        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<List<UserDTO>> getAllUsersInRoom(Long roomId) {
        if (roomId == null) {
            logger.warning("Room is null");

            return ResponseEntity.noContent().build();
        }
        List<MemberShip> memberShips = memberShipRepository.findAllByChatRoomId(roomId);

        List<UserDTO> users = memberShips.stream()
                .map(m -> userClient.getUserById(m.getUserId()))
                .toList();
        return ResponseEntity.ok(users);
    }

    @Override
    public ResponseEntity<Void> deleteUserInRoom(Long userId, Long roomId) {
        if (roomId == null || userId == null) {
            logger.warning("Room or user id is null");

            return ResponseEntity.noContent().build();
        }
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        memberShipRepository.deleteMemberShipByUserId(userId);
        logger.info("User deleted in room " + chatRoom.getId());

        return ResponseEntity.noContent().build();
    }

    @Override
    public boolean isUserInRoom(Long userId, Long roomId) {
        return memberShipRepository.existsMemberShipByUserIdAndChatRoomId(userId, roomId);
    }
}
