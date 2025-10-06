package com.example.chatservice.service.impl;

import com.example.chatservice.dto.ChatRoomDTO;
import com.example.chatservice.mapper.ChatRoomMapper;
import com.example.chatservice.model.ChatRoom;
import com.example.chatservice.model.MemberShip;
import com.example.chatservice.repository.ChatRoomRepository;
import com.example.chatservice.repository.MemberShipRepository;
import com.example.chatservice.service.ChatRoomService;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomMapper chatRoomMapper;
    private final ChatRoomRepository chatRoomRepository;
    private final Logger logger = Logger.getLogger(ChatRoomServiceImpl.class.getName());
    private final MemberShipRepository memberShipRepository;


    @Override
    public ResponseEntity<ChatRoomDTO> createChatRoom(ChatRoomDTO chatRoomDTO) {
        ChatRoom chatRoom = chatRoomMapper.toModel(chatRoomDTO);

        ChatRoom save = chatRoomRepository.save(chatRoom);
        ChatRoomDTO dto = chatRoomMapper.toDTO(save);

        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<ChatRoomDTO> getChatRoomInfo(Long id) {
        ChatRoom chatRoom = chatRoomRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat room not found"));

        ChatRoomDTO chatRoomDTO = chatRoomMapper.toDTO(chatRoom);
        return ResponseEntity.ok(chatRoomDTO);
    }

    @Override
    public ResponseEntity<List<ChatRoomDTO>> getAllRoomsByUserId(Long userId) {
        List<MemberShip> memberShip = memberShipRepository.getMemberShipByUserId(userId);

        List<Long> roomIds = memberShip.stream().map(MemberShip::getChatRoomId)
                .toList();
        List<ChatRoom> rooms = chatRoomRepository.findAllById(roomIds);

        List<ChatRoomDTO> chatRoomDTOS = rooms.stream().map(room -> new ChatRoomDTO(room.getId(), room.getName()))
                .toList();
        return ResponseEntity.ok(chatRoomDTOS);
    }

    @Override
    public void deleteChatRoom(Long id) {
        if (!chatRoomRepository.existsById(id)) {
            logger.warning("Chat room already exists");
        }
        chatRoomRepository.deleteById(id);
        logger.info("Chat room deleted successfully");

        ResponseEntity.noContent()
                .build();
    }
}
