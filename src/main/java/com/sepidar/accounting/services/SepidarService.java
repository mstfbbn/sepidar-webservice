package com.sepidar.accounting.services;

import com.sepidar.accounting.models.SepidarConfiguration;
import com.sepidar.accounting.models.internal.DeviceRegisterResponseDTO;
import com.sepidar.accounting.models.requests.NewInvoiceRequest;
import com.sepidar.accounting.models.responses.GenerationVersionResponse;
import com.sepidar.accounting.models.responses.LoginResponse;
import com.sepidar.accounting.services.impl.SepidarServiceImpl;

public interface SepidarService {

    static SepidarService getInstance(SepidarConfiguration configuration) {
        return new SepidarServiceImpl(configuration);
    }

    DeviceRegisterResponseDTO register();

    LoginResponse login(String xmlString);

    boolean isAuthenticated(String xmlString, String token);

    GenerationVersionResponse generationVersion();

    void createNewInvoice(String xmlString, String token, NewInvoiceRequest request);
}
