package com.yvolabs.streamapi.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author Yvonne N
 */
@Document(collection = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StreamUser {
    @Id
    private ObjectId id;

    @NotEmpty(message = "firstName is required")
    @Field(name = "firstname")
    private String firstName;

    @NotEmpty(message = "lastName is required")
    @Field(name = "lastname")
    private String lastName;

    @NotEmpty(message = "email is required")
    @Email(message = "please provide a valid email")
    private String email;

    @Size(min = 6, message = "password should be at least 6 characters long")
    private String password;

    private boolean enabled;

    @NotEmpty(message = "roles are required")
    private String roles; //comma separated string
}
