package vincentcorp.vshop.Authenticator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vincentcorp.vshop.Authenticator.util.Time;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "time")
public class TimeModel {
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
