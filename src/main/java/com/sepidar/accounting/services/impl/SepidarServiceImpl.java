package com.sepidar.accounting.services.impl;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.sepidar.accounting.exceptions.SepidarGlobalException;
import com.sepidar.accounting.models.administrative_divisions.AdministrativeDivision;
import com.sepidar.accounting.models.authentication.*;
import com.sepidar.accounting.models.bank.Bank;
import com.sepidar.accounting.models.bank.BankAccount;
import com.sepidar.accounting.models.bank.ReceiptNew;
import com.sepidar.accounting.models.bank.ReceiptResult;
import com.sepidar.accounting.models.common.ErrorResponse;
import com.sepidar.accounting.models.common.SepidarRequestHeader;
import com.sepidar.accounting.models.currency.Currency;
import com.sepidar.accounting.models.customer.Customer;
import com.sepidar.accounting.models.customer.CustomerAdd;
import com.sepidar.accounting.models.customer.CustomerEdit;
import com.sepidar.accounting.models.customer.CustomerGrouping;
import com.sepidar.accounting.models.general.GenerationVersion;
import com.sepidar.accounting.models.invoice.Invoice;
import com.sepidar.accounting.models.invoice.InvoiceBatch;
import com.sepidar.accounting.models.invoice.InvoiceBatchResult;
import com.sepidar.accounting.models.invoice.InvoiceNewOnQuotation;
import com.sepidar.accounting.models.item.Inventory;
import com.sepidar.accounting.models.item.Item;
import com.sepidar.accounting.models.price_note.PriceNoteItem;
import com.sepidar.accounting.models.property.Property;
import com.sepidar.accounting.models.quotation.BatchResult;
import com.sepidar.accounting.models.quotation.Quotation;
import com.sepidar.accounting.models.quotation.QuotationBatch;
import com.sepidar.accounting.models.quotation.QuotationBatchResult;
import com.sepidar.accounting.models.sale_type.SaleType;
import com.sepidar.accounting.models.stock.Stock;
import com.sepidar.accounting.models.unit.Unit;
import com.sepidar.accounting.services.SepidarApiProxy;
import com.sepidar.accounting.services.SepidarService;
import com.sepidar.accounting.utils.EncryptionUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * NOTE: every method assumes that configuration details are set before.
 */
@Slf4j
@AllArgsConstructor
public class SepidarServiceImpl implements SepidarService {

    private final String API_VERSION;
    private final String API_URL;
    private final String DEVICE_SERIAL_ID;

    private static RsaRawPublicKey getRSAFromXmlString(String xmlString, String requestId) {

        if (Strings.isNullOrEmpty(xmlString)) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, "xmlString is null or empty");
        }

        try {
            StringReader sr = new StringReader(xmlString);
            JAXBContext jaxbContext = JAXBContext.newInstance(RsaKeyValue.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            RsaKeyValue rsaKeyValue = (RsaKeyValue) unmarshaller.unmarshal(sr);

            if (rsaKeyValue == null || Strings.isNullOrEmpty(rsaKeyValue.getExponent()) || Strings.isNullOrEmpty(rsaKeyValue.getModulus())) {
                LOGGER.error("getRSAFromXmlString(req={}). RSA response or rsaKeyValue object is null or exponent or modulus are null or empty for xmlString <{}>.", requestId, xmlString);
                throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, "rsa response is null or empty.");
            }

            return RsaRawPublicKey.of(
                    Base64.getDecoder().decode(rsaKeyValue.getModulus().getBytes(StandardCharsets.UTF_8)),
                    Base64.getDecoder().decode(rsaKeyValue.getExponent().getBytes(StandardCharsets.UTF_8))
            );
        } catch (JAXBException e) {
            LOGGER.error("getRSAFromXmlString(req=" + requestId + "). " + e.getMessage(), e);
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, e.getMessage());
        }
    }

    private static <T> T handleApiCallWithReturn(Call<T> call, String requestId) {
        try {
            Response<T> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 1, exception.getMessage());
        }
    }

    private static <T> void handleApiCallWithoutReturn(Call<T> call, String requestId) {
        try {
            Response<T> response = call.execute();
            if (!response.isSuccessful()) {
                handleErrorResponse(response, requestId);
            }
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 1, exception.getMessage());
        }
    }

    /**
     * NOTE: defined two generics because it's possible that methods' output not be the same as sepidar's response. Therefore, response wouldn't be of type 'T'.
     * e.g. {@link #register()}
     */
    private static <T, E> E handleErrorResponse(Response<T> response, String requestId) {
        if (response.errorBody() == null) {
            LOGGER.error("handleErrorResponse(req={}). error response body is null while response is not successful.", requestId);
            throw new SepidarGlobalException(response.code(), 1, "error response body is null.");
        }

        try {
            ErrorResponse errorResponse = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
            throw new SepidarGlobalException(response.code(), errorResponse.getType(), errorResponse.getMessage());
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            LOGGER.error("handleErrorResponse(req={}). error happened parsing errorBody with message <{}>", requestId, exception.getMessage());
            throw new SepidarGlobalException(response.code(), 1, exception.getMessage());
        }
    }

    @Override
    public DeviceRegisterResponseDTO register() {
        String requestId = getRandomUniqueId();

        byte[] iv = EncryptionUtil.aesGenerateRandomIv();
        String ivBase64Encoded = Base64.getEncoder().encodeToString(iv);
        String cypher = EncryptionUtil.aesEncrypt(getEncryptionKey().getBytes(), iv, getIntegrationId().getBytes());

        if (Strings.isNullOrEmpty(cypher)) {
            LOGGER.error("register(req={}). cypher is null or empty for ivBase64Encoded <{}> and integrationId <{}>.", requestId, ivBase64Encoded, getIntegrationId());
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, "cypher is null or empty in register.");
        }

        Call<DeviceRegisterResponse> deviceRegisterResponseCall = getSepidarApi().deviceRegister(
                DeviceRegisterRequest.of(
                        cypher,
                        ivBase64Encoded,
                        getIntegrationId()
                )
        );

        LOGGER.debug("register(req={}). api call started with base64 encoded of iv <{}>, base64 encoded of cypher <{}> and integrationId <{}>.", requestId, ivBase64Encoded, cypher, getIntegrationId());

        try {
            Response<DeviceRegisterResponse> response = deviceRegisterResponseCall.execute();

            if (response.isSuccessful()) {
                if (response.body() == null) {
                    LOGGER.error("register(req={}). register api responded with null body", requestId);
                    throw new SepidarGlobalException(response.code(), 0, "sepidar response is successful but body is null");
                }

                LOGGER.debug("register(req={}). register api responded with iv <{}>, cypher <{}> and device title <{}>.", requestId, response.body().getIv(), response.body().getCypher(), response.body().getDeviceTitle());

                byte[] ivDecodedByteArray = Base64.getDecoder().decode(response.body().getIv());
                byte[] cypherDecodedByteArray = Base64.getDecoder().decode(response.body().getCypher());

                String xmlString = EncryptionUtil.aesDecrypt(ivDecodedByteArray, getEncryptionKey().getBytes(), cypherDecodedByteArray);

                if (Strings.isNullOrEmpty(xmlString)) {
                    LOGGER.error("register(req={}). xmlString is null or empty with iv <{}> and cypher <{}>.", requestId, response.body().getIv(), response.body().getCypher());
                    throw new SepidarGlobalException(response.code(), 0, "xml containing rsa public key is null or empty.");
                }

                return DeviceRegisterResponseDTO.of(xmlString, response.body().getDeviceTitle());
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("(req=" + requestId + "). " + e.getMessage(), e);
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, e.getMessage());
        }
    }

    @Override
    public LoginResponse login(String rsaPublicKeyXmlString, String username, String password) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, rsaPublicKeyXmlString, null);
        Call<LoginResponse> call = getSepidarApi().userLogin(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(),
                LoginRequest.of(
                        username,
                        EncryptionUtil.md5Encrypt(password)
                )
        );
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public boolean isAuthenticated(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<Boolean> call = getSepidarApi().isAuthenticated(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public GenerationVersion getGenerationVersion() {
        String requestId = getRandomUniqueId();
        Call<GenerationVersion> call = getSepidarApi().getGenerationVersion();
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public List<AdministrativeDivision> getAdministrativeDivisions(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<AdministrativeDivision>> call = getSepidarApi().getAdministrativeDivisions(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public List<CustomerGrouping> getCustomerGroupings(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<CustomerGrouping>> call = getSepidarApi().getCustomerGroupings(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public List<Customer> getCustomers(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<Customer>> call = getSepidarApi().getCustomers(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public Customer getCustomer(String xmlString, String token, Integer customerId) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<Customer> call = getSepidarApi().getCustomer(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), customerId);
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public Customer createCustomer(String xmlString, String token, CustomerAdd customerAdd) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<Customer> call = getSepidarApi().newCustomer(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), customerAdd);
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public Customer editCustomer(String xmlString, String token, CustomerEdit customerEdit) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<Customer> call = getSepidarApi().editCustomer(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), customerEdit);
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public List<Unit> getUnits(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<Unit>> call = getSepidarApi().getUnits(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public List<Property> getProperties(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<Property>> call = getSepidarApi().getProperties(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public List<Stock> getStocks(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<Stock>> call = getSepidarApi().getStocks(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public List<Item> getItems(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<Item>> call = getSepidarApi().getItems(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public String getItemImage(String xmlString, String token, Integer itemId) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<String> call = getSepidarApi().getItemImage(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), itemId);
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public List<Inventory> getInventories(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<Inventory>> call = getSepidarApi().getInventories(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public List<SaleType> getSaleTypes(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<SaleType>> call = getSepidarApi().getSaleTypes(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public List<PriceNoteItem> getPriceNoteItems(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<PriceNoteItem>> call = getSepidarApi().getPriceNoteItems(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public List<Currency> getCurrencies(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<Currency>> call = getSepidarApi().getCurrencies(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public List<Quotation> getQuotations(String xmlString, String token, String fromDate, String toDate) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<Quotation>> call = getSepidarApi().getQuotations(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), fromDate, toDate);
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public Quotation getQuotation(String xmlString, String token, Integer quotationId) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<Quotation> call = getSepidarApi().getQuotation(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), quotationId);
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public Quotation createQuotation(String xmlString, String token, QuotationBatch quotation) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<Quotation> call = getSepidarApi().createQuotation(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), quotation);
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public List<QuotationBatchResult> createQuotationBatch(String xmlString, String token, List<QuotationBatch> quotationBatchList) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<QuotationBatchResult>> call = getSepidarApi().createQuotationBatch(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), quotationBatchList);
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public void closeQuotation(String xmlString, String token, Integer quotationId) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<String> call = getSepidarApi().closeQuotation(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), quotationId);
        handleApiCallWithoutReturn(call, requestId);
    }

    @Override
    public List<BatchResult> closeQuotationBatch(String xmlString, String token, List<Integer> quotationIdList) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<BatchResult>> call = getSepidarApi().closeQuotationBatch(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), quotationIdList);
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public void uncloseQuotation(String xmlString, String token, Integer quotationId) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<String> call = getSepidarApi().uncloseQuotation(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), quotationId);
        handleApiCallWithoutReturn(call, requestId);
    }

    @Override
    public List<BatchResult> uncloseQuotationBatch(String xmlString, String token, List<Integer> quotationIdList) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<BatchResult>> call = getSepidarApi().uncloseQuotationBatch(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), quotationIdList);
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public void deleteQuotation(String xmlString, String token, Integer quotationId) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<String> call = getSepidarApi().deleteQuotation(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), quotationId);
        handleApiCallWithoutReturn(call, requestId);
    }

    @Override
    public List<BatchResult> deleteQuotationBatch(String xmlString, String token, List<Integer> quotationIdList) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<BatchResult>> call = getSepidarApi().deleteQuotationBatch(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), quotationIdList);
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public List<Invoice> getInvoices(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<Invoice>> call = getSepidarApi().getInvoices(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public Invoice getInvoice(String xmlString, String token, Integer invoiceId) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<Invoice> call = getSepidarApi().getInvoice(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), invoiceId);
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public Invoice createInvoice(String xmlString, String token, InvoiceBatch invoice) {
        String requestId = UUID.randomUUID().toString();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<Invoice> call = getSepidarApi().createInvoice(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), invoice);
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public List<InvoiceBatchResult> createInvoiceBatch(String xmlString, String token, List<InvoiceBatch> invoiceBatchList) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<InvoiceBatchResult>> call = getSepidarApi().createInvoiceBatch(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), invoiceBatchList);
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public Invoice createInvoiceBasedOnQuotation(String xmlString, String token, Integer quotationId) {
        String requestId = UUID.randomUUID().toString();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<Invoice> call = getSepidarApi().createInvoiceBasedOnQuotation(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), InvoiceNewOnQuotation.of(quotationId));
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public List<Bank> getBanks(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<Bank>> call = getSepidarApi().getBanks(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public List<BankAccount> getBankAccounts(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<BankAccount>> call = getSepidarApi().getBankAccounts(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        return handleApiCallWithReturn(call, requestId);
    }

    @Override
    public ReceiptResult saveReceiptBasedOnInvoice(String xmlString, String token, ReceiptNew receipt) {
        String requestId = UUID.randomUUID().toString();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<ReceiptResult> call = getSepidarApi().saveReceiptBasedOnInvoice(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), receipt);
        return handleApiCallWithReturn(call, requestId);
    }

    private SepidarRequestHeader getRequestHeader(String requestId, String xmlString, String token) {
        RsaRawPublicKey rsaRawPublicKey = getRSAFromXmlString(xmlString, requestId);
        UUID arbitraryCode = UUID.randomUUID();
        String arbitraryCodeEncrypted = EncryptionUtil.rsaEncryptionForUUID(rsaRawPublicKey.getModulus(), rsaRawPublicKey.getExponent(), arbitraryCode);
        if (Strings.isNullOrEmpty(arbitraryCodeEncrypted)) {
            LOGGER.error("getRequestHeader(req={}). arbitraryCodeEncrypted is null or empty for arbitraryCode <{}>, base64 of rsaModulus <{}> and base64 of rsaExponent <{}>", requestId, arbitraryCode, rsaRawPublicKey.getModulus(), rsaRawPublicKey.getExponent());
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, "invalid rsa encryption.");
        }

        return SepidarRequestHeader.of(API_VERSION, getIntegrationId(), arbitraryCode.toString(), arbitraryCodeEncrypted, Strings.isNullOrEmpty(token) ? null : "Bearer " + token);
    }

    private SepidarApiProxy getSepidarApi() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        return retrofit.create(SepidarApiProxy.class);
    }

    private String getEncryptionKey() {
        return DEVICE_SERIAL_ID.concat(DEVICE_SERIAL_ID);
    }

    private String getIntegrationId() {
        return DEVICE_SERIAL_ID.substring(0, 4);
    }

    private String getRandomUniqueId() {
        return UUID.randomUUID().toString();
    }
}
