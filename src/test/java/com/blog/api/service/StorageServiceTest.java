package com.blog.api.service;

import com.blog.api.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.util.FileSystemUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.any;
import org.springframework.core.io.UrlResource;

class StorageServiceTest {
    private StorageService storageService;
    @BeforeEach void setUp() { storageService = new StorageService(); }

    @Test
    void loadAsResource_notFound_throwsException() throws Exception {
        storageService = new StorageService();
        java.lang.reflect.Field field = StorageService.class.getDeclaredField("uploadDir");
        field.setAccessible(true);
        field.set(storageService, "uploads-test");
        storageService.init();
        assertThrows(RuntimeException.class, () -> storageService.loadAsResource("notfound.txt"));
    }

    @Test
    void store_success() throws Exception {
        storageService = new StorageService();
        java.lang.reflect.Field field = StorageService.class.getDeclaredField("uploadDir");
        field.setAccessible(true);
        field.set(storageService, "uploads-test");
        storageService.init();
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));
        String filename = storageService.store(file);
        assertNotNull(filename);
        // Nettoyage
        storageService.delete(filename);
    }

    @Test
    void store_emptyFile_throwsException() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);
        storageService = new StorageService();
        java.lang.reflect.Field field;
        try {
            field = StorageService.class.getDeclaredField("uploadDir");
            field.setAccessible(true);
            field.set(storageService, "uploads-test");
            storageService.init();
        } catch (Exception e) { throw new RuntimeException(e); }
        assertThrows(RuntimeException.class, () -> storageService.store(file));
    }

    @Test
    void store_destinationFileOutsideRoot_throwsException() throws Exception {
        storageService = new StorageService();
        java.lang.reflect.Field field = StorageService.class.getDeclaredField("uploadDir");
        field.setAccessible(true);
        field.set(storageService, "uploads-test");
        storageService.init();
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("../evil.txt");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));
        assertThrows(RuntimeException.class, () -> storageService.store(file));
    }

    @Test
    void store_ioException_throwsException() throws Exception {
        storageService = new StorageService();
        java.lang.reflect.Field field = StorageService.class.getDeclaredField("uploadDir");
        field.setAccessible(true);
        field.set(storageService, "uploads-test");
        storageService.init();
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(file.getInputStream()).thenThrow(new IOException("fail"));
        assertThrows(RuntimeException.class, () -> storageService.store(file));
    }

    @Test
    void deleteAll_success() {
        storageService = new StorageService();
        java.lang.reflect.Field field;
        try {
            field = StorageService.class.getDeclaredField("uploadDir");
            field.setAccessible(true);
            field.set(storageService, "uploads-test");
            storageService.init();
        } catch (Exception e) { throw new RuntimeException(e); }
        assertDoesNotThrow(() -> storageService.deleteAll());
    }

    @Test
    void delete_success() {
        storageService = new StorageService();
        java.lang.reflect.Field field;
        try {
            field = StorageService.class.getDeclaredField("uploadDir");
            field.setAccessible(true);
            field.set(storageService, "uploads-test");
            storageService.init();
        } catch (Exception e) { throw new RuntimeException(e); }
        assertDoesNotThrow(() -> storageService.delete("notfound.txt"));
    }

    @Test
    void delete_ioException_throwsException() throws Exception {
        storageService = new StorageService();
        java.lang.reflect.Field field = StorageService.class.getDeclaredField("uploadDir");
        field.setAccessible(true);
        field.set(storageService, "uploads-test");
        storageService.init();
        Path file = Paths.get("uploads-test/notfound.txt");
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.deleteIfExists(file)).thenThrow(new java.io.IOException("fail"));
            assertThrows(RuntimeException.class, () -> storageService.delete("notfound.txt"));
        }
    }

    @Test
    void init_ioException_throwsException() throws Exception {
        StorageService service = new StorageService();
        java.lang.reflect.Field field = StorageService.class.getDeclaredField("uploadDir");
        field.setAccessible(true);
        field.set(service, "uploads-test-io");
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(any())).thenReturn(false);
            filesMock.when(() -> Files.createDirectories(any())).thenThrow(new java.io.IOException("fail"));
            assertThrows(RuntimeException.class, service::init);
        }
    }

    @Test
    void loadAsResource_success() throws Exception {
        storageService = new StorageService();
        java.lang.reflect.Field field = StorageService.class.getDeclaredField("uploadDir");
        field.setAccessible(true);
        field.set(storageService, "uploads-test");
        storageService.init();
        // Crée un fichier temporaire
        String filename = "file.txt";
        Path filePath = Paths.get("uploads-test").resolve(filename);
        Files.write(filePath, "data".getBytes());
        Resource res = storageService.loadAsResource(filename);
        assertNotNull(res);
        assertTrue(res.exists() && res.isReadable());
        Files.deleteIfExists(filePath);
    }
} 