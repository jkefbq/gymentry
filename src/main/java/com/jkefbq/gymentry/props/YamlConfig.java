package com.jkefbq.gymentry.props;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app")
@NoArgsConstructor
public class YamlConfig {

    private Kafka kafka;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Kafka {
        private Topics topics;

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Topics {
            private String subscriptionPurchases;
            private String confirmedSubscriptions;
        }
    }
}
