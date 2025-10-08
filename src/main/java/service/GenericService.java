package service;

import java.util.List;

public interface GenericService <T> {

    T findById(Long id);
    List<T> findAll();
    void deleteById(Long id);
}
