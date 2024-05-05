package com.yvolabs.streamapi.mapper;

import com.yvolabs.streamapi.dto.UserDto;
import com.yvolabs.streamapi.model.StreamUser;
import org.bson.types.ObjectId;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author Yvonne N
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "id", target = "id", qualifiedByName = "objectIdToString")
    UserDto userToUserDto(StreamUser user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "id", target = "id", qualifiedByName = "stringToObjectId")
    StreamUser userDtoToStreamUser(UserDto userDto);

    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
    )
    @Mapping(source = "id", target = "id", qualifiedByName = "stringToObjectId")
    StreamUser updateUserDto(UserDto userDto, @MappingTarget StreamUser user);

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
