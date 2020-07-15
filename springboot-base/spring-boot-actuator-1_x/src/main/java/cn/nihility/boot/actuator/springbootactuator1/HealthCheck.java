package cn.nihility.boot.actuator.springbootactuator1;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * HealthCheck
 *
 * @author dandelion
 * @date 2020-04-09 22:30
 */
@Component
public class HealthCheck implements HealthIndicator {
    @Override
    public Health health() {
        int errorCode = check();
        if (0 != errorCode) {
            return Health.down()
                    .withDetail("Error Code", errorCode)
                    .build();
        }
        return Health.up().build();
    }

    private int check() {
        return 0;
    }
}
