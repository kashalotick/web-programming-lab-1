package org.example.hotel.core.service;

import org.example.hotel.core.dto.*;
import org.example.hotel.core.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataImportExportServiceTest {

    @TempDir
    Path tempDir;

    private String testFilePath;
    private MockedStatic<Entity> entityMock;

    @BeforeEach
    void setUp() {
        testFilePath = tempDir.resolve("mock_data.json").toString();
        // Створюємо статичний мок для класу Entity перед кожним тестом
        entityMock = mockStatic(Entity.class);
    }

    @AfterEach
    void tearDown() {
        // Обов'язково закриваємо статичний мок після кожного тесту
        entityMock.close();
    }

    @Test
    @DisplayName("Експорт має викликати Entity.readAll та toDTO для кожного об'єкта")
    void testExportUsesMocks() {
        // 1. Готуємо мок-об'єкти
        Guest mockGuest = mock(Guest.class);
        GuestDTO mockDto = new GuestDTO();
        mockDto.name = "Mock User";

        when(mockGuest.toDTO()).thenReturn(mockDto);

        // Налаштовуємо поведінку статичного Entity.readAll
        entityMock.when(() -> Entity.readAll(Guest.class)).thenReturn(List.of(mockGuest));
        entityMock.when(() -> Entity.readAll(Hotel.class)).thenReturn(List.of());
        entityMock.when(() -> Entity.readAll(Room.class)).thenReturn(List.of());
        entityMock.when(() -> Entity.readAll(Reservation.class)).thenReturn(List.of());

        // 2. Викликаємо сервіс
        DataImportExportService.exportJSON(testFilePath);

        // 3. Перевірки (Verifications)
        // Чи викликав сервіс метод readAll?
        entityMock.verify(() -> Entity.readAll(Guest.class));
        // Чи викликав сервіс конвертацію в DTO на мок-об'єкті?
        verify(mockGuest).toDTO();
    }

    @Test
    @DisplayName("Імпорт має викликати Entity.create для відновлення об'єктів")
    void testImportUsesMocks() {
        // Спочатку створимо файл, який будемо імпортувати
        entityMock.when(() -> Entity.readAll(Guest.class)).thenReturn(List.of(new Guest("Ivan")));
        entityMock.when(() -> Entity.readAll(Hotel.class)).thenReturn(List.of());
        entityMock.when(() -> Entity.readAll(Room.class)).thenReturn(List.of());
        entityMock.when(() -> Entity.readAll(Reservation.class)).thenReturn(List.of());

        DataImportExportService.exportJSON(testFilePath);

        // Тепер тестуємо імпорт
        DataImportExportService.importJSON(testFilePath);

        // Перевіряємо, чи викликався Entity.create під час імпорту
        entityMock.verify(() -> Entity.create(any(Guest.class)), atLeastOnce());
        entityMock.verify(() -> Entity.clear());
    }

    @Test
    @DisplayName("Імпорт має логувати помилку, якщо готель для кімнати не знайдено")
    void testImportErrorHandling() {
        // 1. Створюємо файл з даними про кімнату
        Room mockRoom = mock(Room.class);
        RoomDTO roomDto = new RoomDTO();
        roomDto.hotelId = 999; // ID готелю, якого не існує
        roomDto.localId = 101;

        when(mockRoom.toDTO()).thenReturn(roomDto);
        entityMock.when(() -> Entity.readAll(Room.class)).thenReturn(List.of(mockRoom));
        entityMock.when(() -> Entity.readAll(Guest.class)).thenReturn(List.of());
        entityMock.when(() -> Entity.readAll(Hotel.class)).thenReturn(List.of());
        entityMock.when(() -> Entity.readAll(Reservation.class)).thenReturn(List.of());

        // Створюємо файл фізично
        DataImportExportService.exportJSON(testFilePath);

        // 2. Налаштовуємо мок так, щоб Entity.read(Hotel.class, 999) повертав null
        entityMock.when(() -> Entity.read(eq(Hotel.class), anyInt())).thenReturn(null);

        // 3. Тепер імпорт знайде файл, але не знайде готель у базі
        assertDoesNotThrow(() -> DataImportExportService.importJSON(testFilePath));

        // Перевіряємо, що ми хоча б намагалися прочитати готель
        entityMock.verify(() -> Entity.read(eq(Hotel.class), eq(999)));
    }
}