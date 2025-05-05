package iway.irshad.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    private String code;
    private double discountPercent;
    private LocalDate validityStartDate;
    private LocalDate validityEndDate;
    private double minimumOrdervalue;
    private boolean isActive = true;

    @ManyToMany(mappedBy = "usedCoupon")
    private Set<User> usedByUser = new HashSet<>();
}
