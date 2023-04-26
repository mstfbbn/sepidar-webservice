package com.sepidar.accounting;

import com.google.gson.Gson;
import com.sepidar.accounting.models.SepidarConfiguration;
import com.sepidar.accounting.models.responses.ErrorResponse;
import com.sepidar.accounting.models.responses.GenerationVersionResponse;
import com.sepidar.accounting.services.SepidarService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void main() {
        SepidarService sepidarService = SepidarService.getInstance(SepidarConfiguration.of("101", "http://localhost:7373", "1000aaaa", "test_user", "password1234"));
        GenerationVersionResponse generationVersionResponse = sepidarService.generationVersion();
        assertNotEquals(generationVersionResponse, null);
    }

    @Test
    @BeforeAll
    static void gson_convert_error_body() {
        String message = "test message...";
        String errorBodyString = "{\"Type\": 5, \"Message\": \"" + message + "\"}";
        ErrorResponse errorResponse = new Gson().fromJson(errorBodyString, ErrorResponse.class);

        assertAll(
                () -> assertEquals(errorResponse.getMessage(), message),
                () -> assertEquals(errorResponse.getType(), 5)
        );
    }
}