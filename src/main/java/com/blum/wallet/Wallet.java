package com.blum.wallet;

public class Wallet {
    private final String chain;
    private final String address;
    private final String privateKey;

    public Wallet(String chain, String address, String privateKey) {
        this.chain = chain;
        this.address = address;
        this.privateKey = privateKey;
    }

    public String getChain() { return chain; }
    public String getAddress() { return address; }
    public String getPrivateKey() { return privateKey; }

    @Override
    public String toString() { return chain + " | " + address; }
}