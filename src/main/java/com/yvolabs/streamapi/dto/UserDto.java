package com.yvolabs.streamapi.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author Yvonne N
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String id;

    @NotEmpty(message = "firstName is required")
    @Field(name = "firstname")
    private String firstName;

    @NotEmpty(message = "lastName is required")
    @Field(name = "lastname")
    private String lastName;

    private String email;

    private boolean enabled;

    @NotEmpty(message = "roles are required")
    private String roles; //space separated string
}
