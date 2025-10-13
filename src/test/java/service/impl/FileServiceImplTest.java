package service.impl;

import entity.Event;
import entity.File;
import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.EventRepository;
import repository.FileRepository;
import repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private UserRepository userRepository;

    private FileServiceImpl fileService;

    private File testFile1;
    private File testFile2;
    private User user1;
    private User user2;
    private List<File> filesAll;

    @BeforeEach
    void setUp() {

        FileServiceImpl.resetInstance();
        fileService = new FileServiceImpl(fileRepository, userRepository, eventRepository);

        user1 = User.builder()
                    .id(1L)
                    .name("testUser1")
                    .build();User.builder().build();
        user2 = User.builder()
                    .id(2L)
                    .name("testUser2")
                    .build();
        testFile1 = File.builder()
                        .id(1L)
                        .name("test1.txt")
                        .user(user1)
                        .filePath("/path/to/test1.txt")
                        .build();
        testFile2 = File.builder()
                        .id(2L)
                        .name("test2.txt")
                        .user(user2)
                        .filePath("/path/to/test2.txt")
                        .build();
        filesAll = List.of(testFile1, testFile2);
    }

    @Test
    void testSave() {
        File fileToSave = File.builder()
                              .name("test1.txt")
                              .user(user1)
                              .filePath("/path/to/test1.txt")
                              .build();

        File expectedFile = File.builder()
                                .id(1L)
                                .name("test1.txt")
                                .user(user1)
                                .filePath("/path/to/test1.txt")
                                .build();

        when(fileRepository.save(fileToSave)).thenReturn(expectedFile);
        File result = fileService.save(fileToSave);
        assertNotNull(result);
        assertEquals("test1.txt", result.getName());
        verify(fileRepository).save(fileToSave);
    }

    @Test
    void testUpdate() {
        File fileUpdated = File.builder()
                               .id(1L)
                               .name("updated.txt")
                               .user(user1)
                               .filePath("/path/to/updated.txt")
                               .build();
        when(fileRepository.update(fileUpdated)).thenReturn(fileUpdated);

        File resultFile = fileService.update(fileUpdated);
        assertEquals("updated.txt", resultFile.getName());
        verify(fileRepository).update(fileUpdated);
    }

    @Test
    void testFindByName() {
        when(fileRepository.findByName("test1.txt")).thenReturn(testFile1);
        File result = fileService.findByName("test1.txt");
        assertEquals("test1.txt", result.getName());
        verify(fileRepository).findByName("test1.txt");
    }

    @Test
    void testFindByFilePath() {
        when(fileRepository.findByFilePath("/path/to/test1.txt")).thenReturn(testFile1);
        File result = fileService.findByFilePath("/path/to/test1.txt");
        assertEquals("test1.txt", result.getName());
        verify(fileRepository).findByFilePath("/path/to/test1.txt");
    }

    @Test
    void testExistByName() {
        when(fileRepository.existByName("test1.txt")).thenReturn(true);
        Boolean result = fileService.existByName("test1.txt");
        assertEquals(true, result);
        verify(fileRepository).existByName("test1.txt");
    }

    @Test
    void testDeleteById() {
        Long fileId = 1L;
        when(fileRepository.findById(fileId)).thenReturn(testFile1);
        fileService.deleteById(fileId);
        verify(fileRepository).findById(fileId);
        verify(fileRepository).deleteById(1L);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void testDeleteByName() {
        String fileName = "test1.txt";
        when(fileRepository.findByName(fileName)).thenReturn(testFile1);

        fileService.deleteByName(fileName);

        verify(fileRepository).findByName(fileName);
        verify(fileRepository).deleteById(testFile1.getId());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void testFindById() {
        when(fileRepository.findById(1L)).thenReturn(testFile1);
        File result = fileService.findById(1L);
        assertEquals("test1.txt", result.getName());
        verify(fileRepository).findById(1L);
    }

    @Test
    void testFindAll() {
        when(fileRepository.findAll()).thenReturn(filesAll);
        List<File> resultList = fileService.findAll();

        assertEquals(2, resultList.size());
        verify(fileRepository).findAll();
    }

    @Test
    void testUploadFile() throws IOException {
        String fileName = "test.txt";
        InputStream fileContent = mock(InputStream.class);

        when(userRepository.findById(1L)).thenReturn(user1);
        when(fileRepository.existByName(fileName)).thenReturn(false);
        when(fileRepository.save(any(File.class))).thenReturn(testFile1);

        File result = fileService.uploadFile(fileName, fileContent, 1L);

        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(fileRepository).existByName(fileName);
        verify(fileRepository).save(any(File.class));
        verify(eventRepository).save(any(Event.class));
    }
}