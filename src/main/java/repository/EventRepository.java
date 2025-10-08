package repository;

import entity.Event;

import java.util.List;

public interface EventRepository extends GenericRepository<Event, Long> {

    List<Event> findAllByUserId(Long userId);
}
