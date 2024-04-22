package com.yvolabs.streamapi.mapper;

import com.yvolabs.streamapi.dto.MovieDto;
import com.yvolabs.streamapi.model.Movie;
import org.bson.types.ObjectId;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author Yvonne N
 */
@Mapper(componentModel = "spring")
public interface MovieMapper {
    MovieMapper INSTANCE = Mappers.getMapper(MovieMapper.class);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "id", target = "id", qualifiedByName = "objectIdToString")
    MovieDto movieToMovieDtoMapper(Movie movie);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "id", target = "id", qualifiedByName = "stringToObjectId")
    Movie movieDtoToMovieMapper(MovieDto movieDto);

    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
    )
    @Mapping(source = "id", target = "id", qualifiedByName = "stringToObjectId")
    Movie updateMovieDto(MovieDto movieDto, @MappingTarget Movie movie);


    @Named("objectIdToString")
    static String objectIdToString(ObjectId id) {
        return id.toString();
    }

    @Named("stringToObjectId")
    static ObjectId stringToObjectId(String id) {
        if (id != null && !id.isEmpty()) {
            return new ObjectId(id);
        }
        return null;
    }
}
