package iway.irshad.entity;

import iway.irshad.domain.HomeCategorySelection;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class HomeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

   private String name;
   private String image;
   private String categoryId;
   private HomeCategorySelection selection;
}
