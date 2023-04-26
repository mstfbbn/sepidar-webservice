package com.sepidar.accounting.services;

import com.sepidar.accounting.models.requests.DeviceRegisterRequest;
import com.sepidar.accounting.models.requests.LoginRequest;
import com.sepidar.accounting.models.requests.NewInvoiceRequest;
import com.sepidar.accounting.models.responses.DeviceRegisterResponse;
import com.sepidar.accounting.models.responses.GenerationVersionResponse;
import com.sepidar.accounting.models.responses.InvoiceResponse;
import com.sepidar.accounting.models.responses.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface SepidarApiProxy {

    @POST("/api/Devices/Register")
    Call<DeviceRegisterResponse> deviceRegister(@Body DeviceRegisterRequest request);

    @POST("/api/users/login")
    Call<LoginResponse> userLogin(@Body LoginRequest request, @Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode);

    @GET("/api/IsAuthorized")
    Call<Boolean> isAuthenticated(@Header("GenerationVersion") String generationVersion, @Header("Authorization") String authorization, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode);

    @GET("/api/General/GenerationVersion")
    Call<GenerationVersionResponse> getGenerationVersion();

    @POST("/api/invoices")
    Call<InvoiceResponse> createNewInvoice(@Body NewInvoiceRequest request, @Header("GenerationVersion") String generationVersion, @Header("Authorization") String authorization, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode);
}
