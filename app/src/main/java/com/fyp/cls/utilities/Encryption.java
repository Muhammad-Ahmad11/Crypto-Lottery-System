package com.fyp.cls.utilities;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Encryption extends Application
{
    private static String SECRET_KEY = "758287495542";
    private static final String KEY_GENERATION_FLAG = "key_generation_flag";
    private static String SALTVALUE = "koenigsegg";
    private static final int DEFAULT_AES_GCM_MASTER_KEY_SIZE = 256; // Use a suitable key size
    private static final String SALT_ALIAS = "mySaltAlias"; // Use your desired alias for the salt
    private static final String mKeyAlias = "myEncryptionKeyAlias"; // Use your desired alias
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_GENERATION_VERSION = "key_generation_version";
    Context context;

    /*public Encryption(Context context) throws UnrecoverableEntryException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Assuming you have a boolean flag to track whether key generation has been performed
        boolean isKeyGenerated = prefs.getBoolean(KEY_GENERATION_FLAG, false);

        if (!isKeyGenerated) {
            //Toast.makeText(context, "Key generated", Toast.LENGTH_SHORT).show();
            // Perform key generation and storage
            generateAndStoreKeyAndSaltInKeyStore();

            // Set the flag to true
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_GENERATION_FLAG, true);
            editor.apply();
        } else {
            //Toast.makeText(context, "Key Retrieved", Toast.LENGTH_SHORT).show();
            getSecretKeyFromKeystore();
        }
    }

    private void generateAndStoreKeyAndSaltInKeyStore() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableEntryException {
        try {
            KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(
                    mKeyAlias,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
            )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(DEFAULT_AES_GCM_MASTER_KEY_SIZE)
                    .setUserAuthenticationRequired(false) // Require user authentication
                    //.setUserAuthenticationValidityDurationSeconds(300) // Default authentication validity
                    .build();

            KeyGenerator keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore"
            );

            keyGenerator.init(keyGenParameterSpec);
            SecretKey secretKey = keyGenerator.generateKey();

            // Store the SecretKey in the Android Keystore
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);
            keyStore.setEntry(mKeyAlias, secretKeyEntry, null);

        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        // Storing the salt value
        byte[] salt = generateSalt(); // Replace with your salt generation logic
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        KeyProtection keyProtection = new KeyProtection.Builder(KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(false)
                .build();

        SecretKeySpec saltKeySpec = new SecretKeySpec(salt, "AES");
        KeyStore.SecretKeyEntry saltEntry = new KeyStore.SecretKeyEntry(saltKeySpec);
        keyStore.setEntry(SALT_ALIAS, saltEntry, keyProtection);

    }

    private byte[] generateSalt() {
        byte[] salt = new byte[16]; // Adjust the size as needed
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        return salt;
    }

    private void getSecretKeyFromKeystore() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(mKeyAlias, null);

            // Retrieving the salt value
            KeyStore.SecretKeyEntry saltEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(SALT_ALIAS, null);
            byte[] retrievedSalt = saltEntry.getSecretKey().getEncoded();
            byte[] retrievedSecretKey = secretKeyEntry.getSecretKey().getEncoded();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                SALTVALUE = Base64.getEncoder().encodeToString(retrievedSalt);
                SECRET_KEY = Base64.getEncoder().encodeToString(retrievedSecretKey);
            } else {
                SALTVALUE = android.util.Base64.encodeToString(retrievedSalt, android.util.Base64.DEFAULT);
                SECRET_KEY = android.util.Base64.encodeToString(retrievedSecretKey, android.util.Base64.DEFAULT);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /* Encryption Method */
    public String encrypt(String strToEncrypt)
    {
        try
        {
            /* Declare a byte array. */
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            /* Create factory for secret keys. */
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            /* PBEKeySpec class implements KeySpec interface. */
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALTVALUE.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);

            /* Returns encrypted value. */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getEncoder()
                        .encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
            } else {

                // Handle the encryption for versions below Android Oreo (API level < 26)
                try {
                    byte[] encryptedBytes = cipher.doFinal(strToEncrypt.getBytes("UTF-8"));
                    return android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null; // Or handle the exception in an appropriate way
                }
            }
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e)  {
            System.out.println("Error occured during encryption: " + e.toString());
        }
        return null;
    }

    /* Decryption Method */
    public String decrypt(String strToDecrypt)
    {
        try
        {
            /* Declare a byte array. */
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            /* Create factory for secret keys. */
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            /* PBEKeySpec class implements KeySpec interface. */
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALTVALUE.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            /* Retruns decrypted value. */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
            }  else {
                // Handle decryption for versions older than O (API level < 26)
                try {
                    byte[] decodedBytes = android.util.Base64.decode(strToDecrypt, android.util.Base64.DEFAULT);
                    byte[] decryptedBytes = cipher.doFinal(decodedBytes);
                    return new String(decryptedBytes);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null; // Or handle the exception accordingly
                }
            }
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            System.out.println("Error occured during decryption: " + e);
        }
        return null;
    }

}