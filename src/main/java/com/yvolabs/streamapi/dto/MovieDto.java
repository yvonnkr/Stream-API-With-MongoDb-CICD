package com.yvolabs.streamapi.dto;

import com.yvolabs.streamapi.annotations.CreateValidationGroup;
import com.yvolabs.streamapi.annotations.UpdateValidationGroup;
import com.yvolabs.streamapi.annotations.ValidateUpdateFieldNotEmpty;
import com.yvolabs.streamapi.model.Review;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Yvonne N
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieDto {

    private String id;

    @NotEmpty(message = "title is required", groups = {CreateValidationGroup.class})
    @Size(min = 3, message = "title length must be at least 3", groups = {CreateValidationGroup.class})
    @ValidateUpdateFieldNotEmpty(
            message = "title is required and length must be at least 3, alternatively please remove this field",
            groups = {UpdateValidationGroup.class})
    private String title;

    @NotEmpty(message = "description is required", groups = {CreateValidationGroup.class})
    @Size(min = 3, message = "description length must be at least 3", groups = {CreateValidationGroup.class})
    @ValidateUpdateFieldNotEmpty(
            message = "description is required and length must be at least 3, alternatively please remove this field",
            groups = {UpdateValidationGroup.class})
    private String description;

    @NotEmpty(message = "releaseDate is required", groups = {CreateValidationGroup.class})
    @Size(min = 3, message = "releaseDate length must be at least 3", groups = {CreateValidationGroup.class})
    @ValidateUpdateFieldNotEmpty(
            message = "releaseDate is required and length must be at least 3, alternatively please remove this field",
            groups = {UpdateValidationGroup.class})
    private String releaseDate;

    private List<String> genres;

    private List<Review> reviewsIds;


}
