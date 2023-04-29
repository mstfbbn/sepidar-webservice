package com.sepidar.accounting.services.impl;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.sepidar.accounting.exceptions.SepidarGlobalException;
import com.sepidar.accounting.models.SepidarConfiguration;
import com.sepidar.accounting.models.internal.DeviceRegisterResponseDTO;
import com.sepidar.accounting.models.internal.RsaRawPublicKey;
import com.sepidar.accounting.models.requests.DeviceRegisterRequest;
import com.sepidar.accounting.models.requests.LoginRequest;
import com.sepidar.accounting.models.requests.NewInvoiceRequest;
import com.sepidar.accounting.models.responses.*;
import com.sepidar.accounting.models.responses.helpers.RsaKeyValue;
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
import java.util.UUID;

/**
 * NOTE: every method assumes that configuration details are set before.
 */
@Slf4j
public class SepidarServiceImpl implements SepidarService {

    private final String API_VERSION;
    private final String API_URL;
    private final String DEVICE_SERIAL_ID;
    private final String USERNAME;
    private final String PASSWORD;

    // setting RSA values to use in other APIs
//                this.rsaExponent = ;
//                this.rsaModulus = ;

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
    public DeviceRegisterResponseDTO register() {

        String requestId = getRandomUniqueId();

        byte[] iv = EncryptionUtil.aesGenerateRandomIv();
        String ivBase64Encoded = Base64.getEncoder().encodeToString(iv);
        String cypher = EncryptionUtil.aesEncrypt(getEncryptionKey().getBytes(), iv, getIntegrationId().getBytes());

        if (Strings.isNullOrEmpty(cypher)) {
            LOGGER.error("register(req={}). cypher is null or empty for ivBase64Encoded <{}>, encryptionKey <{}>, integrationId <{}>.", requestId, ivBase64Encoded, getEncryptionKey(), getIntegrationId());
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, "cypher is null or empty.");
        }

        Call<DeviceRegisterResponse> deviceRegisterResponseCall = getSepidarApi().deviceRegister(
                new DeviceRegisterRequest(
                        cypher,
                        Base64.getEncoder().encodeToString(iv),
                        getIntegrationId()
                )
        );

        LOGGER.debug("register(req={}). api call started with base64 encoded of iv <{}>, base64 encoded of cypher <{}>, integrationId <{}>.", requestId, ivBase64Encoded, cypher, getIntegrationId());

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
            } else {
                return handleErrorResponse(response, requestId);
            }
        } catch (Exception e) {
            LOGGER.error("req=(" + requestId + ") " + e.getMessage(), e);
            throw new RuntimeException("register(req=" + requestId + "). unknown exception with message: " + e.getMessage());
        }
    }

    @Override
    public LoginResponse login(String xmlString) {

        String requestId = getRandomUniqueId();

        RsaRawPublicKey rsaRawPublicKey = getRSAFromXmlString(xmlString, requestId);

        UUID arbitraryCode = UUID.randomUUID();
        String arbitraryCodeEncrypted = EncryptionUtil.rsaEncryptionForUUID(rsaRawPublicKey.getModulus(), rsaRawPublicKey.getExponent(), arbitraryCode);

        if (Strings.isNullOrEmpty(arbitraryCodeEncrypted)) {
            LOGGER.error("login(req={}). arbitraryCodeEncrypted is null or empty for arbitraryCode <{}>, base64 of rsaModulus <{}> and base64 of rsaExponent <{}>", requestId, arbitraryCode, rsaRawPublicKey.getModulus(), rsaRawPublicKey.getExponent());
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, "invalid rsa encryption.");
        }

        LOGGER.debug("login(req={}). api call started with arbitraryCode <{}>, arbitraryCodeEncrypted <{}>, integrationId <{}>, username <{}>, password <{}>", requestId, arbitraryCode, arbitraryCodeEncrypted, getIntegrationId(), USERNAME, PASSWORD);

        Call<LoginResponse> loginResponseCall = getSepidarApi().userLogin(
                new LoginRequest(
                        USERNAME,
                        EncryptionUtil.md5Encrypt(PASSWORD)
                ),
                API_VERSION,
                getIntegrationId(),
                arbitraryCode.toString(),
                arbitraryCodeEncrypted
        );

        try {
            Response<LoginResponse> response = loginResponseCall.execute();

            if (response.isSuccessful()) {
                LOGGER.debug("login(req={}). login api responded with userId <{}>, title <{}>, canRegisterInvoice <{}>.", requestId, response.body().getUserId(), response.body().getTitle(), response.body().getCanRegisterInvoice());
                return response.body();
            }
            return handleErrorResponse(response, requestId);
        } catch (Exception e) {
            LOGGER.error("req=(" + requestId + ")" + e.getMessage(), e);
            throw new RuntimeException("login(req=" + requestId + "). unknown exception with message: " + e.getMessage());
        }
    }

    @Override
    public boolean isAuthenticated(String xmlString, String token) {

        String requestId = getRandomUniqueId();

        RsaRawPublicKey rsaRawPublicKey = getRSAFromXmlString(xmlString, requestId);

        UUID arbitraryCode = UUID.randomUUID();
        String arbitraryCodeEncrypted = EncryptionUtil.rsaEncryptionForUUID(rsaRawPublicKey.getModulus(), rsaRawPublicKey.getExponent(), arbitraryCode);

        LOGGER.info("isAuthenticated(req={}). authenticated api call started with arbitraryCode <{}>, arbitraryCodeEncrypted <{}>, integrationId <{}>, token <{}>.", requestId, arbitraryCode, arbitraryCodeEncrypted, getIntegrationId(), token);

        Call<Boolean> authenticatedResponseCall = getSepidarApi().isAuthenticated(
                API_VERSION,
                token,
                getIntegrationId(),
                arbitraryCode.toString(),
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
    public GenerationVersionResponse generationVersion() {

        String requestId = getRandomUniqueId();

        Call<GenerationVersionResponse> generationVersionCall = getSepidarApi().getGenerationVersion();
        try {
            Response<GenerationVersionResponse> response = generationVersionCall.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
            return handleErrorResponse(response, requestId);

        } catch (IOException ioException) {
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, ioException.getMessage());
        }
    }

    @Override
    public void createNewInvoice(String xmlString, String token, NewInvoiceRequest request) {
        String requestId = UUID.randomUUID().toString();

        if (request == null || Strings.isNullOrEmpty(request.getGUID())) {
            LOGGER.error("createNewInvoice(req={}). either newInvoiceRequest <{}> is null or GUID is null or empty.", requestId, request);
            throw new RuntimeException("request or guid is null or empty.");
        }

        RsaRawPublicKey rsaRawPublicKey = getRSAFromXmlString(xmlString, requestId);

        UUID arbitraryCode = UUID.randomUUID();
        String arbitraryCodeEncrypted = EncryptionUtil.rsaEncryptionForUUID(rsaRawPublicKey.getModulus(), rsaRawPublicKey.getExponent(), arbitraryCode);

        Call<InvoiceResponse> invoiceResponseCall = getSepidarApi().createNewInvoice(
                request,
                API_VERSION,
                token,
                getIntegrationId(),
                arbitraryCode.toString(),
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
            LOGGER.error("(req=" + requestId + "). " + e.getMessage(), e);
            throw new RuntimeException("createNewInvoice(req=" + requestId + "). unknown exception with message: " + e.getMessage());
        }
    }

    public static RsaRawPublicKey getRSAFromXmlString(String xmlString, String requestId) {

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
            throw new SepidarGlobalException(response.code(), 0, "error response body is null.");
        }

        try {
            ErrorResponse errorResponse = new Gson().fromJson(response.errorBody().string(), ErrorResponse.class);
            throw new SepidarGlobalException(response.code(), errorResponse.getType(), errorResponse.getMessage());
        } catch (Exception exception) {
            LOGGER.error("handleErrorResponse(req={}). error happened parsing errorBody with message <{}>", requestId, exception.getMessage());
            throw new SepidarGlobalException(response.code(), 0, exception.getMessage());
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
