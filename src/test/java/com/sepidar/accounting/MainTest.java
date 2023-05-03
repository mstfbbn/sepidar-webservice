package com.sepidar.accounting;

import com.google.gson.Gson;
import com.sepidar.accounting.models.common.ErrorResponse;
import com.sepidar.accounting.models.common.SepidarConfiguration;
import com.sepidar.accounting.models.general.GenerationVersion;
import com.sepidar.accounting.services.SepidarService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {

    @Test
    @BeforeAll
    static void uuid_to_byte_array_check () {
        UUID raw = UUID.fromString("3f0caa8c-2e86-488d-866f-036af2ec0ef5");
        String hexFormat = Long.toHexString(raw.getMostSignificantBits()) + Long.toHexString(raw.getLeastSignificantBits());
        assertEquals("3f0caa8c2e86488d866f036af2ec0ef5", hexFormat);
    }

    @Test
    void main() {
        SepidarService sepidarService = SepidarService.getInstance(SepidarConfiguration.of("101", "http://localhost:7373", "1000aaaa"));
        GenerationVersion generationVersion = sepidarService.getGenerationVersion();
        assertEquals("101", generationVersion.getGenerationVersion());
    }

    @Test
    @BeforeAll
    static void gson_convert_error_body() {
        String message = "test message...";
        String errorBodyString = "{\"Type\": 5, \"Message\": \"" + message + "\"}";
        ErrorResponse errorResponse = new Gson().fromJson(errorBodyString, ErrorResponse.class);

        assertAll(
                () -> assertEquals(message, errorResponse.getMessage()),
                () -> assertEquals(5, errorResponse.getType())
        );
    }
}