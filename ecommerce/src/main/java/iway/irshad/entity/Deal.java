package iway.irshad.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Deal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    private Integer discount;

    @OneToOne
    private HomeCategory category;
}
