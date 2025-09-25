import org.web3j.crypto.*;
import org.bitcoinj.core.*;
import org.bitcoinj.params.MainNetParams;

import java.io.*;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.*;

public class WalletManager {

    private static final String STORAGE_FILE = "wallets.json";
    private Map<String, Map<String, String>> wallets;

    public WalletManager() {
        wallets = loadWallets();
    }

    // --- Ethereum ---
    public String generateEthereumWallet() throws Exception {
        ECKeyPair keyPair = Keys.createEcKeyPair();
        String privateKey = keyPair.getPrivateKey().toString(16);
        String address = "0x" + Keys.getAddress(keyPair);
        saveWallet("ETH", address, privateKey);
        return address;
    }

    public void importEthereumWallet(String privateKeyHex) throws Exception {
        BigInteger privKey = new BigInteger(privateKeyHex, 16);
        ECKeyPair keyPair = ECKeyPair.create(privKey);
        String address = "0x" + Keys.getAddress(keyPair);
        saveWallet("ETH", address, privateKeyHex);
    }

        // --- Bitcoin ---
    public String generateBitcoinWallet() {
        NetworkParameters params = MainNetParams.get();
        ECKey key = new ECKey(new SecureRandom());
        String address = LegacyAddress.fromKey(params, key).toString();
        String privateKey = key.getPrivateKeyAsWiF(params);
        saveWallet("BTC", address, privateKey);
        return address;
    }

    public void importBitcoinWallet(String wif) {
        NetworkParameters params = MainNetParams.get();
        ECKey key = DumpedPrivateKey.fromBase58(params, wif).getKey();
        String address = LegacyAddress.fromKey(params, key).toString();
        saveWallet("BTC", address, wif);
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
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(f, HashMap.class);
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
        wallets.forEach((chain, map) -> {
            System.out.println("=== " + chain + " ===");
            map.forEach((address, key) -> System.out.println("Address: " + address));
        });
    }
}
