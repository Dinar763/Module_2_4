package service;

import entity.User;

public interface UserService extends GenericService <User>  {

    User findByName(String name);
    User save(User user);
    User update(User user);
}
