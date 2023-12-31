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
import ru.practicum.ewm.exception.model.NotFoundException;
import ru.practicum.ewm.model.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void deleteCategoryTest_whenCategoryIsExist_thenDeleteCategory() {
        Category category = Category.builder().name("test").build();
        entityManager.persist(category);

        categoryService.deleteCategory(category.getId());

        TypedQuery<Long> query = entityManager
                .createQuery("Select count(c) from Category c where c.id = :id", Long.class);
        Long count = query
                .setParameter("id", category.getId())
                .getSingleResult();

        assertEquals(0, count);
    }

    @Test
    void deleteCategoryTest_whenCategoryIsNotExist_thenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> categoryService.deleteCategory(999L));
    }

    @Test
    void deleteCategoryTest_whenCategoryIsNotEmpty_thenThrowAlreadyExistException() {
        Category category = Category.builder().name("test").build();
        entityManager.persist(category);
        User initiator = User.builder().name("test1").email("test@test.com").build();
        entityManager.persist(initiator);
        Location location = Location.builder().lat(5.1F).lon(4.2F).build();
        entityManager.persist(location);
        Event event = Event.builder()
                .annotation("test")
                .category(category)
                .createdOn(LocalDateTime.now().minusDays(1))
                .description("test")
                .eventDate(LocalDateTime.now().minusDays(5))
                .initiator(initiator)
                .location(location)
                .paid(false)
                .participantLimit(0)
                .requestModeration(false)
                .state(EventState.PENDING)
                .title("test")
                .build();
        entityManager.persist(event);

        assertThrows(AlreadyExistException.class, () -> categoryService.deleteCategory(category.getId()));
    }

    @Test
    void updateCategoryTest_whenCategoryIsExists_thenUpdateCategory() {
        Category category = Category.builder().name("test").build();
        entityManager.persist(category);
        CategoryDto updateCategoryDto = CategoryDto.builder().name("updateTest").build();

        CategoryDto categoryDto = categoryService.updateCategory(category.getId(), updateCategoryDto);

        assertEquals(category.getId(), categoryDto.getId());
        assertEquals(updateCategoryDto.getName(), categoryDto.getName());
    }

    @Test
    void updateCategoryTest_whenCategoryNameIsExists_thenAlreadyExistException() {
        Category category1 = Category.builder().name("test1").build();
        entityManager.persist(category1);
        Category category2 = Category.builder().name("test2").build();
        entityManager.persist(category2);
        CategoryDto updateCategoryDto = CategoryDto.builder().name("test2").build();

        assertThrows(AlreadyExistException.class, () -> categoryService.updateCategory(category1.getId(),
                updateCategoryDto));
    }

    @Test
    void updateCategoryTest_whenCategoryIsNotExists_thenNotFoundException() {
        CategoryDto updateCategoryDto = CategoryDto.builder().name("updateTest").build();

        assertThrows(NotFoundException.class, () -> categoryService.updateCategory(999L, updateCategoryDto));
    }

    @Test
    void getCategoriesTest_whenCategoryIsExists_thenReturnListCategories() {
        Category category = Category.builder().name("test").build();
        entityManager.persist(category);

        List<CategoryDto> categoryDtos = categoryService.getCategories(0, 10);

        assertFalse(categoryDtos.isEmpty());
        assertEquals(1, categoryDtos.size());
        assertEquals(category.getId(), categoryDtos.get(0).getId());
        assertEquals(category.getName(), categoryDtos.get(0).getName());
    }

    @Test
    void getCategoryByIdTest_whenCategoryIsExists_thenReturnCategoryDto() {
        Category category = Category.builder().name("test").build();
        entityManager.persist(category);

        CategoryDto categoryDto = categoryService.getCategoryById(category.getId());

        assertEquals(category.getId(), categoryDto.getId());
        assertEquals(category.getName(), categoryDto.getName());
    }
}