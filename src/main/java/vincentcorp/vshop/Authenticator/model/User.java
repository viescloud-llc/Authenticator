package vincentcorp.vshop.Authenticator.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String username;

    @Column
    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    private UserProfile userProfile;

    @ManyToMany(cascade = CascadeType.REFRESH)
    private List<Role> userRoles;

    @OneToMany(cascade = CascadeType.ALL)
    private List<UserApi> userApis;

    @Column(columnDefinition = "BIT(1) default false")
    private boolean expirable = false;

    @OneToOne(cascade = CascadeType.ALL)
    private TimeModel expireTime;

    @Column(columnDefinition = "BIT(1) default true")
    private boolean enable = true;
}
