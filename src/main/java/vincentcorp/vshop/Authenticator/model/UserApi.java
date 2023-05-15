package vincentcorp.vshop.Authenticator.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_api")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserApi {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 100)
    private String name;
    
    @Column
    private String apiKey;

    @Column(columnDefinition = "BIT(1) default false")
    private boolean expirable = false;

    @Column(columnDefinition = "BIT(1) default true")
    private boolean enable = true;

    @OneToOne(cascade = CascadeType.ALL)
    private TimeModel expireTime;
}
