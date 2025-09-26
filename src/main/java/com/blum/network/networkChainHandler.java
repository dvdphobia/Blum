package com.blum.network;

import com.blum.network.BscNetwork;

public class networkChainHandler {
    public static void main(String[] args) {
        String rpcUrl = BscNetwork.resolveRpcUrl();
        System.out.println("Resolved BSC RPC URL: " + rpcUrl);
    }
    
}
