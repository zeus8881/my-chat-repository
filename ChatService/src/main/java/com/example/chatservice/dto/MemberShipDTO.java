package com.example.chatservice.dto;

public record MemberShipDTO(Long id,
                            Long chatRoomId,
                            Long userId,
                            String role,
                            UserDTO userDTO) {
}
