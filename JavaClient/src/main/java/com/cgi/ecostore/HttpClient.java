package com.cgi.ecostore;

import com.cgi.ecostore.config.EnvironmentProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@SpringBootApplication
public class HttpClient implements CommandLineRunner {

    @Autowired
    private EnvironmentProperties environment;

    private final Logger logger = LoggerFactory.getLogger("HttpClient");

    public static void main(String[] args) {
        SpringApplication.run(HttpClient.class, args).close();
    }

    public void run(String[] args) throws InterruptedException {
        logger.info("Startet ...");
        run();
    }

    private void run() throws InterruptedException {
        var devices = environment.getDevices();
        var url = environment.getUrl();

        while (true) {
            devices.forEach(
                    device -> {
                        try {
                            sendValue(url, device);
                        } catch (URISyntaxException | IOException | InterruptedException e) {
                            logger.error("Can not send values to " + device.getName(), e);
                        }
                    });
            Thread.sleep(1000);
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

        String telemetry = device.getTelemetry();
        String[] fields = telemetry.split(",");

        for (String field : fields) {
            String trimmedField = field.trim();
            payloadBuilder.append("\"").append(trimmedField).append("\":\"").append(getValue(device.getRange()))
                    .append("\",");
        }

        payloadBuilder.deleteCharAt(payloadBuilder.length() - 1); // Remove the trailing comma
        payloadBuilder.append("}");

        return HttpRequest.BodyPublishers.ofString(payloadBuilder.toString());
    }

    private String getValue(EnvironmentProperties.Range range) {
        int r = (int) ((Math.random() * (range.getMax()) - range.getMin()) + range.getMin());
        return String.valueOf(r);
    }

    private String getUrl(String url, String accessToken) {
        return url + "/api/v1/" + accessToken + "/telemetry";
    }
}