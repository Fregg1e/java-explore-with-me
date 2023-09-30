package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.CommentDto;

import java.util.List;

public interface CommentAdminService {
    List<CommentDto> getCommentsByEventId(Long eventId, Integer from, Integer size);

    void deleteCommentByAdmin(Long commentId);

    CommentDto getCommentById(Long commentId);
}
