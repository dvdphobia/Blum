# Blum Wallet (Beginner Friendly)

A very simple Java app to create/import wallets for Ethereum (ETH), Binance Smart Chain (BSC), and Bitcoin (BTC), list them, and check balances.

## 1) Build

```bash
mvn -q -DskipTests clean install
```

## 2) Run

```bash
mvn -q -f cli/pom.xml exec:java
```

You will see a menu. Type the number and press Enter.

## 3) What you can do
- Generate ETH wallet
- Import ETH wallet (hex private key)
- Generate BTC wallet
- Import BTC wallet (WIF private key)
- List wallets (stored in `wallets.json`)
- Get balance (ETH, BSC or BTC)

## 4) Get your balance
1. Choose option `6. Get Balance`
2. Enter chain: `ETH`, `BSC` or `BTC`
3. Paste your address
4. If ETH/BSC: you need an RPC URL (how the app talks to the network)
   - Easiest: set a free public URL in your terminal:
     ```bash
     export ETH_RPC_URL="https://cloudflare-eth.com"
     export BSC_RPC_URL="https://bsc-dataseed.binance.org"
     ```
   - Or paste any RPC URL when the app asks (Infura/Alchemy/Ankr/etc.)

The app prints your balance in `ETH`, `BNB` (BSC), or `BTC`.

## Notes
- BTC balances use BlockCypher public API. Heavy use may need an API key or your own node.
- ETH/BSC balances use the RPC URL you provide via `web3j`.
