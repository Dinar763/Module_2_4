package service.impl;

import entity.Event;
import entity.EventType;
import entity.File;
import entity.User;
import exception.ServiceException;
import repository.EventRepository;
import repository.FileRepository;
import repository.UserRepository;
import repository.impl.EventRepositoryImpl;
import repository.impl.FileRepositoryImpl;
import service.EventService;

import java.util.Collections;
import java.util.List;

import static utill.HibernateUtil.*;

public class EventServiceImpl implements EventService {

    private static EventServiceImpl instance;
    EventRepository eventRepository;
    FileRepository fileRepository;
    UserRepository userRepository;

    EventServiceImpl() {
        this.eventRepository = EventRepositoryImpl.getInstance();
        this.fileRepository = FileRepositoryImpl.getInstance();
    }

    public static synchronized EventServiceImpl getInstance() {
        if (instance == null) {
            instance = new EventServiceImpl();
        }
        return instance;
    }

    @Override
    public Event save(Long userId, Long fileId, EventType type) {
        beginTransaction();
        try {
            User user = userRepository.findById(userId);
            if (user == null) {
                throw new ServiceException("User not found: " + userId);
            }

            File file = fileRepository.findById(fileId);
            if (file == null) {
                throw new ServiceException("File not found: " + fileId);
            }

            Event event = Event.builder()
                               .fileId(file.getId())
                               .fileName(file.getName())
                               .filePath(file.getFilePath())
                    .user(user)
                    .type(type)
                               .build();
            commitTransaction();
            return eventRepository.save(event);
        } catch (Exception e) {
            rollbackTransaction();
            throw new ServiceException("Failed to save event: " + e);
        }
    }

    @Override
    public Event update(Event event) {
        beginTransaction();
        try {
            Event updatedEvent = eventRepository.update(event);
            commitTransaction();
            return updatedEvent;
        } catch (Exception e) {
            rollbackTransaction();
            throw new ServiceException("Failed to update event: " + e);
        }
    }

    @Override
    public List<Event> findAllByUserId(Long userId) {
        List<Event> events = eventRepository.findAllByUserId(userId);
        return events != null ? events : Collections.emptyList();
    }

    @Override
    public Event findById(Long id) {
        if (id == null) {
            throw new ServiceException("ID must be not null");
        }
        return eventRepository.findById(id);
    }

    @Override
    public List<Event> findAll() {
        List<Event> events = eventRepository.findAll();
        return events != null ? events : Collections.emptyList();
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new ServiceException("ID must be not null");
        }
        beginTransaction();
        try {
            eventRepository.deleteById(id);
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw new ServiceException("Failed to delete event: " +  e);
        }
    }
}
