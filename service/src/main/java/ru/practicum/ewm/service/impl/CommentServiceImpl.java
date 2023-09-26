package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.exception.model.EventStateException;
import ru.practicum.ewm.exception.model.NotFoundException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.CommentAdminService;
import ru.practicum.ewm.service.CommentPrivateService;
import ru.practicum.ewm.utils.OffsetPageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentPrivateService, CommentAdminService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentDto createComment(Long userId, Long eventId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден.",
                String.format("Пользователя с ID = %d не существует.", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено.",
                        String.format("Событие с ID = %d не существует.", eventId)));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new EventStateException("Невозможно создать комментарий", "Статус Event должен быть PUBLISHED");
        }
        Comment comment = Comment.builder().user(user).event(event).text(commentDto.getText())
                .created(LocalDateTime.now()).build();
        return CommentMapper.fromCommentToCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateComment(Long userId, Long commentId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден.",
                String.format("Пользователя с ID = %d не существует.", userId)));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден.",
                        String.format("Комментарий с ID = %d не существует.", commentId)));
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new NotFoundException("Комментарий не найден.",
                    String.format("Комментарий с ID = %d не существует.", commentId));
        }
        comment.setText(commentDto.getText());
        return CommentMapper.fromCommentToCommentDto(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден.",
                String.format("Пользователя с ID = %d не существует.", userId)));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден.",
                        String.format("Комментарий с ID = %d не существует.", commentId)));
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new NotFoundException("Комментарий не найден.",
                    String.format("Комментарий с ID = %d не существует.", commentId));
        }
        commentRepository.deleteById(comment.getId());
    }

    @Override
    public List<CommentDto> getCommentsByUserId(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден.",
                String.format("Пользователя с ID = %d не существует.", userId)));
        return commentRepository.getCommentsByUserId(user.getId(), new OffsetPageRequest(from, size)).stream()
                .map(CommentMapper::fromCommentToCommentDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentByUserIdAndCommentId(Long userId, Long commentId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден.",
                String.format("Пользователя с ID = %d не существует.", userId)));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден.",
                        String.format("Комментарий с ID = %d не существует.", commentId)));
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new NotFoundException("Комментарий не найден.",
                    String.format("Комментарий с ID = %d не существует.", commentId));
        }
        return CommentMapper.fromCommentToCommentDto(comment);
    }

    @Override
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
    public CommentDto getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден.",
                        String.format("Комментарий с ID = %d не существует.", commentId)));
        return CommentMapper.fromCommentToCommentDto(comment);
    }
}
