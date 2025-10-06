package com.example.chatservice.dto;

import java.time.LocalDateTime;

public record MessageDTO(Long id,
                         Long roomId,
                         Long senderId,
                         String content,
                         LocalDateTime createdAt,
                         String senderName) {
}
