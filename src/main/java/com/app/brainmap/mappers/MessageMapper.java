package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.MessageDto;
import com.app.brainmap.domain.dto.PrivateMessageDto;
import com.app.brainmap.domain.dto.UserChatDto;
import com.app.brainmap.domain.entities.Message;
import com.app.brainmap.domain.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapper {
    @Mapping(target = "senderId", source = "senderId.id")
    @Mapping(target = "receiverId", source = "receiverId.id")
    @Mapping(target = "message", source = "content")
    MessageDto toDto(Message message);

    @Mapping(target = "senderId", source = "senderId.id")
    @Mapping(target = "receiverId", source = "receiverId.id")
    @Mapping(target = "message", source = "content")
    PrivateMessageDto toPrivateMessageDto(Message message);

    UserChatDto toUserChatDto(User user);
}
