package vincentcorp.vshop.Authenticator.model;

import java.io.Serializable;
import java.util.List;

import com.viescloud.llc.viesspringutils.config.jpa.BooleanConverter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "route")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Route implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String path;

    @Column(length = 20)
    private String method;

    @Builder.Default
    @Column(length = 10)
    @Convert(converter = BooleanConverter.class)
    private boolean secure = false;

    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    private List<Role> roles;
}
