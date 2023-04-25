package com.sepidar.accounting.services.impl;

import com.google.common.base.Strings;
import com.sepidar.accounting.exceptions.SepidarGlobalException;
import com.sepidar.accounting.models.SepidarConfiguration;
import com.sepidar.accounting.models.requests.DeviceRegisterRequest;
import com.sepidar.accounting.models.requests.LoginRequest;
import com.sepidar.accounting.models.requests.NewInvoiceRequest;
import com.sepidar.accounting.models.responses.DeviceRegisterResponse;
import com.sepidar.accounting.models.responses.InvoiceResponse;
import com.sepidar.accounting.models.responses.LoginResponse;
import com.sepidar.accounting.models.responses.helpers.RSAKeyValue;
import com.sepidar.accounting.services.SepidarApiProxy;
import com.sepidar.accounting.services.SepidarService;
import lombok.extern.slf4j.Slf4j;
import com.sepidar.accounting.utils.EncryptionUtil;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Calendar;
import java.util.UUID;

@Slf4j
public class SepidarServiceImpl implements SepidarService {

    private final String API_VERSION;
    private final String API_URL;
    private final String DEVICE_SERIAL_ID;
    private final String USERNAME;
    private final String PASSWORD;

    private byte[] rsaModulus = null;
    private byte[] rsaExponent = null;
    private String token = null;

    public SepidarServiceImpl(SepidarConfiguration configuration) {
        this.API_VERSION = configuration.getApiVersion();
        this.API_URL = configuration.getUrl();
        this.DEVICE_SERIAL_ID = configuration.getDeviceId();
        this.USERNAME = configuration.getUsername();
        this.PASSWORD = configuration.getPassword();
    }

    /**
     * This method assumes that configuration details are set before.
     */
    @Override
    public void register() {

        String requestId = getRandomUniqueId();

        byte[] iv = EncryptionUtil.aesGenerateRandomIv();
        String cypher = EncryptionUtil.aesEncrypt(iv, getEncryptionKey().getBytes(), getIntegrationId().getBytes());

        if (Strings.isNullOrEmpty(cypher)) {
            LOGGER.error("register(req={}). cypher is null or empty for iv <{}>, encryptionKey <{}>, integrationKey <{}>.", requestId, iv, getEncryptionKey(), getIntegrationId());
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, "cypher is null or empty.");
        }

        Call<DeviceRegisterResponse> deviceRegisterResponseCall = getSepidarApi().deviceRegister(
                new DeviceRegisterRequest(
                        cypher,
                        Base64.getEncoder().encodeToString(iv),
                        getIntegrationId()
                )
        );

        LOGGER.debug("register(req={}). api call started with base64 encoded of iv <{}>, base64 encoded of cypher <{}>, integrationId <{}>.", requestId, Base64.getEncoder().encodeToString(iv), cypher, getIntegrationId());

        try {
            Response<DeviceRegisterResponse> response = deviceRegisterResponseCall.execute();

            if (response.isSuccessful()) {
                if (response.body() == null) {
                    LOGGER.error("register(req={}). register api responded with null body", requestId);
                    throw new RuntimeException("api response is null");
                }

                LOGGER.debug("register(req={}). register api responded with iv <{}>, cypher <{}>, device title <{}>.", requestId, response.body().getIv(), response.body().getCypher(), response.body().getDeviceTitle());

                byte[] ivDecodedByteArray = Base64.getDecoder().decode(response.body().getIv());
                byte[] cypherDecodedByteArray = Base64.getDecoder().decode(response.body().getCypher());

                String xmlString = EncryptionUtil.aesDecrypt(ivDecodedByteArray, getEncryptionKey().getBytes(), cypherDecodedByteArray);

                if (Strings.isNullOrEmpty(xmlString)) {
                    LOGGER.error("register(req={}). xmlString is null or empty with iv <{}>, cypher <{}>.", requestId, response.body().getIv(), response.body().getCypher());
                    throw new RuntimeException("xml containing rsa public key is null or empty.");
                }

                RSAKeyValue rsaResponse = EncryptionUtil.getRSAFromXmlString(xmlString);

                if (rsaResponse == null || Strings.isNullOrEmpty(rsaResponse.getExponent()) || Strings.isNullOrEmpty(rsaResponse.getModulus())) {
                    LOGGER.error("register(req={}). RSA response or rsaKeyValue object is null or exponent or modulus are null or empty for xmlString <{}>.", requestId, xmlString);
                    throw new RuntimeException("rsa response is null or empty.");
                }

                // setting RSA values to use in other APIs
                this.rsaExponent = Base64.getDecoder().decode(rsaResponse.getExponent().getBytes(StandardCharsets.UTF_8));
                this.rsaModulus = Base64.getDecoder().decode(rsaResponse.getModulus().getBytes(StandardCharsets.UTF_8));
            } else {
                LOGGER.error("register(req={}). register api responded with error code <{}> and message <{}>", requestId, response.code(), (response.errorBody() == null) ? null : response.errorBody().string());
                throw new RuntimeException("api responded with error code " + response.code());
            }
        } catch (Exception e) {
            LOGGER.error("req=(" + requestId + ") " + e.getMessage(), e);
            throw new RuntimeException("register(req=" + requestId + "). unknown exception with message: " + e.getMessage());
        }
    }

    @Override
    public void login() {

        String requestId = getRandomUniqueId();

        if ((rsaModulus == null) || (rsaModulus.length == 0) || (rsaExponent == null) || (rsaExponent.length == 0)) {
            LOGGER.error("login(req={}). rsaModulus or rsaExponent are null or empty. running register()...", requestId);
            register();

            if ((rsaModulus == null) || (rsaModulus.length == 0) || (rsaExponent == null) || (rsaExponent.length == 0)) {
                LOGGER.error("login(req={}). rsaModulus or rsaExponent are still null or empty after running register(). throwing exception...", requestId);
                throw new RuntimeException("encryption keys are null or empty.");
            }
        }

        String arbitraryCode = UUID.randomUUID().toString();
        String arbitraryCodeEncrypted = EncryptionUtil.rsaEncryption(rsaModulus, rsaExponent, arbitraryCode.getBytes());

        if (Strings.isNullOrEmpty(arbitraryCodeEncrypted)) {
            LOGGER.error("login(req={}). arbitraryCodeEncrypted is null or empty for arbitraryCode <{}>, rsaModulus <{}> and rsaExponent <{}>", requestId, arbitraryCode, rsaModulus, rsaExponent);
            throw new RuntimeException("invalid rsa encryption.");
        }

        LOGGER.info("login(req={}). api call started with arbitraryCode <{}>, arbitraryCodeEncrypted <{}>, integrationId <{}>, username <{}>, password <{}>", requestId, arbitraryCode, arbitraryCodeEncrypted, getIntegrationId(), USERNAME, PASSWORD);

        Call<LoginResponse> loginResponseCall = getSepidarApi().userLogin(
                new LoginRequest(
                        USERNAME,
                        EncryptionUtil.md5Encrypt(PASSWORD)
                ),
                API_VERSION,
                getIntegrationId(),
                arbitraryCode,
                arbitraryCodeEncrypted
        );

        try {
            Response<LoginResponse> response = loginResponseCall.execute();

            if (response.isSuccessful()) {
                if (response.body() == null) {
                    LOGGER.error("login(req={}). login api responded with null body", requestId);
                    throw new RuntimeException("api response is null");
                }

                LOGGER.info("login(req={}). login api responded with userId <{}>, title <{}>, canRegisterInvoice <{}>.", requestId, response.body().getUserId(), response.body().getTitle(), response.body().getCanRegisterInvoice());

                // setting token
                this.token = response.body().getToken();
            } else {
                LOGGER.error("login(req={}). login api responded with error code <{}> and message <{}>", requestId, response.code(), (response.errorBody() == null) ? null : response.errorBody().string());
                throw new RuntimeException("api responded with error code " + response.code());
            }
        } catch (Exception e) {
            LOGGER.error("req=" + requestId + ")" + e.getMessage(), e);
            throw new RuntimeException("login(req=" + requestId + "). unknown exception with message: " + e.getMessage());
        }
    }

    @Override
    public boolean isAuthenticated() {

        String requestId = getRandomUniqueId();
        String arbitraryCode = UUID.randomUUID().toString();
        String arbitraryCodeEncrypted = EncryptionUtil.rsaEncryption(rsaModulus, rsaExponent, arbitraryCode.getBytes());

        LOGGER.info("isAuthenticated(req={}). authenticated api call started with arbitraryCode <{}>, arbitraryCodeEncrypted <{}>, integrationId <{}>, token <{}>.", requestId, arbitraryCode, arbitraryCodeEncrypted, getIntegrationId(), this.token);

        Call<Boolean> authenticatedResponseCall = getSepidarApi().isAuthenticated(
                API_VERSION,
                this.token,
                getIntegrationId(),
                arbitraryCode,
                arbitraryCodeEncrypted
        );

        try {
            Response<Boolean> response = authenticatedResponseCall.execute();
            if (response.isSuccessful()) {
                LOGGER.info("authenticated(req={}). token is valid.", requestId);
                return true;
            } else {
                LOGGER.error("authenticated(req={}). isAuthenticated api responded with error code <{}> and message <{}>", requestId, response.code(), (response.errorBody() == null) ? null : response.errorBody().string());
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("req=" + requestId + ")" + e.getMessage(), e);
            throw new RuntimeException("authenticated(req=" + requestId + "). unknown exception with message: " + e.getMessage());
        }
    }

    @Override
    public void createNewInvoice(NewInvoiceRequest request) {

        try {
            if (!isAuthenticated()) {
                login();
            }
        } catch (RuntimeException runtimeException) {
            login();
        }

        createNewInvoiceInternal(request);
    }

    private void createNewInvoiceInternal(NewInvoiceRequest request) {
        String requestId = UUID.randomUUID().toString();

        if (request == null || Strings.isNullOrEmpty(request.getGUID())) {
            LOGGER.error("createNewInvoice(req={}). either newInvoiceRequest <{}> is null or GUID is null or empty.", requestId, request);
            throw new RuntimeException("request or guid is null or empty.");
        }

        String arbitraryCode = UUID.randomUUID().toString();
        String arbitraryCodeEncrypted = EncryptionUtil.rsaEncryption(rsaModulus, rsaExponent, arbitraryCode.getBytes());

        Call<InvoiceResponse> invoiceResponseCall = getSepidarApi().createNewInvoice(
                request,
                API_VERSION,
                this.token,
                getIntegrationId(),
                arbitraryCode,
                arbitraryCodeEncrypted
        );

        try {
            Response<InvoiceResponse> response = invoiceResponseCall.execute();

            if (response.isSuccessful()) {
                LOGGER.info("createNewInvoice(req={}). invoice with GUID <{}> successfully created.", requestId, request.getGUID());
            } else {
                LOGGER.error("createNewInvoice(req={}). api responded with error code <{}> and message <{}>", requestId, response.code(), (response.errorBody() == null) ? null : response.errorBody().string());
                throw new RuntimeException("api responded with error code " + response.code());
            }
        } catch (Exception e) {
            LOGGER.error("(req=" + requestId + ")" + e.getMessage(), e);
            throw new RuntimeException("createNewInvoice(req=" + requestId + "). unknown exception with message: " + e.getMessage());
        }
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
