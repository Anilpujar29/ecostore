package com.cgi.ecostore.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "thingsboard")
@Getter
@Setter
public class EnvironmentProperties {
    // https://stackoverflow.com/questions/32593014/mapping-list-in-yaml-to-list-of-objects-in-spring-boot

    @Autowired
    private Environment env;

    private String url;
    private List<Device> devices;

    public EnvironmentProperties() {
    }

    @Configuration
    @ConfigurationProperties(prefix = "thingsboard.devices")
    @Getter
    @Setter
    public static class Device {
        private String name;
        private String accessToken;
        private String telemetry;
        private Range range;

        public Device() {
        }
    }

    @Configuration
    @ConfigurationProperties(prefix = "thingsboard.devices.range")
    @Getter
    @Setter
    public static class Range {
        private int min;
        private int max;

        public Range() {
        }
    }

}
