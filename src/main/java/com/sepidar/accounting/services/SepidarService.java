package com.sepidar.accounting.services;

import com.sepidar.accounting.models.SepidarConfiguration;
import com.sepidar.accounting.models.requests.NewInvoiceRequest;
import com.sepidar.accounting.services.impl.SepidarServiceImpl;

public interface SepidarService {

    static SepidarService getInstance(SepidarConfiguration configuration) {
        return new SepidarServiceImpl(configuration);
    }

    void register();

    void login();

    boolean isAuthenticated();

    void createNewInvoice(NewInvoiceRequest request);
}
