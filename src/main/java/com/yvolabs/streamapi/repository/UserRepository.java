package com.yvolabs.streamapi.repository;

import com.yvolabs.streamapi.model.StreamUser;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * @author Yvonne N
 */
public interface UserRepository extends MongoRepository<StreamUser, ObjectId> {
    Optional<StreamUser> findByEmail(String email);
}
