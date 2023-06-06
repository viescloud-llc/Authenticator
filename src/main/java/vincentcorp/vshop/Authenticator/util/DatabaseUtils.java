package vincentcorp.vshop.Authenticator.util;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope("prototype")
@Slf4j
public class DatabaseUtils<V, K> {

    @Getter
    @Setter
    private int TTL = 600;

    private String hashes = String.format("%s.%s", this.getClass().getName(), "default");

    @Autowired
    private RedisTemplate<String, V> redisTemplate;

    @Getter
    private JpaRepository<V, K> jpaRepository;

    public DatabaseUtils<V, K> init(JpaRepository<V, K> jpaRepository, String hashes) {
        this.jpaRepository = jpaRepository;
        this.hashes = hashes;
        return this;
    }

    public V get(K key) {
        try {
            String hashKey = String.format("%s.%s", this.hashes, key);
            V value = this.redisTemplate.opsForValue().get(hashKey);

            if (ObjectUtils.isEmpty(value)) {
                value = null;
                var oValue = this.jpaRepository.findById(key);
                if (oValue.isPresent()) {
                    value = oValue.get();
                    this.save(key, value);
                    return value;
                }
            }

            return value;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    public V getAndExpire(K key) {
        String hashKey = String.format("%s.%s", this.hashes, key);
        var value = this.get(key);
        if (!ObjectUtils.isEmpty(value))
            this.redisTemplate.expire(hashKey, Duration.ofSeconds(TTL));

        return value;
    }

    public V save(K key, V value) {
        try {
            var hashKey = String.format("%s.%s", this.hashes, key);

            if (!ObjectUtils.isEmpty(this.jpaRepository)) {
                value = jpaRepository.save(value);
            }

            this.redisTemplate.opsForValue().set(hashKey, value);

            return value;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    public V save(V value) {
        try {
            if (ObjectUtils.isEmpty(this.jpaRepository))
                return null;

            value = jpaRepository.save(value);
            var id = ReflectionUtils.getIdFieldValue(value);

            if (ObjectUtils.isEmpty(id))
                return null;

            var hashKey = String.format("%s.%s", this.hashes, id);
            this.redisTemplate.opsForValue().set(hashKey, value);

            return value;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    public V saveAndExpire(K key, V value) {
        try {
            var hashKey = String.format("%s.%s", this.hashes, key);
            var saveValue = this.save(key, value);
            this.redisTemplate.expire(hashKey, Duration.ofMinutes(TTL));

            return saveValue;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    public V saveAndExpire(V value) {
        try {
            var saveValue = this.save(value);

            var id = ReflectionUtils.getIdFieldValue(saveValue);
            if (ObjectUtils.isEmpty(id))
                return null;

            var hashKey = String.format("%s.%s", this.hashes, id);
            this.redisTemplate.expire(hashKey, Duration.ofMinutes(TTL));

            return saveValue;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    public void deleteById(K key) {
        try {
            var hashKey = String.format("%s.%s", this.hashes, key);

            this.jpaRepository.deleteById(key);

            this.redisTemplate.delete(hashKey);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public void delete(V value) {
        try {
            var id = ReflectionUtils.getIdFieldValue(value);
            var hashKey = String.format("%s.%s", this.hashes, id);

            this.jpaRepository.delete(value);

            if (!ObjectUtils.isEmpty(id))
                this.redisTemplate.delete(hashKey);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Bean
    public static JedisConnectionFactory connectionFactory(@Value("${spring.data.redis.host}") String redisHost,
            @Value("${spring.data.redis.port}") int redisPort) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisHost);
        configuration.setPort(redisPort);
        return new JedisConnectionFactory(configuration);
    }

    @Bean
    @Autowired
    public static RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        // redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // redisTemplate.setHashKeySerializer(new JdkSerializationRedisSerializer());
        // redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
        // redisTemplate.setKeySerializer(new StringRedisSerializer());
        // redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        // redisTemplate.setEnableTransactionSupport(true);
        // redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
