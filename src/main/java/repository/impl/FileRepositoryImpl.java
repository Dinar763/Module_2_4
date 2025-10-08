package repository.impl;

import entity.File;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import repository.FileRepository;
import utill.HibernateUtil;

import java.util.List;

@NoArgsConstructor
public class FileRepositoryImpl implements FileRepository {

    private static FileRepositoryImpl instance;

    public static synchronized FileRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new FileRepositoryImpl();
        }
        return instance;
    }

    @Override
    public File findByName(String name) {
        Session session = HibernateUtil.getCurrentSession();
        return session.createQuery("FROM File f WHERE f.name = :name", File.class)
                      .setParameter("name", name)
                      .uniqueResult();
    }

    @Override
    public File findByFilePath(String filePath) {
        Session session = HibernateUtil.getCurrentSession();
        return session.createQuery("FROM File f WHERE f.filePath = :filePath", File.class)
                      .setParameter("filePath", filePath)
                      .uniqueResult();
    }

    @Override
    public boolean existByName(String fileName) {
        return findByName(fileName) != null;
    }

    @Override
    public void deleteByName(String fileName) {
        Session session = HibernateUtil.getCurrentSession();
        File file = findByName(fileName);
        if (file != null) {
            session.remove(file);
        }
    }

    @Override
    public File findById(Long id) {
        Session session = HibernateUtil.getCurrentSession();
        return session.get(File.class, id);
    }

    @Override
    public List<File> findAll() {
        Session session = HibernateUtil.getCurrentSession();
        return session.createQuery("FROM File", File.class).list();
    }

    @Override
    public File save(File file) {
        Session session = HibernateUtil.getCurrentSession();
        session.persist(file);
        return  file;
    }

    @Override
    public File update(File file) {
        Session session = HibernateUtil.getCurrentSession();
        return session.merge(file);
    }

    @Override
    public void deleteById(Long id) {
        Session session = HibernateUtil.getCurrentSession();
        File file = findById(id);
        if (file != null) {
            session.remove(file);
        }
    }
}
