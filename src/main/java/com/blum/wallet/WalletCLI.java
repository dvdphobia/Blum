package com.blum.wallet;

import java.util.Scanner;
import com.blum.network.BscNetwork;

public class WalletCLI {
    private final WalletManager manager;
    private final Scanner scanner;

    public WalletCLI() {
        this.manager = new WalletManager();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) throws Exception {
        new WalletCLI().run();
    }

    private void run() throws Exception {
        while (true) {
            printMenu();
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1: handleGenerateEth(); break;
                case 2: handleImportEth(); break;
                case 3: handleGenerateBtc(); break;
                case 4: handleImportBtc(); break;
                case 5: handleList(); break;
                case 6: handleBalance(); break;
                case 0: System.exit(0);
                default: println("Invalid choice");
            }
        }
    }

    private void printMenu() {
        println("");
        println("1. Generate ETH Wallet");
        println("2. Import ETH Wallet");
        println("3. Generate BTC Wallet");
        println("4. Import BTC Wallet");
        println("5. List Wallets");
        println("6. Get Balance");
        println("0. Exit");
    }

    private void handleGenerateEth() throws Exception {
        String addr = manager.generateWalletHandler("ETH");
        println("New ETH Address: " + addr);
    }

    private void handleImportEth() throws Exception {
        String key = readLine("Enter ETH private key (hex): ");
        manager.importEthereumWallet(key);
        println("ETH wallet imported.");
    }

    private void handleGenerateBtc() {
        try {
            String addr = manager.generateWalletHandler("BTC");
            println("New BTC Address: " + addr);
        } catch (Exception e) {
            println("Error generating BTC wallet: " + e.getMessage());
        }
    }

    private void handleImportBtc() {
        String wif = readLine("Enter BTC WIF private key: ");
        manager.importBitcoinWallet(wif);
        println("BTC wallet imported.");
    }

    private void handleList() {
        println("Your wallets:");
        manager.listWallets();
    }

    private void handleBalance() {
        String chain = readLine("Chain (ETH/BTC/BSC): ").toUpperCase();
        String addr = readLine("Address: ");
        try {
            if ("ETH".equals(chain)) {
                String rpc = System.getenv("ETH_RPC_URL");
                if (rpc == null || rpc.isBlank()) {
                    rpc = readLine("ETH RPC URL (e.g., https://mainnet.infura.io/v3/KEY): ");
                }
                String bal = manager.getEthereumBalance(addr, rpc);
                println("Balance: " + bal + " ETH");
            } else if ("BTC".equals(chain)) {
                String bal = manager.getBitcoinBalanceBTC(addr);
                println("Balance: " + bal + " BTC");
            } else if ("BSC".equals(chain)) {
                String rpc = System.getenv(BscNetwork.ENV);
                if (rpc == null || rpc.isBlank()) {
                    println("Using default public BSC RPC");
                    rpc = BscNetwork.resolveRpcUrl();
                }
                String bal = manager.getBscBalance(addr, rpc);
                println("Balance: " + bal + " BNB");
            } else {
                println("Unsupported chain");
            }
        } catch (Exception e) {
            println("Error: " + e.getMessage());
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            println("Please enter a number.");
            scanner.nextLine();
            System.out.print(prompt);
        }
        int val = scanner.nextInt();
        scanner.nextLine();
        return val;
    }

    private void println(String s) {
        System.out.println(s);
    }
}