package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.exception.model.AlreadyExistException;
import ru.practicum.ewm.exception.model.NotFoundException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.CategoryAdminService;
import ru.practicum.ewm.service.CategoryPublicService;
import ru.practicum.ewm.utils.OffsetPageRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryAdminService, CategoryPublicService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.fromNewCategoryDtoToCategory(newCategoryDto);
        try {
            category = categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistException("Name должен быть уникальным.",
                    "Категория с таким name уже существует!");
        }
        log.debug("Создана категория: {}", category);
        return CategoryMapper.fromCategoryToCategoryDto(category);
    }

    @Override
    public void deleteCategory(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория не найдена.",
                        String.format("Категория с ID = %d не существует.", catId)));
        Long eventCount = eventRepository.getCountEventsByCategoryId(category.getId());
        if (eventCount != 0) {
            throw new AlreadyExistException("Категория не пуста.",
                String.format("Категория с ID = %d не пуста.", catId));
        }
        categoryRepository.deleteById(category.getId());
        log.debug("Категория с id={} удалена.", category.getId());
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория не найдена.",
                        String.format("Категория с ID = %d не существует.", catId)));
        category.setName(categoryDto.getName());
        try {
            category = categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistException("Name должен быть уникальным.",
                    "Категория с таким name уже существует!");
        }
        log.debug("Категория обновлена: {}", category);
        return CategoryMapper.fromCategoryToCategoryDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        return categoryRepository.findAll(new OffsetPageRequest(from, size)).stream()
                .map(CategoryMapper::fromCategoryToCategoryDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория не найдена.",
                        String.format("Категория с ID = %d не существует.", catId)));
        return CategoryMapper.fromCategoryToCategoryDto(category);
    }
}
