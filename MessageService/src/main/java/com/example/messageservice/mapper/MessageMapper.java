package com.example.messageservice.mapper;

import com.example.messageservice.dto.ChatRoomDTO;
import com.example.messageservice.dto.MessageDTO;
import com.example.messageservice.dto.UserDTO;
import com.example.messageservice.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "id", source = "message.id")
    @Mapping(target = "status", source = "message.status")
    @Mapping(target = "content", source = "message.content")
    @Mapping(target = "senderId", source = "message.senderId")
    @Mapping(target = "roomId", source = "message.roomId")
    @Mapping(target = "senderName", source = "userDTO.username")
    @Mapping(target = "chatRoomDTO", source = "chatRoomDTO")
    MessageDTO toDTO(Message message, UserDTO userDTO, ChatRoomDTO chatRoomDTO);

    Message toModel(MessageDTO messageDTO);
}
