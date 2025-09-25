import java.util.Scanner;

public class WalletCLI {
    public static void main(String[] args) throws Exception {
        WalletManager manager = new WalletManager();
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n1. Generate ETH Wallet\n2. Import ETH Wallet\n3. Generate BTC Wallet\n4. Import BTC Wallet\n5. List Wallets\n0. Exit");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline
            switch (choice) {
                case 1:
                    System.out.println("New ETH Address: " + manager.generateEthereumWallet());
                    break;
                case 2:
                    System.out.print("Enter ETH private key: ");
                    String ethKey = sc.nextLine();
                    manager.importEthereumWallet(ethKey);
                    break;
                case 3:
                    System.out.println("New BTC Address: " + manager.generateBitcoinWallet());
                    break;
                case 4:
                    System.out.print("Enter BTC WIF private key: ");
                    String btcKey = sc.nextLine();
                    manager.importBitcoinWallet(btcKey);
                    break;
                case 5:
                    manager.listWallets();
                    break;
                case 0:
                    System.exit(0);
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}
