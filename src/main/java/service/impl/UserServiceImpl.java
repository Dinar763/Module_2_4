package service.impl;

import entity.User;
import exception.ServiceException;
import repository.UserRepository;
import repository.impl.UserRepositoryImpl;
import service.UserService;

import java.util.Collections;
import java.util.List;

import static utill.HibernateUtil.*;

public class UserServiceImpl implements UserService {

    private static UserServiceImpl instance;
    private final UserRepository userRepository;

    private UserServiceImpl() {
        this.userRepository = UserRepositoryImpl.getInstance();
    }

    public static synchronized UserServiceImpl getInstance() {
        if (instance == null) {
            instance = new UserServiceImpl();
        }
        return instance;
    }

    @Override
    public User findByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public User save(User user) {
        beginTransaction();
        try {
            if (existByName(user.getName())) {
                throw new ServiceException("User " + user.getName() + " already exists");
            }
            User savedUser = userRepository.save(user);
            commitTransaction();
            return savedUser;
        } catch (ServiceException e) {
            rollbackTransaction();
            throw e;
        } catch (Exception e) {
            rollbackTransaction();
            throw new ServiceException("Failed to save user: " + e);
        }
    }

    @Override
    public User update(User user) {
        beginTransaction();
        try {
            User updatedUser = userRepository.update(user);
            commitTransaction();
            return updatedUser;
        } catch (Exception e) {
            rollbackTransaction();
            throw new ServiceException("Failed to update user: " + e);
        }
    }

    @Override
    public User findById(Long id) {
        if (id == null) {
            throw new ServiceException("ID must be not null");
        }
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        return users != null ? users : Collections.emptyList();
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new ServiceException("ID must be not null");
        }
        User user = findById(id);
        if (user == null) {
            throw new ServiceException("User with id " + id + " not found");
        }
        beginTransaction();
        try {
            userRepository.deleteById(id);
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw new ServiceException("Failed to delete user: " +  e);
        }
    }

    private boolean existByName(String name) {
        return userRepository.existByName(name);
    }
}
