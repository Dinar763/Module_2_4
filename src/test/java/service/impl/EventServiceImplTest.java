package service.impl;


import entity.Event;
import entity.EventType;
import entity.File;
import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.EventRepository;
import repository.FileRepository;
import repository.UserRepository;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private User testUser;
    private File testFile;
    private Event testEvent1;
    private Event testEvent2;
    private List<Event> eventsAllById;
    private List<Event> eventsAll;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                       .id(1L)
                       .name("testUser")
                       .build();

        testFile = File.builder()
                       .id(2L)
                       .name("test.txt")
                       .filePath("/path/to/test.txt")
                       .build();

        testEvent1 = Event.builder()
                          .id(1L)
                          .user(testUser)
                          .fileId(testFile.getId())
                          .fileName(testFile.getName())
                          .filePath(testFile.getFilePath())
                          .type(EventType.UPLOAD)
                          .build();

        testEvent2 = Event.builder()
                          .id(2L)
                          .user(new User(2L, "Valer Valerov", new ArrayList<>()))
                          .fileId(3L)
                          .fileName("test2.txt")
                          .filePath("/path/to/test2.txt")
                          .type(EventType.UPLOAD)
                          .build();

        eventsAllById = List.of(testEvent1);
        eventsAll = List.of(testEvent1, testEvent2);
    }

    @Test
    void testSave() {
        when(userRepository.findById(testUser.getId())).thenReturn(testUser);
        when(fileRepository.findById(testFile.getId())).thenReturn(testFile);
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent1);

        Event result = eventService.save(testUser.getId(), testFile.getId(), EventType.UPLOAD);

        assertNotNull(result);
        assertEquals(testEvent1.getId(), result.getId());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void testUpdate() {
        Event updatedEvent = Event.builder()
                                  .id(1L)
                                  .user(testUser)
                                  .fileId(testFile.getId())
                                  .fileName("updated.txt")
                                  .filePath(testFile.getFilePath())
                                  .type(EventType.UPLOAD)
                                  .build();

        when(eventRepository.update(updatedEvent)).thenReturn(updatedEvent);

        Event resulEvent = eventService.update(updatedEvent);
        assertEquals("updated.txt", resulEvent.getFileName());
        verify(eventRepository).update(updatedEvent);
    }

    @Test
    void testFindAllByUserId(){
        when(eventRepository.findAllByUserId(1L)).thenReturn(eventsAllById);
        List<Event> resultList = eventService.findAllByUserId(1L);

        assertEquals(1, resultList.size());
        verify(eventRepository).findAllByUserId(1L);
    }

    @Test
    void testFindById() {
        when(eventRepository.findById(1l)).thenReturn(testEvent1);
        Event result = eventService.findById(1l);
        assertEquals("test.txt", result.getFileName());
        verify(eventRepository).findById(1L);
    }

    @Test
    void testFindAll() {
        when(eventRepository.findAll()).thenReturn(eventsAll);
        List<Event> resultList = eventService.findAll();

        assertEquals(2, resultList.size());
        verify(eventRepository).findAll();
    }

    @Test
    void testDeleteById() {
        eventService.deleteById(1L);
        verify(eventRepository).deleteById(1L);
    }
}