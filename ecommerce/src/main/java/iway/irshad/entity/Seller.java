package iway.irshad.entity;

import iway.irshad.domain.AccountStatus;
import iway.irshad.domain.USER_ROLE;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    private String sellerName;
    private String mobile;

    @Column(unique = true, nullable = false)
    private String email;
    private String password;

    @Embedded
    private BusinessDetails businessDetail = new BusinessDetails();

    @Embedded
    private BankDetails bankDetails = new BankDetails();

    @OneToOne(cascade = CascadeType.ALL)
    private Address pickupAddress = new Address();

    private String GSTIN;

    private USER_ROLE role = USER_ROLE.ROLE_SELLER;

    private boolean isEmailVerified = false;

    private AccountStatus accountStatus = AccountStatus.PENDING_VERIFICATION;
}
