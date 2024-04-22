package com.yvolabs.streamapi.repository;

import com.yvolabs.streamapi.model.Movie;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Yvonne N
 */
public interface MovieRepository extends MongoRepository<Movie, ObjectId> {
}
