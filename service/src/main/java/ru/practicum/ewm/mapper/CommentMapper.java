package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.model.Comment;

public class CommentMapper {
    public static CommentDto fromCommentToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .user(comment.getUser().getName())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }
}
