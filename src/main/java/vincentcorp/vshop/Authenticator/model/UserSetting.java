package vincentcorp.vshop.Authenticator.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_setting")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSetting implements Serializable {
    @Id
    private int id;

    @Column(columnDefinition = "BLOB")
    private String data;
}
