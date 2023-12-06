package vincentcorp.vshop.Authenticator.model;

import java.io.Serializable;

import com.vincent.inc.viesspringutils.util.Time;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "time")
public class TimeModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private int year;
    
    @Column
    private int month;
    
    @Column
    private int day;
    
    @Column
    private int hours;
    
    @Column
    private int minute;
    
    @Column
    private int second;

    public Time toTime() {
        return new Time(year, month, day, hours, minute, second);
    }
}
