package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.CommentDto;

import java.util.List;

public interface CommentPrivateService {
    CommentDto createComment(Long userId, Long eventId, CommentDto commentDto);

    CommentDto updateComment(Long userId, Long commentId, CommentDto commentDto);

    void deleteComment(Long userId, Long commentId);

    List<CommentDto> getCommentsByUserId(Long userId, Integer from, Integer size);

    CommentDto getCommentByUserIdAndCommentId(Long userId, Long commentId);
}
