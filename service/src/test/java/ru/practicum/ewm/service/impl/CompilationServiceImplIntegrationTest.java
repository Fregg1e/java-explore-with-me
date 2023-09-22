package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;
import ru.practicum.ewm.exception.model.NotFoundException;
import ru.practicum.ewm.model.Compilation;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("integrationtest")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CompilationServiceImplIntegrationTest {
    private final EntityManager entityManager;
    private final CompilationServiceImpl compilationService;

    @Test
    void createCompilationTest_whenOnlyTitle_thenCompilationDto() {
        NewCompilationDto newCompilationDto = NewCompilationDto.builder().title("test").build();

        CompilationDto compilationDto = compilationService.createCompilation(newCompilationDto);

        assertEquals(newCompilationDto.getTitle(), compilationDto.getTitle());
        assertEquals(false, compilationDto.getPinned());
        assertTrue(compilationDto.getEvents().isEmpty());
    }

    @Test
    void createCompilationTest_whenEventIsNotExists_thenNotFoundException() {
        NewCompilationDto newCompilationDto = NewCompilationDto.builder()
                .events(List.of(999L))
                .title("test")
                .build();

        assertThrows(NotFoundException.class, () -> compilationService.createCompilation(newCompilationDto));
    }

    @Test
    void deleteCompilationTest_whenDelete_thenCompilationDoesNotExists() {
        Compilation compilation = Compilation.builder().title("test").pinned(false).build();
        entityManager.persist(compilation);

        compilationService.deleteCompilation(compilation.getId());

        TypedQuery<Long> query = entityManager
                .createQuery("Select count(c) from Compilation c where c.id = :id", Long.class);
        Long count = query
                .setParameter("id", compilation.getId())
                .getSingleResult();

        assertEquals(0, count);
    }

    @Test
    void updateCompilationTest_whenUpdateCompilation_thenReturnUpdatedCompilation() {
        Compilation compilation = Compilation.builder().title("test").pinned(false).build();
        entityManager.persist(compilation);
        UpdateCompilationRequest updateCompilationRequest = UpdateCompilationRequest.builder().title("updateTest")
                .build();

        CompilationDto compilationDto = compilationService.updateCompilation(compilation.getId(),
                updateCompilationRequest);

        assertEquals(compilation.getPinned(), compilationDto.getPinned());
        assertEquals(updateCompilationRequest.getTitle(), compilationDto.getTitle());
    }

    @Test
    void getCompilationsTest_whenCompilationsNotFound_thenReturnEmptyList() {
        List<CompilationDto> compilationDtos = compilationService.getCompilations(null, 0, 10);

        assertTrue(compilationDtos.isEmpty());
    }

    @Test
    void getCompilationsTest_whenCompilationsFound_thenReturnCompilationsList() {
        Compilation compilation = Compilation.builder().title("test").pinned(false).build();
        entityManager.persist(compilation);

        List<CompilationDto> compilationDtos = compilationService.getCompilations(null, 0, 10);

        assertFalse(compilationDtos.isEmpty());
        assertEquals(1, compilationDtos.size());
        assertEquals(compilation.getId(), compilationDtos.get(0).getId());
    }

    @Test
    void getCompilationByIdTest_whenCompilationFound_thenReturnCompilation() {
        Compilation compilation = Compilation.builder().title("test").pinned(false).build();
        entityManager.persist(compilation);

        CompilationDto compilationDto = compilationService.getCompilationById(compilation.getId());

        assertEquals(compilation.getId(), compilationDto.getId());
        assertEquals(compilation.getTitle(), compilationDto.getTitle());
        assertEquals(compilation.getPinned(), compilationDto.getPinned());
    }
}