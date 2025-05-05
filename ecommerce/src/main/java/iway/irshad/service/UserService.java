package iway.irshad.service;

import iway.irshad.entity.User;

public interface UserService {

    User findUserByJwtToken(String jwtToken) throws Exception;
    User findUserByEmail(String email) throws Exception;

}
