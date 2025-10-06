package com.example.messageservice.dto;

import com.example.messageservice.model.Status;

public record MessageDTO(Long id,
                         Long roomId,
                         Long senderId,
                         String content,
                         Status status,
                         UserDTO userDTO,
                         ChatRoomDTO chatRoomDTO,
                         String senderName) {
}
