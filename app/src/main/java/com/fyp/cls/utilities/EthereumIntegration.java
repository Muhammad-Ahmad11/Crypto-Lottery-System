package com.fyp.cls.utilities;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;
import org.web3j.protocol.core.methods.request.Transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Security;
import java.util.Collections;

public class EthereumIntegration {
    private Context context;
    private Web3j web3;
    private Credentials credentials;
    private final String contractAddress = "0x5B38Da6a701c568545dCfcB03FcB875f56beddC4";  // Replace with your actual contract address

    public EthereumIntegration(Context context, String privateKey) {
        this.context = context;
        this.credentials = Credentials.create(privateKey); // Initialize credentials with the provided private key
        setupBouncyCastle();
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    private void setupBouncyCastle() {
        Security.addProvider(new BouncyCastleProvider());
    }

    public void strictMode() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void connectToEthNetwork() {
        String infuraApiKey = "10bc630355184b6f90736e7d89a3d0a0";
        web3 = Web3j.build(new HttpService("https://mainnet.infura.io/v3/" + infuraApiKey));

        try {
            String clientVersion = web3.web3ClientVersion().send().getWeb3ClientVersion();
            Log.d("TAG", "Connected to Ethereum client version: " + clientVersion);
            showToast("Connected to Ethereum client version: " + clientVersion);
        } catch (IOException e) {
            Log.d("TAG", "Connection failed: " + e.getMessage());
            showToast("Connection failed: " + e.getMessage());
        }
    }

    public void setUserDataOnBlockchain(String data) {
        try {
            Function setUserDataFunction = new Function(
                    "setUserData",
                    Collections.singletonList(new Utf8String(data)),
                    Collections.emptyList()
            );

            String encodedFunction = FunctionEncoder.encode(setUserDataFunction);

            org.web3j.protocol.core.methods.response.EthSendTransaction transactionResponse = new org.web3j.tx.RawTransactionManager(web3, credentials)
                    .sendTransaction(
                            BigInteger.valueOf(30000), // Gas price
                            BigInteger.valueOf(30000), // Gas limit
                            contractAddress,
                            encodedFunction,
                            BigInteger.ZERO // Nonce - you can adjust this based on your requirements
                    );

            if (transactionResponse.hasError()) {
                showToast("Error sending transaction: " + transactionResponse.getError().getMessage());
            } else {
                String transactionHash = transactionResponse.getTransactionHash();
                showToast("Set UserData Transaction Hash: " + transactionHash);
            }
        } catch (Exception e) {
            showToast("Setting user data on blockchain failed: " + e.getMessage());
        }
    }


    public String getUserDataFromBlockchain() {
        try {
            Function getUserDataFunction = new Function(
                    "getUserData",
                    Collections.emptyList(),
                    Collections.singletonList(new TypeReference<Utf8String>() {})
            );

            String encodedFunction = FunctionEncoder.encode(getUserDataFunction);

            EthCall ethCall = web3.ethCall(
                    Transaction.createEthCallTransaction(
                            credentials.getAddress(),
                            contractAddress,
                            encodedFunction
                    ),
                    DefaultBlockParameterName.LATEST
            ).send();

            String result = ethCall.getResult();
            showToast("Get UserData Result: " + result);

            return result;
        } catch (Exception e) {
            showToast("Getting user data from blockchain failed: " + e.getMessage());
            Log.d("TAG", "Getting user data from blockchain failed: " + e.getMessage());
            return null;
        }
    }
}
