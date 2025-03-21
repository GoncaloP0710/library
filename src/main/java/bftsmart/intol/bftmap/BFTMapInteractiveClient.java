/**
 * BFT Map implementation (interactive client).
 *
 */
package bftsmart.intol.bftmap;

import Crypto.Coin;
import Crypto.Nft;
import Exceptions.InvalidPaymentException;
import Exceptions.NotEnoughMoneyException;
import Utils.InterfaceHandler;
import Utils.Utils;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class BFTMapInteractiveClient {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int clientId = (args.length > 0) ? Integer.parseInt(args[0]) : 1001;
        BFTMap<Integer, Coin> coinMap = new BFTMap<>(Utils.generateId(clientId));
        BFTMap<Integer, Nft> nftMap = new BFTMap<>(Utils.generateId(clientId+1)+1);
        Console console = System.console();
        InterfaceHandler.startUp();
        InterfaceHandler.help();

        mainLoop:
        while (true) {
            String cmd = console.readLine("\n  > ");

            if (cmd.equalsIgnoreCase("HELP")) {

                InterfaceHandler.help();
                
            } else if (cmd.equalsIgnoreCase("MINT") && clientId == 4) { // Make sure only the client 4 can mint coins

                float coin_value;
                try {
                    coin_value = Integer.parseInt(console.readLine("Enter the value of the coin: "));
                    if (coin_value <= 0) {
                        InterfaceHandler.erro("\tThe value of the coin must be greater than 0!\n");
                        continue mainLoop;
                    }
                } catch (NumberFormatException e) {
                    InterfaceHandler.erro("\tThe value is supposed to be a float!\n");
                    continue mainLoop;
                }
                Coin new_coin = new Coin(coin_value, clientId);

                //invokes the op on the servers
                coinMap.put(new_coin.getId(), new_coin);

                InterfaceHandler.success("Coin with id " + new_coin.getId() + " and value " + new_coin.getValue() + " minted\n");

            } else if (cmd.equalsIgnoreCase("MY_COINS")) {

                Collection<Coin> coins = coinMap.values(Class.forName("Crypto.Coin"));
                if (coins.isEmpty()) {
                    InterfaceHandler.info("\tNo coins found!\n");
                    continue mainLoop;
                }
                for (Coin coin : coins) {
                    if (coin.getOwner() == clientId) {
                        InterfaceHandler.info(coin.toString());
                    }
                }

            } else if (cmd.equalsIgnoreCase("SPEND")) {

                float transfer_value;
                int reciver_id;
                ArrayList<Integer> coins_id_to_transfer = new ArrayList<Integer>();

                try {
                    transfer_value = Float.parseFloat(console.readLine("Enter the value to transfer: "));
                    if(transfer_value <= 0){
                        InterfaceHandler.erro("\tThe value of the coin must be greater than 0!\n");
                        continue mainLoop;
                    }
                    reciver_id = Integer.parseInt(console.readLine("Enter the id of the receiver: "));
                    String coin_ids = console.readLine("Enter the IDs of the coins to transfer (comma-separated): ");
                    coins_id_to_transfer = Utils.coinIdToArray(coin_ids.split(","));
                } catch (NumberFormatException e) {
                    InterfaceHandler.erro("\tThe format of the value or the ID is wrong!\n");
                    continue mainLoop;
                }

                try {
                    spend_coins(coinMap, clientId, transfer_value, reciver_id, coins_id_to_transfer);
                } catch (NotEnoughMoneyException e) {
                    continue mainLoop;
                } catch (InvalidPaymentException e) {
                    continue mainLoop;
                }

            } else if (cmd.equalsIgnoreCase("MY_NFTS")) {

                Collection<Nft> nfts = nftMap.values(Class.forName("Crypto.Nft"));
                if (nfts.isEmpty()) {
                    InterfaceHandler.info("\tNo NFTs found!\n");
                    continue mainLoop;
                }
                for (Nft nft : nfts) {
                    if (nft.getOwner() == clientId) {
                        InterfaceHandler.info(nft.toString());                    
                    }
                }

            } else if (cmd.equalsIgnoreCase("MINT_NFT")) {

                String nft_name;
                String nft_uri;
                float nft_value;

                try {
                    nft_name = console.readLine("Enter the name of the NFT: ");

                    while (!Utils.uniqueNftName(nftMap.values(Class.forName("Crypto.Nft")), nft_name)) {
                        InterfaceHandler.erro("\tThe name of the NFT is not unique!\n");
                        nft_name = console.readLine("Enter the name of the NFT: ");
                    }

                    nft_uri = console.readLine("Enter the URI of the NFT: ");
                    nft_value = Float.parseFloat(console.readLine("Enter the value of the NFT: "));

                    if (nft_value <= 0) {
                        InterfaceHandler.erro("\tThe value of the NFT must be greater than 0!\n");
                        continue mainLoop;
                    }
                } catch (NumberFormatException e) {
                    InterfaceHandler.erro("\tThe value is supposed to be a float!\n");
                    continue mainLoop;
                }

                Nft new_nft = new Nft(nft_name, nft_uri, nft_value, clientId);
                nftMap.put(new_nft.getId(), new_nft);
                InterfaceHandler.success("NFT with id " + new_nft.getId() + ", value " + new_nft.getValue() + " and owner " + new_nft.getOwner() + " minted\n");

            } else if (cmd.equalsIgnoreCase("SET_NFT_PRICE")) {

                String nft_name;
                float new_price;

                try {
                    nft_name = console.readLine("Enter the name of the NFT: ");
                    new_price = Float.parseFloat(console.readLine("Enter the new price of the NFT: "));
                    if(new_price <= 0){
                        InterfaceHandler.erro("\tThe value of the coin must be greater than 0!\n");
                        continue mainLoop;  
                    }
                } catch (NumberFormatException e) {
                    InterfaceHandler.erro("\tThe values were of an incorrect type!\n");
                    continue mainLoop;
                }
                Collection<Nft> nfts = nftMap.values(Class.forName("Crypto.Nft"));
                for (Nft nft : nfts) {
                    if (nft.getOwner() == clientId && nft.getName().equals(nft_name)) {
                        nft.setValue(new_price);
                        nftMap.put(nft.getId(), nft);
                        InterfaceHandler.success("NFT with id " + nft.getId() + " and value " + nft.getValue() + " updated\n");
                        continue mainLoop;
                    }
                }

                InterfaceHandler.erro("\tThe NFT with the name " + nft_name + " was not found!\n");

            } else if (cmd.equalsIgnoreCase("SEARCH_NFT")) {

                String nft_name;
                try {
                    nft_name = console.readLine("Enter the string of the NFT to search: ");
                } catch (NumberFormatException e) {
                    InterfaceHandler.erro("\tThe value is supposed to be a string!\n");
                    continue mainLoop;
                }
                
                Collection<Nft> nfts = Utils.containsStr(nftMap.values(Class.forName("Crypto.Nft")), nft_name);
                for (Nft nft : nfts) {
                    InterfaceHandler.info(nft.toString());
                }

            } else if (cmd.equalsIgnoreCase("BUY_NFT")) {

                int nft_id;
                ArrayList<Integer> coins_id_to_transfer = new ArrayList<Integer>();

                try {
                    nft_id = Integer.parseInt(console.readLine("Enter the ID of the NFT to buy: "));
                    String coin_ids = console.readLine("Enter the IDs of the coins to transfer (comma-separated): ");
                    coins_id_to_transfer = Utils.coinIdToArray(coin_ids.split(","));
                } catch (NumberFormatException e) {
                    InterfaceHandler.erro("\tThe format of the value or receiver ID is wrong!\n");
                    continue mainLoop;
                }

                Nft nft = nftMap.get(nft_id);
                if (nft == null) {
                    InterfaceHandler.erro("\tThe NFT with the ID " + nft_id + " was not found!\n");
                    continue mainLoop;
                }

                try {
                    spend_coins(coinMap, clientId, nft.getValue(), nft.getOwner(), coins_id_to_transfer);
                } catch (NotEnoughMoneyException e) {
                    continue mainLoop;
                } catch (InvalidPaymentException e) {
                    continue mainLoop;
                }

                nft.setOwner(clientId);
                nftMap.put(nft.getId(), nft);
                InterfaceHandler.success("NFT with id " + nft.getId() + " and value " + nft.getValue() + " bought\n");

            } else {
                InterfaceHandler.erro("\tInvalid command :P\n");
            }
        }
    }

    /**
     * Transfer coins from one client to another.
     * 
     * @param coinMap BFTMap of coins
     * @param clientId ID of the sender
     * @param transfer_value Value to transfer
     * @param reciver_id ID of the receiver
     * @param coins_id_to_transfer IDs of the coins to transfer
     * @throws NotEnoughMoneyException  If the sum of the coins is lower than the transfer value
    * @throws InvalidPaymentException 
    */
    private static void spend_coins(BFTMap<Integer, Coin> coinMap, int clientId, float transfer_value, int reciver_id, ArrayList<Integer> coins_id_to_transfer) throws NotEnoughMoneyException, InvalidPaymentException {
        ArrayList<Object> values = coinMap.getValues(coins_id_to_transfer);
        ArrayList<Coin> coins_to_transfer = new ArrayList<>();
            for (Object value : values) {
                if (value instanceof Coin) {
                    coins_to_transfer.add((Coin) value);
                } else {
                    InterfaceHandler.erro("\tInvalid coin ID: " + value + " is not a Coin\n");
                    throw new InvalidPaymentException("Invalid coin ID: " + value + " is not a Coin");
                }
            }
    
            float coins_change = Utils.coins_change(coins_to_transfer, transfer_value);
            if (coins_change < 0) {
                InterfaceHandler.erro("\tThe sum of the coins is lower than the transfer value!\n");
                throw new NotEnoughMoneyException("The sum of the coins is lower than the transfer value!");
            }
    
            // Remove the coins from the sender
            for (Coin coin : coins_to_transfer) {
                coinMap.remove(coin.getId());
            }
    
            // Create new coin for the receiver
            Coin new_coin = new Coin(transfer_value, reciver_id);
            coinMap.put(new_coin.getId(), new_coin);
    
            // Create new coin for the sender with the change
            if (coins_change > 0) {
                Coin change_coin = new Coin(coins_change, clientId);
                coinMap.put(change_coin.getId(), change_coin);
            }
    }
}
