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
    private List<Store> stores;

    public EnvironmentProperties() {
    }

    @Configuration
    @ConfigurationProperties(prefix = "thingsboard.stores")
    @Getter
    @Setter
    public static class Store {
        private String name;
        private List<Device> devices;

        public Store() {
        }

    }

    @Configuration
    @ConfigurationProperties(prefix = "thingsboard.stores.devices")
    @Getter
    @Setter
    public static class Device {
        private String name;
        private String accessToken;
        private List<Telemetry> telemetries;

        public Device() {
        }
    }

    @Configuration
    @ConfigurationProperties(prefix = "thingsboard.stores.devices.telemetries")
    @Getter
    @Setter
    public static class Telemetry {
        private String name;
        private Range range;

        public Telemetry() {
        }
    }

    @Configuration
    @ConfigurationProperties(prefix = "thingsboard.stores.devices.telemetries.range")
    @Getter
    @Setter
    public static class Range {
        private double min;
        private double max;

        public Range() {
        }
    }

}




