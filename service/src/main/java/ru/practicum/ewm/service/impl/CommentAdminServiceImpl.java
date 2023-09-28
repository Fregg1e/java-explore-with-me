package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.exception.model.NotFoundException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.CommentAdminService;
import ru.practicum.ewm.utils.OffsetPageRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CommentAdminServiceImpl implements CommentAdminService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByEventId(Long eventId, Integer from, Integer size) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено.",
                        String.format("Событие с ID = %d не существует.", eventId)));
        return commentRepository.getCommentsByEventId(event.getId(), new OffsetPageRequest(from, size)).stream()
                .map(CommentMapper::fromCommentToCommentDto).collect(Collectors.toList());
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден.",
                        String.format("Комментарий с ID = %d не существует.", commentId)));
        commentRepository.deleteById(comment.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден.",
                        String.format("Комментарий с ID = %d не существует.", commentId)));
        return CommentMapper.fromCommentToCommentDto(comment);
    }
}
