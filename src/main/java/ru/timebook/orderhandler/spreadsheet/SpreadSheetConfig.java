package ru.timebook.orderhandler.spreadsheet;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import ru.timebook.orderhandler.spreadsheet.exceptions.SpreadSheetException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

@Configuration
public class SpreadSheetConfig {
    @Bean
    JsonFactory createJsonFactory() {
        return JacksonFactory.getDefaultInstance();
    }

    @Bean
    NetHttpTransport createNetHttpTransport() {
        try {
            return GoogleNetHttpTransport.newTrustedTransport();
        } catch (Exception e) {
            throw new SpreadSheetException(e);
        }
    }

    @Bean
    Credential getCredential(
            ResourceLoader resourceLoader,
            JsonFactory jsonFactory,
            NetHttpTransport netHttpTransport,
            @Value("${spreadsheet.credential_file_name}") String credentialFileName,
            @Value("${spreadsheet.token_directory}") String tokenDirectory
    ) {
        Resource resource = resourceLoader.getResource("classpath:" + credentialFileName);
        try {
            InputStream input = resource.getInputStream();
            if (input == null) {
                throw new SpreadSheetException("Can't load credential file: " + credentialFileName);
            }

            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(input));
            List<String> scopes = Collections.singletonList(SheetsScopes.SPREADSHEETS);

            // Build flow and trigger user authorization request.
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    netHttpTransport, jsonFactory, clientSecrets, scopes)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokenDirectory)))
                    .setAccessType("offline")
                    .build();
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

            return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        } catch (Exception exception) {
            throw new SpreadSheetException(exception);
        }
    }

    @Bean
    Sheets createSheetsInstance(
            NetHttpTransport netHttpTransport,
            JsonFactory jsonFactory,
            @Value("${application.name}") String applicationName,
            Credential credential
    ) {
        return new Sheets.Builder(netHttpTransport, jsonFactory, credential)
                .setApplicationName(applicationName)
                .build();
    }
}
