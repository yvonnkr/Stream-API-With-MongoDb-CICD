package com.yvolabs.streamapi.converter;

import com.yvolabs.streamapi.dto.MovieDto;
import com.yvolabs.streamapi.model.Movie;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author Yvonne N
 * <p>
 * Refactored to use mapstruct mapper instead
 */
@Component
public class MovieDtoToMovieConverter implements Converter<MovieDto, Movie> {
    @Override
    public Movie convert(MovieDto source) {
        return Movie.builder()
                .title(source.getTitle())
                .description(source.getDescription())
                .releaseDate(source.getReleaseDate())
                .genres(source.getGenres())
                .build();
    }
}
