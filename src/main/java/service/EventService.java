package service;

import entity.Event;
import entity.EventType;

import java.util.List;

public interface EventService extends GenericService <Event> {

    Event save(Long userId, Long fileId, EventType type);
    Event update(Event event);
    List<Event> findAllByUserId(Long userId);
}
