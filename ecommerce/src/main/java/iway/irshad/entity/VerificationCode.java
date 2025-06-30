package iway.irshad.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    private String otp;

    private String email;

    private LocalDateTime createdAt = LocalDateTime.now();


    private int attemptCount = 0;

    @OneToOne
    private User user;

    @OneToOne
    private Seller seller;

}

