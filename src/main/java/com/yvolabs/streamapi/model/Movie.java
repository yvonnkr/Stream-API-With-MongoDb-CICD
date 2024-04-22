package com.yvolabs.streamapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

/**
 * @author Yvonne N
 */
@Document(collection = "movies")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Movie {
    @Id
    private ObjectId id;
    private String title;
    private String description;
    private String releaseDate;
    private List<String> genres;
    @DocumentReference
    private List<Review> reviewsIds;
}
