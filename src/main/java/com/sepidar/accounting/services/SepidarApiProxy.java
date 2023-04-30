package com.sepidar.accounting.services;

import com.sepidar.accounting.models.administrative_divisions.AdministrativeDivision;
import com.sepidar.accounting.models.authentication.DeviceRegisterRequest;
import com.sepidar.accounting.models.authentication.DeviceRegisterResponse;
import com.sepidar.accounting.models.authentication.LoginRequest;
import com.sepidar.accounting.models.authentication.LoginResponse;
import com.sepidar.accounting.models.customer.Customer;
import com.sepidar.accounting.models.customer.CustomerAdd;
import com.sepidar.accounting.models.customer.CustomerEdit;
import com.sepidar.accounting.models.customer.CustomerGrouping;
import com.sepidar.accounting.models.general.GenerationVersion;
import com.sepidar.accounting.models.invoice.InvoiceResponse;
import com.sepidar.accounting.models.invoice.NewInvoiceRequest;
import com.sepidar.accounting.models.property.Property;
import com.sepidar.accounting.models.stock.Stock;
import com.sepidar.accounting.models.unit.Unit;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface SepidarApiProxy {

    @POST("/api/Devices/Register")
    Call<DeviceRegisterResponse> deviceRegister(@Body DeviceRegisterRequest request);

    @POST("/api/users/login")
    Call<LoginResponse> userLogin(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Body LoginRequest request);

    @GET("/api/IsAuthorized")
    Call<Boolean> isAuthenticated(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization);

    @GET("/api/General/GenerationVersion")
    Call<GenerationVersion> getGenerationVersion();

    @GET("/api/AdministrativeDivisions")
    Call<List<AdministrativeDivision>> getAdministrativeDivisions(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization);

    @GET("/api/CustomerGroupings")
    Call<List<CustomerGrouping>> getCustomerGroupings(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization);

    @GET("/api/Customers")
    Call<List<Customer>> getCustomers(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization);

    @GET("/api/Customers/{CustomerID}")
    Call<Customer> getCustomer(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization, @Path("CustomerID") Integer customerId);

    @POST("/api/Customers")
    Call<Customer> newCustomer(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization, @Body CustomerAdd customerAdd);

    @PUT("/api/Customers/{CustomerID}")
    Call<Customer> editCustomer(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization, @Body CustomerEdit customerEdit);

    @GET("/api/Units")
    Call<List<Unit>> getUnits(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization);

    @GET("/api/properties")
    Call<List<Property>> getProperties(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization);

    @GET("/api/Stocks")
    Call<List<Stock>> getStocks(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization);

    @POST("/api/invoices")
    Call<InvoiceResponse> createNewInvoice(@Body NewInvoiceRequest request, @Header("GenerationVersion") String generationVersion, @Header("Authorization") String authorization, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode);
}
