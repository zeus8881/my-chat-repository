package com.example.chatservice.mapper;

import com.example.chatservice.dto.MemberShipDTO;
import com.example.chatservice.dto.UserDTO;
import com.example.chatservice.model.MemberShip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberShipMapper {
    @Mapping(source = "memberShip.chat.id", target = "chatRoomId")
    @Mapping(source = "memberShip.userId", target = "userId")
    @Mapping(source = "memberShip.id", target = "id")
    @Mapping(source = "userDTO", target = "userDTO  ")
    MemberShipDTO toDTO(MemberShip memberShip, UserDTO userDTO);

    MemberShip toModel(MemberShipDTO memberShipDTO);
}
