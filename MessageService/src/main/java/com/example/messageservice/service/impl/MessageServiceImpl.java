package com.example.messageservice.service.impl;

import com.example.messageservice.client.ChatRoomClient;
import com.example.messageservice.client.UserClient;
import com.example.messageservice.dto.ChatRoomDTO;
import com.example.messageservice.dto.MessageDTO;
import com.example.messageservice.dto.UserDTO;
import com.example.messageservice.mapper.MessageMapper;
import com.example.messageservice.model.Message;
import com.example.messageservice.model.Status;
import com.example.messageservice.repository.MessageRepository;
import com.example.messageservice.service.MessageService;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final UserClient userClient;
    private final ChatRoomClient chatRoomClient;
    private static final Logger logger = Logger.getLogger(MessageServiceImpl.class.getName());
    private final RedisTemplate<String, MessageDTO> redisTemplate;

    @Override
    public ResponseEntity<MessageDTO> createMessage(Long chatId, Long senderId, MessageDTO messageDTO) {
        if (chatId == null || senderId == null) {
            logger.warning("Error. Id is null!");

            return ResponseEntity.badRequest().build();
        }
        Message message = messageMapper.toModel(messageDTO);

        message.setSenderId(senderId);
        message.setRoomId(chatId);
        message.setStatus(Status.DELIVERED);

        UserDTO userDTO = userClient.getUser(senderId);
        ChatRoomDTO chatRoomDTO = chatRoomClient.getRoomById(chatId);

        Message save = messageRepository.save(message);
        MessageDTO dto = new MessageDTO(save.getId(),
                save.getRoomId(),
                save.getSenderId(),
                save.getContent(),
                save.getStatus(),
                userDTO,
                chatRoomDTO,
                userDTO.username());

        redisTemplate.opsForValue().set(getCachedKey(message.getId()), dto);
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<MessageDTO> getMessageById(Long id) {
        String key = getCachedKey(id);
        MessageDTO cachedDTO = redisTemplate.opsForValue().get(key);

        if (cachedDTO != null) {
            return ResponseEntity.ok(cachedDTO);
        }

        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));

        message.setStatus(Status.READ);
        UserDTO userDTO = userClient.getUser(message.getSenderId());
        ChatRoomDTO chatRoomDTO = chatRoomClient.getRoomById(message.getRoomId());

        MessageDTO dto = messageMapper.toDTO(message, userDTO, chatRoomDTO);
        return ResponseEntity.ok(dto);

    }

    @Override
    public ResponseEntity<MessageDTO> getMessageByChatId(Long chatId, Integer limit, Integer offset) {
        Message message = new Message();
        Pageable pageable = PageRequest.of(limit, offset, Sort.by("createdAt").descending());
        messageRepository.findByRoomIdAndCreatedAt(chatId, message.getCreatedAt(), pageable);

        Message save = messageRepository.save(message);

        UserDTO userDTO = userClient.getUser(message.getSenderId());
        ChatRoomDTO chatRoomDTO = chatRoomClient.getRoomById(message.getRoomId());

        MessageDTO messageDTO = messageMapper.toDTO(save, userDTO, chatRoomDTO);
        redisTemplate.opsForValue().set(getCachedKey(save.getId()), messageDTO);

        return ResponseEntity.ok(messageDTO);
    }

    @Override
    public ResponseEntity<Void> deleteMessage(Long id) {
        if (!messageRepository.existsById(id)) {
            logger.warning("Message already exists");
        }
        messageRepository.deleteById(id);

        return ResponseEntity.noContent()
                .build();
    }

    @Override
    public ResponseEntity<MessageDTO> updateMessage(Long id, String newContent) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
        message.setContent(newContent);

        UserDTO userDTO = userClient.getUser(message.getSenderId());
        ChatRoomDTO chatRoomDTO = chatRoomClient.getRoomById(message.getRoomId());

        Message save = messageRepository.save(message);
        MessageDTO dto = messageMapper.toDTO(save, userDTO, chatRoomDTO);

        return ResponseEntity.ok(dto);
    }

    private String getCachedKey(Long messageId) {
        return "message:" + messageId;
    }
}
