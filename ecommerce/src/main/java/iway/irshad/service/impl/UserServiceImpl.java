package iway.irshad.service.impl;

import iway.irshad.config.JwtProvider;
import iway.irshad.entity.User;
import iway.irshad.repository.UserRepository;
import iway.irshad.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    public User findUserByJwtToken(String jwtToken) throws Exception {
        String email = jwtProvider.getEmailFromJwtToken(jwtToken);

        return this.findUserByEmail(email);
    }

    @Override
    public User findUserByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new Exception("User not found with this " + email);
        }
        return user;
    }
}
