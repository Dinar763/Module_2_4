package repository;

import entity.User;

public interface UserRepository extends GenericRepository<User, Long> {

    User findByName(String name);
    boolean existByName(String name);
}
