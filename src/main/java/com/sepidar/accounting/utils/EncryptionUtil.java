package com.sepidar.accounting.utils;

import com.google.common.base.Strings;
import com.sepidar.accounting.exceptions.SepidarGlobalException;
import com.sepidar.accounting.models.responses.helpers.RSAKeyValue;
import lombok.extern.slf4j.Slf4j;
import sun.security.rsa.RSAPublicKeyImpl;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
public class EncryptionUtil {

    public static String aesEncrypt(byte[] key, byte[] iv, byte[] rawText) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encrypted = cipher.doFinal(rawText);

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, e.getMessage());
        }
    }

    public static String aesDecrypt(byte[] iv, byte[] key, byte[] encrypted) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return new String(cipher.doFinal(encrypted));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, e.getMessage());
        }
    }

    public static byte[] aesGenerateRandomIv() {

        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    public static String md5Encrypt(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage(), e);
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, e.getMessage());
        }
    }

    public static String rsaEncryption(byte[] rsaModulus, byte[] rsaExponent, byte[] raw) {
        try {
            PublicKey rsaPublicKeySpec = new RSAPublicKeyImpl(new BigInteger(1, rsaModulus), new BigInteger(1, rsaExponent));
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKeySpec);
            return new String(Base64.getEncoder().encode(cipher.doFinal(raw)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, e.getMessage());
        }
    }

    public static RSAKeyValue getRSAFromXmlString(String xmlString) {

        if (Strings.isNullOrEmpty(xmlString)) {
            return null;
        }

        try {
            StringReader sr = new StringReader(xmlString);
            JAXBContext jaxbContext = JAXBContext.newInstance(RSAKeyValue.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (RSAKeyValue) unmarshaller.unmarshal(sr);
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage(), e);
            throw new SepidarGlobalException(HttpURLConnection.HTTP_INTERNAL_ERROR, 0, e.getMessage());
        }
    }
}
