package net.codejava.budget_tracker.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;



@Configuration
public class GoogleSheetsConfig {
    private static final List <String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/spreadsheets");
    private static final String APPLICATION_NAME = "MonTrack";

    @Value("${google.sheets.credentials.path:}")
    private String credentialsPath;

    @Value("${google.sheets.credentials.json:}")
    private String credentialsJson;


    @Bean
    public Sheets sheetsService() throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        GoogleCredentials credentials = loadCredentials().createScoped(SCOPES);
        HttpCredentialsAdapter credentialsAdapter = new HttpCredentialsAdapter(credentials);

        return new Sheets.Builder(httpTransport, jsonFactory, credentialsAdapter)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    

    private GoogleCredentials loadCredentials() throws IOException {
        if (credentialsJson != null && !credentialsJson.isBlank()) {
            try (InputStream in = new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8))) {
                return GoogleCredentials.fromStream(in);
            }
        }
        if (credentialsPath != null && !credentialsPath.isBlank()) {
            try (InputStream in = new FileInputStream(credentialsPath)) {
                return GoogleCredentials.fromStream(in);
            }
        }
        throw new IllegalStateException(
                "Google Sheets credentials are not configured. Set the GOOGLE_SHEETS_CREDENTIALS_PATH " +
                        "or GOOGLE_SHEETS_CREDENTIALS_JSON environment variable. See README.md for setup steps.");
    }



}
