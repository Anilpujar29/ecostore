package com.cgi.ecostore;

import com.cgi.ecostore.config.EnvironmentProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class HttpClient implements CommandLineRunner {

    @Autowired
    private EnvironmentProperties environment;
    private Boolean alarm;
    private int occupancy;
    private int occupancy1;

    private int energy = 100;

    private final Logger logger = LoggerFactory.getLogger("HttpClient");

    public static void main(String[] args) {
        SpringApplication.run(HttpClient.class, args).close();
    }

    public void run(String[] args) throws InterruptedException {
        logger.info("Startet ...");
        run();
    }

    private void run() throws InterruptedException {

        var stores = environment.getStores();
        var url = environment.getUrl();

        this.alarm = true;
        this.occupancy = 8;
        this.occupancy1 = 6;




        while (true) {
            stores.forEach(
                    store -> {
                            store.getDevices().forEach(
                                    device -> {
                                        try {
                                            sendValue(url, device);
                                            Thread.sleep(100);
                                        } catch (URISyntaxException | IOException | InterruptedException e) {
                                            logger.error("Can not send values to " + device.getName(), e);
                                        }
                                    }
                            );
                    });

        }
    }

    private void sendValue(String url, EnvironmentProperties.Device device)
            throws URISyntaxException, IOException, InterruptedException {
        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(getUrl(url, device.getAccessToken())))
                .header("Content-Type", "application/json")
                .POST(getRequestBody(device))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    // private HttpRequest.BodyPublisher getRequestBody(EnvironmentProperties.Device
    // device) {
    // return HttpRequest.BodyPublishers.ofString(
    // "{\"" + device.getTemperature() + "\":\"" + getValue(device.getRange()) +
    // "\",\"" + device.getHumidity()
    // + "\":\""
    // + getValue(device.getRange()) + "\",\"" + device.getTvoc() + "\":\""
    // + getValue(device.getRange()) + "\",\""
    // + device.getCo2() + "\":\"" + getValue(device.getRange()) + "\"}");
    // }
    private HttpRequest.BodyPublisher getRequestBody(EnvironmentProperties.Device device) {
        StringBuilder payloadBuilder = new StringBuilder("{");

        var telemetries = device.getTelemetries();
        telemetries.forEach( telemetry -> {
                payloadBuilder.append("\"").append(telemetry.getName()).append("\":\"").append(getValue(telemetry.getRange(), telemetry))
                        .append("\",");
                    }
            );


        payloadBuilder.deleteCharAt(payloadBuilder.length()-1); // Remove the trailing comma
        payloadBuilder.append("}");

        return HttpRequest.BodyPublishers.ofString(payloadBuilder.toString());
    }

    private String getValue(EnvironmentProperties.Range range, EnvironmentProperties.Telemetry telemetry) {
        if(telemetry.getName().equals("enabled")) return "true";
        if(telemetry.getName().equals("on")) return "false";

        List<String> integers = Arrays.asList("people_count", "temperature");
        double r = (Math.random() * (range.getMax() - range.getMin())) + range.getMin();
        return String.valueOf(integers.contains(telemetry.getName()) ? (int) r : new DecimalFormat("#.##").format(r));
    }

    private String getUrl(String url, String accessToken) {
        return url + "/api/v1/" + accessToken + "/telemetry";
    }
}