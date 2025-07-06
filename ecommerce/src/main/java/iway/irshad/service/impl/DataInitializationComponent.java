package iway.irshad.service.impl;

import iway.irshad.domain.USER_ROLE;
import iway.irshad.entity.User;
import iway.irshad.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializationComponent implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void run(String... args) {
        initializeAdminUser();
    }

    private void initializeAdminUser() {
        String adminUserName = "iway.irshaad@gmail.com";

        if (userRepository.findByEmail(adminUserName) == null) {
            User adminuser = new User();

            adminuser.setEmail(adminUserName);
            adminuser.setPassword(passwordEncoder.encode("Iway@Admin"));
            adminuser.setMobile("+91 - 9304200977");
            adminuser.setFirstName("Md");
            adminuser.setLastName("Irshad");
            adminuser.setRole(USER_ROLE.ROLE_ADMIN);

            userRepository.save(adminuser);
        }
    }
}
