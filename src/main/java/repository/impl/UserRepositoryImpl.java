package repository.impl;

import entity.User;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import repository.UserRepository;
import utill.HibernateUtil;

import java.util.List;

@NoArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private static UserRepositoryImpl instance;

    public static synchronized UserRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new UserRepositoryImpl();
        }
        return instance;
    }

    @Override
    public User findByName(String name) {
        Session session = HibernateUtil.getCurrentSession();
        return session.createQuery("FROM User u WHERE u.name = :name", User.class)
                .setParameter("name", name)
                .uniqueResult();
    }

    @Override
    public boolean existByName(String name) {
        return findByName(name) != null;
    }

    @Override
    public User findById(Long id) {
        Session session = HibernateUtil.getCurrentSession();
        return session.get(User.class, id);
    }

    @Override
    public List<User> findAll() {
        Session session = HibernateUtil.getCurrentSession();
        return session.createQuery("FROM User ", User.class).list();
    }

    @Override
    public User save(User user) {
        Session session = HibernateUtil.getCurrentSession();
        session.persist(user);
        session.flush();
        return user;
    }

    @Override
    public User update(User user) {
        Session session = HibernateUtil.getCurrentSession();
        return session.merge(user);
    }

    @Override
    public void deleteById(Long id) {
        Session session = HibernateUtil.getCurrentSession();
        User user = findById(id);
        if (user != null) {
            session.remove(user);
        }
    }
}
