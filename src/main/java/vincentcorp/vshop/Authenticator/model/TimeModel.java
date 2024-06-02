package vincentcorp.vshop.Authenticator.model;

import com.vincent.inc.viesspringutils.util.DateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "time")
public class TimeModel extends DateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
}
