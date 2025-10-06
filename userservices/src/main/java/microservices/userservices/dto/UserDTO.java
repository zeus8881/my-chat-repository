package microservices.userservices.dto;

import microservices.userservices.model.Role;

public record UserDTO(Long id,
                      String username,
                      String email,
                      String passwordHash,
                      Role role) {
}
