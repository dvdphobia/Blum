package com.blum.wallet;

import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.bitcoinj.core.*;
import org.bitcoinj.params.MainNetParams;

import java.io.*;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// WalletManager: creates, imports, lists wallets and fetches balances.
public class WalletManager {

    private static final String STORAGE_FILE = "wallets.json";
    private Map<String, Map<String, String>> wallets;

    public WalletManager() {
        wallets = loadWallets();
    }

        public String generateWalletHandler(String chain) throws Exception {
        if (chain.equalsIgnoreCase("ETH")) {

            // Ethereum wallet creation handler 
            ECKeyPair keyPair = Keys.createEcKeyPair();
            String privateKey = keyPair.getPrivateKey().toString(16);
            String address = "0x" + Keys.getAddress(keyPair);
            saveWallet("ETH", address, privateKey);
            return address;

        } else if (chain.equalsIgnoreCase("BTC")) {

            // Bitcoin wallet creation handler 
            NetworkParameters params = MainNetParams.get();
            ECKey key = new ECKey(new SecureRandom());
            String address = LegacyAddress.fromKey(params, key).toString();
            String privateKey = key.getPrivateKeyAsWiF(params);
            saveWallet("BTC", address, privateKey);
            return address;
        
        } else {
            throw new IllegalArgumentException("Unsupported chain: " + chain);
        }
    }





    public void importEthereumWallet(String privateKeyHex) throws Exception {
        BigInteger privKey = new BigInteger(privateKeyHex, 16);
        ECKeyPair keyPair = ECKeyPair.create(privKey);
        String address = "0x" + Keys.getAddress(keyPair);
        saveWallet("ETH", address, privateKeyHex);
    }

    public void importBitcoinWallet(String wif) {
        NetworkParameters params = MainNetParams.get();
        ECKey key = DumpedPrivateKey.fromBase58(params, wif).getKey();
        String address = LegacyAddress.fromKey(params, key).toString();
        saveWallet("BTC", address, wif);
    }

    // --- Balances ---
    public String getEthereumBalance(String address, String rpcUrl) throws Exception {
        if (rpcUrl == null || rpcUrl.isBlank()) {
            throw new IllegalArgumentException("ETH RPC URL is required (e.g., set ETH_RPC_URL env var)");
        }
        if (!isValidEthAddress(address)) {
            throw new IllegalArgumentException("Invalid ETH address: " + address);
        }
        Web3j web3j = Web3j.build(new HttpService(rpcUrl));
        try {
            BigInteger wei = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST)
                    .send()
                    .getBalance();
            return Convert.fromWei(wei.toString(), Convert.Unit.ETHER).toPlainString();
        } finally {
            web3j.shutdown();
        }
    }

    public String getBitcoinBalanceBTC(String address) throws Exception {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("BTC address is required");
        }
        String url = "https://api.blockcypher.com/v1/btc/main/addrs/" + address + "/balance";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch BTC balance (" + response.statusCode() + ")\n" + response.body());
        }
        com.fasterxml.jackson.databind.JsonNode node = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(response.body());
        long sats = node.path("final_balance").asLong();
        double btc = sats / 100_000_000.0;
        return String.format(java.util.Locale.ROOT, "%.8f", btc);
    }

    // --- Binance Smart Chain (BSC) ---
    public String getBscBalance(String address, String rpcUrl) throws Exception {
        if (rpcUrl == null || rpcUrl.isBlank()) {
            throw new IllegalArgumentException("BSC RPC URL is required (set BSC_RPC_URL or pass one)");
        }
        if (!isValidEthAddress(address)) {
            throw new IllegalArgumentException("Invalid BSC (EVM) address: " + address);
        }
        Web3j web3j = Web3j.build(new HttpService(rpcUrl));
        try {
            BigInteger wei = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST)
                    .send()
                    .getBalance();
            return Convert.fromWei(wei.toString(), Convert.Unit.ETHER).toPlainString();
        } finally {
            web3j.shutdown();
        }
    }

    private boolean isValidEthAddress(String address) {
        if (address == null) return false;
        String a = address.trim();
        if (!a.startsWith("0x") || a.length() != 42) return false;
        for (int i = 2; i < a.length(); i++) {
            char c = a.charAt(i);
            boolean isHex = (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
            if (!isHex) return false;
        }
        return true;
    }

    // --- Storage ---
    private void saveWallet(String chain, String address, String privateKey) {
        wallets.putIfAbsent(chain, new HashMap<>());
        wallets.get(chain).put(address, privateKey);
        saveToFile();
    }

    private Map<String, Map<String, String>> loadWallets() {
        File f = new File(STORAGE_FILE);
        if (!f.exists()) return new HashMap<>();
        try (Reader reader = new FileReader(f)) {
            return new com.fasterxml.jackson.databind.ObjectMapper().readValue(f, HashMap.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private void saveToFile() {
        try (Writer writer = new FileWriter(STORAGE_FILE)) {
            new com.fasterxml.jackson.databind.ObjectMapper().writeValue(writer, wallets);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listWallets() {
        getAllWallets().forEach(w -> System.out.println(w.toString()));
    }

    public List<Wallet> getAllWallets() {
        List<Wallet> out = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> entry : wallets.entrySet()) {
            String chain = entry.getKey();
            for (Map.Entry<String, String> e : entry.getValue().entrySet()) {
                out.add(new Wallet(chain, e.getKey(), e.getValue()));
            }
        }
        return out;
    }

    public List<Wallet> getWalletsByChain(String chain) {
        List<Wallet> out = new ArrayList<>();
        Map<String, String> map = wallets.getOrDefault(chain, Collections.emptyMap());
        for (Map.Entry<String, String> e : map.entrySet()) {
            out.add(new Wallet(chain, e.getKey(), e.getValue()));
        }
        return out;
    }
}