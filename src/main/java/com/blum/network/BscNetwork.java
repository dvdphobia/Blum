package com.blum.network;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

public final class BscNetwork {
    private BscNetwork() {}

    public static final String ENV = "BSC_RPC_URL";

    public static String defaultPublicRpc() {
        return "https://bsc-dataseed.binance.org";
    }

    public static String resolveRpcUrl() {
        String fromEnv = System.getenv(ENV);
        if (fromEnv != null && !fromEnv.isBlank()) return fromEnv.trim();
        return defaultPublicRpc();
    }

    public static Web3j web3j(String rpcUrl) {
        return Web3j.build(new HttpService(rpcUrl));
    }
}