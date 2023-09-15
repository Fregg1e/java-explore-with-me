package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.exception.model.AlreadyExistException;
import ru.practicum.ewm.model.Category;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@ActiveProfiles("integrationtest")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CategoryServiceImplIntegrationTest {
    private final EntityManager entityManager;
    private final CategoryServiceImpl categoryService;

    @Test
    void createCategoryTest_whenCreateCategory_thenReturnCategoryDto() {
        NewCategoryDto newCategoryDto = NewCategoryDto.builder().name("test").build();

        CategoryDto categoryDto = categoryService.createCategory(newCategoryDto);

        assertEquals(newCategoryDto.getName(), categoryDto.getName());
    }

    @Test
    void createCategoryTest_whenCreateIsExists_thenThrowAlreadyExistsException() {
        NewCategoryDto newCategoryDto = NewCategoryDto.builder().name("test").build();
        Category category = Category.builder().name("test").build();
        entityManager.persist(category);

        assertThrows(AlreadyExistException.class, () -> categoryService.createCategory(newCategoryDto));
    }
}