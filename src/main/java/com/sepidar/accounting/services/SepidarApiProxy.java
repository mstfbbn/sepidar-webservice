package com.sepidar.accounting.services;

import com.sepidar.accounting.models.administrative_divisions.AdministrativeDivision;
import com.sepidar.accounting.models.authentication.DeviceRegisterRequest;
import com.sepidar.accounting.models.authentication.DeviceRegisterResponse;
import com.sepidar.accounting.models.authentication.LoginRequest;
import com.sepidar.accounting.models.authentication.LoginResponse;
import com.sepidar.accounting.models.bank.Bank;
import com.sepidar.accounting.models.bank.BankAccount;
import com.sepidar.accounting.models.bank.ReceiptNew;
import com.sepidar.accounting.models.bank.ReceiptResult;
import com.sepidar.accounting.models.currency.Currency;
import com.sepidar.accounting.models.customer.Customer;
import com.sepidar.accounting.models.customer.CustomerAdd;
import com.sepidar.accounting.models.customer.CustomerEdit;
import com.sepidar.accounting.models.customer.CustomerGrouping;
import com.sepidar.accounting.models.general.GenerationVersion;
import com.sepidar.accounting.models.invoice.Invoice;
import com.sepidar.accounting.models.invoice.InvoiceBatch;
import com.sepidar.accounting.models.invoice.InvoiceBatchResult;
import com.sepidar.accounting.models.item.Inventory;
import com.sepidar.accounting.models.item.Item;
import com.sepidar.accounting.models.price_note.PriceNoteItem;
import com.sepidar.accounting.models.property.Property;
import com.sepidar.accounting.models.quotation.BatchResult;
import com.sepidar.accounting.models.quotation.Quotation;
import com.sepidar.accounting.models.quotation.QuotationBatch;
import com.sepidar.accounting.models.quotation.QuotationBatchResult;
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

    @GET("/api/Items")
    Call<List<Item>> getItems(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization);

    @GET("/api/Items/{itemID}/Image/")
    Call<String> getItemImage(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization, @Path("itemID") Integer itemId);

    @GET("/api/Items/Inventories/")
    Call<List<Inventory>> getInventories(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization);

    @GET("/api/PriceNoteItems")
    Call<List<PriceNoteItem>> getPriceNoteItems(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization);

    @GET("/api/Currencies")
    Call<List<Currency>> getCurrencies(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization);

    @GET("/api/Quotations")
    Call<List<Quotation>> getQuotations(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization, @Query("fromDate") String fromDate, @Query("toDate") String toDate);

    @GET("/api/Quotations/{id}")
    Call<Quotation> getQuotation(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization, @Path("id") Integer quotationId);

    @POST("/api/Quotations")
    Call<Quotation> createQuotation(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization, @Body QuotationBatch quotation);

    @POST("/api/Quotations/Batch/")
    Call<List<QuotationBatchResult>> createQuotationBatch(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization, @Body List<QuotationBatch> quotationBatchList);

    @POST("/api/Quotations/{quotationID}/Close/")
    Call<String> closeQuotation(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization, @Path("quotationID") Integer quotationId);

    @POST("/api/Quotations/Close/Batch")
    Call<List<BatchResult>> closeQuotationBatch(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization, @Body List<Integer> quotationIdList);

    @POST("/api/Quotations/{quotationID}/UnClose/")
    Call<String> uncloseQuotation(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization, @Path("quotationID") Integer quotationId);

    @POST("/api/Quotations/UnClose/Batch")
    Call<List<BatchResult>> uncloseQuotationBatch(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization, @Body List<Integer> quotationIdList);

    @DELETE("/api/Quotations/{quotationID}")
    Call<String> deleteQuotation(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization, @Path("quotationID") Integer quotationId);

    @DELETE("/api/Quotations/Batch")
    Call<List<BatchResult>> deleteQuotationBatch(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization, @Body List<Integer> quotationIdList);

    @GET("/api/invoices/")
    Call<List<Invoice>> getInvoices(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization);

    @GET("/api/invoices/{id}")
    Call<Invoice> getInvoice(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization, @Path("id") Integer invoiceId);

    @POST("/api/invoices")
    Call<Invoice> createInvoice(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization, @Body InvoiceBatch invoice);

    @POST("/api/Invoices/Batch/")
    Call<List<InvoiceBatchResult>> createInvoiceBatch(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization, @Body List<InvoiceBatch> invoiceBatchList);

    @POST("/api/Invoices/BasedOnQuotation/")
    Call<Invoice> createInvoiceBasedOnQuotation(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization, @Body Integer quotationId);

    @GET("/api/banks/")
    Call<List<Bank>> getBanks(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization);

    @GET("/api/BankAccounts")
    Call<List<BankAccount>> getBankAccounts(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization);

    @POST("/api/Receipts/BasedOnInvoice/")
    Call<ReceiptResult> saveReceiptBasedOnInvoice(@Header("GenerationVersion") String generationVersion, @Header("IntegrationID") String IntegrationID, @Header("ArbitraryCode") String arbitraryCode, @Header("EncArbitraryCode") String encArbitraryCode, @Header("Authorization") String authorization, @Body ReceiptNew receipt);
}
