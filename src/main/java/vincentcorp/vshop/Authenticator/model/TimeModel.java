package vincentcorp.vshop.Authenticator.model;

import java.io.Serializable;
import java.time.ZoneId;

import com.vincent.inc.viesspringutils.util.DateTime;

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
    private int hour;
    
    @Column
    private int minute;
    
    @Column
    private int second;

    @Column
    private ZoneId currentZoneId = DateTime.DEFAULT_ZONE_ID;

    @Column
    private boolean bypassMax = false;

    public DateTime toDateTime() {
        return DateTime.builder()
                       .year(year)
                       .month(month)
                       .day(day)
                       .hour(hour)
                       .minute(minute)
                       .second(second)
                       .currentZoneId(currentZoneId)
                       .bypassMax(bypassMax)
                       .build();
    }
}
