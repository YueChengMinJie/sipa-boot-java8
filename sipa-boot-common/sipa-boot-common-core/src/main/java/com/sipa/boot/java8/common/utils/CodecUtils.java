package com.sipa.boot.java8.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhouxiajie
 * @date 2019-04-02
 */
public class CodecUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(CodecUtils.class);

    /**
     * AES-128-ECB AES/ECB/PKCS5Padding
     */

    /**
     * AES/GCM/PKCS5Padding
     */

    /**
     * AES/CBC/PKCS5Padding
     */
    public static class AdvancedEncryptionStandard {
        public static String encrypt(byte[] data, String key, String initVector) {
            try {
                IvParameterSpec iv = null;
                if (StringUtils.isNotBlank(initVector)) {
                    iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
                }
                SecretKeySpec sks = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                if (iv != null) {
                    cipher.init(Cipher.ENCRYPT_MODE, sks, iv);
                } else {
                    cipher.init(Cipher.ENCRYPT_MODE, sks);
                }

                byte[] encrypted = cipher.doFinal(data);
                return Base64.encodeBase64String(encrypted);
            } catch (Exception e) {
                LOGGER.error("AES.encrypt error", e);
            }
            return null;
        }

        public static String decrypt(byte[] data, String key, String initVector) {
            try {
                IvParameterSpec iv = null;
                if (StringUtils.isNotBlank(initVector)) {
                    iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
                }
                SecretKeySpec sks = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                if (iv != null) {
                    cipher.init(Cipher.DECRYPT_MODE, sks, iv);
                } else {
                    cipher.init(Cipher.DECRYPT_MODE, sks);
                }
                byte[] original = cipher.doFinal(Base64.decodeBase64(data));

                return new String(original);
            } catch (Exception e) {
                LOGGER.error("AES.decrypt error", e);
            }
            return null;
        }
    }

    /**
     * DESede/CBC/PKCS5Padding
     */
    public static class TripleDes {
        private static final int PATCH_START = 16;

        private static final int PATCH = 6;

        private static final int IV_LENGTH = 8;

        private static final String KEY = "SIPA-BOOT-IOV";

        private static final String ORIGIN_ENCODER = "md5";

        private static final String ALGORITHM = "DESede";

        private static final String CIPHER = "DESede/CBC/PKCS5Padding";

        public static String encryptReturnHex(byte[] data) {
            byte[] encrypt = encrypt(data);
            return encrypt == null ? null : Hex.encodeHexString(encrypt);
        }

        public static String decrypt(String data) {
            try {
                byte[] decrypt = decrypt(Hex.decodeHex(data));
                return new String(Objects.requireNonNull(decrypt));
            } catch (Exception e) {
                LOGGER.error("TripleDes.decryptReturnHex error", e);
            }
            return null;
        }

        public static byte[] encrypt(byte[] data) {
            try {
                final byte[] keyBytes = getKeyBytes();

                final Cipher cipher = getCipher(keyBytes, Cipher.ENCRYPT_MODE);

                return cipher.doFinal(data);
            } catch (Exception e) {
                LOGGER.error("TripleDes.encrypt error", e);
            }
            return null;
        }

        public static byte[] decrypt(byte[] data) {
            try {
                final byte[] keyBytes = getKeyBytes();

                final Cipher decipher = getCipher(keyBytes, Cipher.DECRYPT_MODE);

                return decipher.doFinal(data);
            } catch (Exception e) {
                LOGGER.error("TripleDes.decrypt error", e);
            }
            return null;
        }

        private static Cipher getCipher(byte[] keyBytes, int decryptMode) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
            final SecretKey key = new SecretKeySpec(keyBytes, ALGORITHM);

            final IvParameterSpec iv = new IvParameterSpec(new byte[IV_LENGTH]);

            final Cipher cipher = Cipher.getInstance(CIPHER);

            cipher.init(decryptMode, key, iv);

            return cipher;
        }

        private static byte[] getKeyBytes() throws NoSuchAlgorithmException {
            final MessageDigest md = MessageDigest.getInstance(ORIGIN_ENCODER);

            final byte[] digestOfPassword = md.digest(KEY.getBytes(StandardCharsets.UTF_8));

            final byte[] keyBytes = Arrays.copyOf(digestOfPassword, PATCH_START + IV_LENGTH);

            for (int j = 0, k = PATCH_START; j < PATCH;) {
                keyBytes[k++] = keyBytes[j++];
            }

            return keyBytes;
        }
    }
}
