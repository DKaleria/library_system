package com.example.identityservice.usecaseses.mapper;

import com.example.identityservice.database.entity.User;
import com.example.sharedservice.event.AuthUserGotEvent;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface AuthUserMapper {
    @Mapping(target = "birthDate", ignore = true )
    @Mapping(target = "firstname", ignore = true )
    @Mapping(target = "lastname", ignore = true )
    @Mapping(target = "password", ignore = true )
    AuthUserGotEvent userToAuthUserGotEvent(User user);

    @Mapping(target = "birthDate", ignore = true )
    @Mapping(target = "firstname", ignore = true )
    @Mapping(target = "lastname", ignore = true )
    @Mapping(target = "password", ignore = true )
    User authUserGotEventToUser(AuthUserGotEvent authUserGotEvent);
}
