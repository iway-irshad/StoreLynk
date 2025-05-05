package iway.irshad.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessDetails {

    private String businessName;
    private String businessAddress;
    private String businessEmail;
    private String businessPhone;
    private String logo;
    private String banner;
}
