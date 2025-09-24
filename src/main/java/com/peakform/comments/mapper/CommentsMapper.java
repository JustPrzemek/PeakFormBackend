package com.peakform.comments.mapper;

import com.peakform.comments.dto.CommentsDTO;
import com.peakform.comments.dto.ResponseCommentDTO;
import com.peakform.comments.model.Comments;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentsMapper {

    @Mapping(source = "id", target = "commentId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "content", target = "content")
    CommentsDTO toCommentDTO(Comments comments);

    @Mapping(source = "id", target = "commentId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "user.profileImageUrl", target = "profileImageUrl")
    ResponseCommentDTO toResponseCommentDTO(Comments comments);
}
