package utill;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory;
    private static final ThreadLocal<Session> sessionThreadLocal = new ThreadLocal<>();

    static {
        try {
            sessionFactory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getCurrentSession() {
        Session session = sessionThreadLocal.get();
        if (session == null || !session.isOpen()) {
            session = sessionFactory.openSession();
            sessionThreadLocal.set(session);
        }
        return session;
    }

    public static void closeCurrentSession() {
        Session session = sessionThreadLocal.get();
        if (session != null && session.isOpen()) {
            session.close();
        }
        sessionThreadLocal.remove();
    }

    public static Session openSession() {
        return sessionFactory.openSession();
    }

    public static void beginTransaction() {
        getCurrentSession().beginTransaction();
    }

    public static void commitTransaction() {
        getCurrentSession().getTransaction().commit();
    }

    public static void rollbackTransaction() {
        getCurrentSession().getTransaction().rollback();
    }

    public static void shutdown() {
        sessionFactory.close();
    }
}
