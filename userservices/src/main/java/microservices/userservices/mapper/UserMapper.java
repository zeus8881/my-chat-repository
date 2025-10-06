package microservices.userservices.mapper;

import microservices.userservices.dto.UserDTO;
import microservices.userservices.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "role", source = "user.role")
    UserDTO toDTO(User user);

    User toModel(UserDTO userDTO);
}
