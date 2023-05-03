package com.sepidar.accounting.services.impl;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.sepidar.accounting.constants.AdministrativeDivisionType;
import com.sepidar.accounting.exceptions.SepidarGlobalException;
import com.sepidar.accounting.models.administrative_divisions.AdministrativeDivision;
import com.sepidar.accounting.models.administrative_divisions.AdministrativeDivisionDTO;
import com.sepidar.accounting.models.authentication.*;
import com.sepidar.accounting.models.bank.Bank;
import com.sepidar.accounting.models.bank.BankAccount;
import com.sepidar.accounting.models.bank.ReceiptNew;
import com.sepidar.accounting.models.bank.ReceiptResult;
import com.sepidar.accounting.models.common.ErrorResponse;
import com.sepidar.accounting.models.common.SepidarConfiguration;
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
import com.sepidar.accounting.services.SepidarApiProxy;
import com.sepidar.accounting.services.SepidarService;
import com.sepidar.accounting.utils.EncryptionUtil;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * NOTE: every method assumes that configuration details are set before.
 */
@Slf4j
public class SepidarServiceImpl implements SepidarService {

    private final String API_VERSION;
    private final String API_URL;
    private final String DEVICE_SERIAL_ID;

    public SepidarServiceImpl(SepidarConfiguration configuration) {
        this.API_VERSION = configuration.getApiVersion();
        this.API_URL = configuration.getUrl();
        this.DEVICE_SERIAL_ID = configuration.getDeviceId();
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
                new DeviceRegisterRequest(
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

        SepidarRequestHeader headers = getRequestHeader(requestId, rsaPublicKeyXmlString, "");
        LOGGER.debug("login(req={}). api call started with arbitraryCode <{}>, arbitraryCodeEncrypted <{}>, integrationId <{}> and username <{}>", requestId, headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getIntegrationId(), username);

        Call<LoginResponse> loginResponseCall = getSepidarApi().userLogin(
                headers.getGenerationVersion(),
                headers.getIntegrationId(),
                headers.getArbitraryCode(),
                headers.getArbitraryCodeEncoded(),
                new LoginRequest(
                        username,
                        EncryptionUtil.md5Encrypt(password)
                )
        );

        try {
            Response<LoginResponse> response = loginResponseCall.execute();

            if (response.isSuccessful()) {
                if (response.body() == null) {
                    LOGGER.error("login(req={}). api responded with null body", requestId);
                    throw new SepidarGlobalException(response.code(), 0, "sepidar response is successful but body is null");
                }
                LOGGER.debug("login(req={}). api responded with userId <{}>, title <{}>.", requestId, response.body().getUserId(), response.body().getTitle());
                return response.body();
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
    public boolean isAuthenticated(String xmlString, String token) {
        String requestId = getRandomUniqueId();

        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        LOGGER.info("isAuthenticated(req={}). authenticated api call started with arbitraryCode <{}>, arbitraryCodeEncrypted <{}> and integrationId <{}>.", requestId, headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getIntegrationId());

        Call<Boolean> authenticatedResponseCall = getSepidarApi().isAuthenticated(
                headers.getGenerationVersion(),
                headers.getIntegrationId(),
                headers.getArbitraryCode(),
                headers.getArbitraryCodeEncoded(),
                headers.getToken()
        );

        try {
            Response<Boolean> response = authenticatedResponseCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            LOGGER.debug("authenticated(req={}). isAuthenticated api responded with error code <{}> and message <{}>", requestId, response.code(), (response.errorBody() == null) ? null : response.errorBody().string());
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("(req=" + requestId + "). " + e.getMessage(), e);
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, e.getMessage());
        }
    }

    @Override
    public GenerationVersion getGenerationVersion() {
        String requestId = getRandomUniqueId();
        Call<GenerationVersion> generationVersionCall = getSepidarApi().getGenerationVersion();
        try {
            Response<GenerationVersion> response = generationVersionCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (IOException ioException) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, ioException.getMessage());
        }
    }

    @Override
    public List<AdministrativeDivisionDTO> getAdministrativeDivisions(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<AdministrativeDivision>> administrativeDivisionCall = getSepidarApi().getAdministrativeDivisions(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        try {
            Response<List<AdministrativeDivision>> response = administrativeDivisionCall.execute();
            if (response.isSuccessful()) {
                assert response.body() != null;
                return response.body().stream()
                        .map(item ->
                                AdministrativeDivisionDTO.of(
                                        item.getDivisionId(), item.getTitle(), AdministrativeDivisionType.of(item.getType()), item.getParentDivisionRef())
                        )
                        .collect(Collectors.toList());
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public List<CustomerGrouping> getCustomerGroupings(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<CustomerGrouping>> customerGroupingCall = getSepidarApi().getCustomerGroupings(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        try {
            Response<List<CustomerGrouping>> response = customerGroupingCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public List<Customer> getCustomers(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<Customer>> customerCall = getSepidarApi().getCustomers(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        try {
            Response<List<Customer>> response = customerCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public Customer getCustomer(String xmlString, String token, Integer customerId) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<Customer> customerCall = getSepidarApi().getCustomer(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), customerId);
        try {
            Response<Customer> response = customerCall.execute();
            if (response.isSuccessful()) {
                // TODO: add mapper for customer
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public Customer createCustomer(String xmlString, String token, CustomerAdd customerAdd) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<Customer> customerAddCall = getSepidarApi().newCustomer(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), customerAdd);
        try {
            Response<Customer> response = customerAddCall.execute();
            if (response.isSuccessful()) {
                // TODO: add mapper for customer
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public Customer editCustomer(String xmlString, String token, CustomerEdit customerEdit) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<Customer> customerEditCall = getSepidarApi().editCustomer(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), customerEdit);
        try {
            Response<Customer> response = customerEditCall.execute();
            if (response.isSuccessful()) {
                // TODO: add mapper for customer
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public List<Unit> getUnits(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<Unit>> unitsCall = getSepidarApi().getUnits(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        try {
            Response<List<Unit>> response = unitsCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public List<Property> getProperties(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<Property>> propertyCall = getSepidarApi().getProperties(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        try {
            Response<List<Property>> response = propertyCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public List<Stock> getStocks(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<Stock>> stockCall = getSepidarApi().getStocks(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        try {
            Response<List<Stock>> response = stockCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public List<Item> getItems(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<Item>> itemCall = getSepidarApi().getItems(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        try {
            Response<List<Item>> response = itemCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public String getItemImage(String xmlString, String token, Integer itemId) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<String> itemImageCall = getSepidarApi().getItemImage(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), itemId);
        try {
            Response<String> response = itemImageCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public List<Inventory> getInventories(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<Inventory>> inventoryCall = getSepidarApi().getInventories(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        try {
            Response<List<Inventory>> response = inventoryCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public List<PriceNoteItem> getPriceNoteItems(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<PriceNoteItem>> priceNoteCall = getSepidarApi().getPriceNoteItems(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        try {
            Response<List<PriceNoteItem>> response = priceNoteCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public List<Currency> getCurrencies(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<Currency>> currencyCall = getSepidarApi().getCurrencies(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        try {
            Response<List<Currency>> response = currencyCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public List<Quotation> getQuotations(String xmlString, String token, String fromDate, String toDate) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<Quotation>> quotationCall = getSepidarApi().getQuotations(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), fromDate, toDate);
        try {
            Response<List<Quotation>> response = quotationCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public Quotation getQuotation(String xmlString, String token, Integer quotationId) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<Quotation> quotationCall = getSepidarApi().getQuotation(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), quotationId);
        try {
            Response<Quotation> response = quotationCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public Quotation createQuotation(String xmlString, String token, QuotationBatch quotation) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<Quotation> quotationCall = getSepidarApi().createQuotation(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), quotation);
        try {
            Response<Quotation> response = quotationCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public List<QuotationBatchResult> createQuotationBatch(String xmlString, String token, List<QuotationBatch> quotationBatchList) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<QuotationBatchResult>> quotationCall = getSepidarApi().createQuotationBatch(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), quotationBatchList);
        try {
            Response<List<QuotationBatchResult>> response = quotationCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public void closeQuotation(String xmlString, String token, Integer quotationId) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<String> quotationCall = getSepidarApi().closeQuotation(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), quotationId);
        try {
            Response<String> response = quotationCall.execute();
            if (!response.isSuccessful()) {
                handleErrorResponse(response, requestId);
            }
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public List<BatchResult> closeQuotationBatch(String xmlString, String token, List<Integer> quotationIdList) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<BatchResult>> quotationCall = getSepidarApi().closeQuotationBatch(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), quotationIdList);
        try {
            Response<List<BatchResult>> response = quotationCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public void uncloseQuotation(String xmlString, String token, Integer quotationId) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<String> quotationCall = getSepidarApi().uncloseQuotation(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), quotationId);
        try {
            Response<String> response = quotationCall.execute();
            if (!response.isSuccessful()) {
                handleErrorResponse(response, requestId);
            }
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public List<BatchResult> uncloseQuotationBatch(String xmlString, String token, List<Integer> quotationIdList) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<BatchResult>> quotationCall = getSepidarApi().uncloseQuotationBatch(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), quotationIdList);
        try {
            Response<List<BatchResult>> response = quotationCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public void deleteQuotation(String xmlString, String token, Integer quotationId) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<String> quotationCall = getSepidarApi().deleteQuotation(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), quotationId);
        try {
            Response<String> response = quotationCall.execute();
            if (!response.isSuccessful()) {
                handleErrorResponse(response, requestId);
            }
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public List<BatchResult> deleteQuotationBatch(String xmlString, String token, List<Integer> quotationIdList) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<BatchResult>> quotationCall = getSepidarApi().deleteQuotationBatch(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), quotationIdList);
        try {
            Response<List<BatchResult>> response = quotationCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public List<Invoice> getInvoices(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<Invoice>> invoiceCall = getSepidarApi().getInvoices(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        try {
            Response<List<Invoice>> response = invoiceCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public Invoice getInvoice(String xmlString, String token, Integer invoiceId) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<Invoice> Call = getSepidarApi().getInvoice(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), invoiceId);
        try {
            Response<Invoice> response = Call.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public Invoice createInvoice(String xmlString, String token, InvoiceBatch invoice) {
        String requestId = UUID.randomUUID().toString();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<Invoice> invoiceResponseCall = getSepidarApi().createInvoice(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), invoice);

        try {
            Response<Invoice> response = invoiceResponseCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public List<InvoiceBatchResult> createInvoiceBatch(String xmlString, String token, List<InvoiceBatch> invoiceBatchList) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<InvoiceBatchResult>> invoiceCall = getSepidarApi().createInvoiceBatch(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), invoiceBatchList);
        try {
            Response<List<InvoiceBatchResult>> response = invoiceCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public Invoice createInvoiceBasedOnQuotation(String xmlString, String token, Integer quotationId) {
        String requestId = UUID.randomUUID().toString();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<Invoice> invoiceResponseCall = getSepidarApi().createInvoiceBasedOnQuotation(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), quotationId);
        try {
            Response<Invoice> response = invoiceResponseCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public List<Bank> getBanks(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<Bank>> call = getSepidarApi().getBanks(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        try {
            Response<List<Bank>> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public List<BankAccount> getBankAccounts(String xmlString, String token) {
        String requestId = getRandomUniqueId();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<List<BankAccount>> call = getSepidarApi().getBankAccounts(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken());
        try {
            Response<List<BankAccount>> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

    @Override
    public ReceiptResult saveReceiptBasedOnInvoice(String xmlString, String token, ReceiptNew receipt) {
        String requestId = UUID.randomUUID().toString();
        SepidarRequestHeader headers = getRequestHeader(requestId, xmlString, token);
        Call<ReceiptResult> call = getSepidarApi().saveReceiptBasedOnInvoice(headers.getGenerationVersion(), headers.getIntegrationId(), headers.getArbitraryCode(), headers.getArbitraryCodeEncoded(), headers.getToken(), receipt);
        try {
            Response<ReceiptResult> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (SepidarGlobalException e) {
            throw e;
        } catch (Exception exception) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, exception.getMessage());
        }
    }

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

    /**
     * NOTE: defined two generics because it's possible that methods' output not be the same as sepidar's response. Therefore, response wouldn't be of type 'T'.
     * e.g. {@link #register()}
     */
    private <T, E> E handleErrorResponse(Response<T> response, String requestId) {
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

    private SepidarRequestHeader getRequestHeader(String requestId, String xmlString, String token) {
        RsaRawPublicKey rsaRawPublicKey = getRSAFromXmlString(xmlString, requestId);
        UUID arbitraryCode = UUID.randomUUID();
        String arbitraryCodeEncrypted = EncryptionUtil.rsaEncryptionForUUID(rsaRawPublicKey.getModulus(), rsaRawPublicKey.getExponent(), arbitraryCode);
        if (Strings.isNullOrEmpty(arbitraryCodeEncrypted)) {
            LOGGER.error("getRequestHeader(req={}). arbitraryCodeEncrypted is null or empty for arbitraryCode <{}>, base64 of rsaModulus <{}> and base64 of rsaExponent <{}>", requestId, arbitraryCode, rsaRawPublicKey.getModulus(), rsaRawPublicKey.getExponent());
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, "invalid rsa encryption.");
        }

        return SepidarRequestHeader.of(API_VERSION, getIntegrationId(), arbitraryCode.toString(), arbitraryCodeEncrypted, "Bearer " + token);
    }

    private SepidarApiProxy getSepidarApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
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
