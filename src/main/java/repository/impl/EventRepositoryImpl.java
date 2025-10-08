package repository.impl;

import entity.Event;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import repository.EventRepository;
import utill.HibernateUtil;

import java.util.List;

@NoArgsConstructor
public class EventRepositoryImpl implements EventRepository {

    private static EventRepositoryImpl instance;

    public static synchronized EventRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new EventRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<Event> findAllByUserId(Long userId) {
        Session session = HibernateUtil.getCurrentSession();
        return session.createQuery("FROM Event e WHERE e.user.id = :userId", Event.class)
                      .setParameter("userId", userId)
                      .list();
    }

    @Override
    public Event findById(Long id) {
        Session session = HibernateUtil.getCurrentSession();
        return session.get(Event.class, id);
    }

    @Override
    public List<Event> findAll() {
        Session session = HibernateUtil.getCurrentSession();
        return session.createQuery("FROM Event", Event.class).list();
    }

    @Override
    public Event save(Event event) {
        Session session = HibernateUtil.getCurrentSession();
        session.persist(event);
        return event;
    }

    @Override
    public Event update(Event event) {
        Session session = HibernateUtil.getCurrentSession();
        return session.merge(event);
    }

    @Override
    public void deleteById(Long id) {
        Session session = HibernateUtil.getCurrentSession();
        Event event = findById(id);
        if (event != null) {
            session.remove(event);
        }
    }
}
