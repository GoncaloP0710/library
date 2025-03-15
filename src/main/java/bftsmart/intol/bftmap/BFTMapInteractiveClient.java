/**
 * BFT Map implementation (interactive client).
 *
 */
package bftsmart.intol.bftmap;

import Crypto.Coin;
import Crypto.Nft;
import Utils.Utils;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class BFTMapInteractiveClient {

    public static void main(String[] args) throws IOException {
        int clientId = (args.length > 0) ? Integer.parseInt(args[0]) : 1001;
        BFTMap<Integer, String> bftMap = new BFTMap<>(clientId);
        BFTMap<Integer, Coin> coinMap = new BFTMap<>(clientId);
        BFTMap<Integer, Nft> nftMap = new BFTMap<>(clientId);

        Console console = System.console();

        System.out.println("\nCommands:\n");
        System.out.println("\tPUT: Insert value into the map");
        System.out.println("\tGET: Retrieve value from the map");
        System.out.println("\tSIZE: Retrieve the size of the map");
        System.out.println("\tREMOVE: Removes the value associated with the supplied key");
        System.out.println("\tKEYSET: List all keys available in the table");
        System.out.println("\tEXIT: Terminate this client\n");

        while (true) {
            String cmd = console.readLine("\n  > ");

            if (cmd.equalsIgnoreCase("PUT")) {

                int key;
                try {
                    key = Integer.parseInt(console.readLine("Enter a numeric key: "));
                } catch (NumberFormatException e) {
                    System.out.println("\tThe key is supposed to be an integer!\n");
                    continue;
                }
                String value = console.readLine("Enter an alpha-numeric value: ");

                //invokes the op on the servers
                bftMap.put(key, value);

                System.out.println("\nkey-value pair added to the map\n");
            } else if (cmd.equalsIgnoreCase("GET")) {

                int key;
                try {
                    key = Integer.parseInt(console.readLine("Enter a numeric key: "));
                } catch (NumberFormatException e) {
                    System.out.println("\tThe key is supposed to be an integer!\n");
                    continue;
                }

                //invokes the op on the servers
                String value = bftMap.get(key);

                System.out.println("\nValue associated with " + key + ": " + value + "\n");

            } else if (cmd.equalsIgnoreCase("KEYSET")) {

                System.out.println("\tYou are supposed to implement this command :)\n");

            } else if (cmd.equalsIgnoreCase("REMOVE")) {

                int key;
                try {
                    key = Integer.parseInt(console.readLine("Enter a numeric key: "));
                } catch (NumberFormatException e) {
                    System.out.println("\tThe key is supposed to be an integer!\n");
                    continue;
                }

                //invokes the op on the servers
                String value = bftMap.remove(key);

                System.out.println("\nValue associated with " + key + ": " + value + " removed\n");

            } else if (cmd.equalsIgnoreCase("SIZE")) {

                //invokes the op on the servers
                int size = bftMap.size();

                System.out.println("\nSize of the map is " + size + "\n");

            } else if (cmd.equalsIgnoreCase("EXIT")) {

                System.out.println("\tEXIT: Bye bye!\n");
                System.exit(0);

            } else if (cmd.equalsIgnoreCase("MINT")) { // && clientId == 4) { // Make sure only the client 4 can mint coins

                float coin_value;
                try {
                    coin_value = Integer.parseInt(console.readLine("Enter the value of the coin: "));
                } catch (NumberFormatException e) {
                    System.out.println("\tThe value is supposed to be a float!\n");
                    continue;
                }
                Coin new_coin = new Coin(coin_value, clientId);

                //invokes the op on the servers
                coinMap.put(new_coin.getId(), new_coin);

                System.out.println("\ncoin with id " + new_coin.getId() + " and value " + new_coin.getValue() + " minted\n");

            } else if (cmd.equalsIgnoreCase("MY_COINS")) {

                Collection<Coin> coins = coinMap.values();
                for (Coin coin : coins) {
                    if (coin.getOwner() == clientId) {
                        System.out.println(coin);
                    }
                }

            } else if (cmd.equalsIgnoreCase("SPEND")) {

                float transfer_value;
                int reciver_id;
                ArrayList<Integer> coins_id_to_transfer = new ArrayList<Integer>();

                try {
                    transfer_value = Float.parseFloat(console.readLine("Enter the value to transfer: "));
                    reciver_id = Integer.parseInt(console.readLine("Enter the id of the receiver: "));
                    String coin_ids = console.readLine("Enter the IDs of the coins to transfer (comma-separated): ");
                    coins_id_to_transfer = Utils.coinIdToArray(coin_ids.split(","));
                } catch (NumberFormatException e) {
                    System.out.println("\tThe format of the value or receiver ID is wrong!\n");
                    continue;
                }

                spend_coins(coinMap, clientId, transfer_value, reciver_id, coins_id_to_transfer);

            } else if (cmd.equalsIgnoreCase("MY_NFTS")) {

                Collection<Nft> nfts = nftMap.values();
                for (Nft nft : nfts) {
                    if (nft.getOwner() == clientId) {
                        System.out.println(nft);                    
                    }
                }

            } else if (cmd.equalsIgnoreCase("MINT_NFT")) {

                String nft_name;
                String nft_uri;
                float nft_value;

                try {
                    nft_name = console.readLine("Enter the name of the NFT: ");

                    while (!Utils.uniqueNftName(nftMap.values(), nft_name)) {
                        System.out.println("\tThe name of the NFT is not unique!\n");
                        nft_name = console.readLine("Enter the name of the NFT: ");
                    }

                    nft_uri = console.readLine("Enter the URI of the NFT: ");
                    nft_value = Float.parseFloat(console.readLine("Enter the value of the NFT: "));
                } catch (NumberFormatException e) {
                    System.out.println("\tThe value is supposed to be a float!\n");
                    continue;
                }

                Nft new_nft = new Nft(nft_name, nft_uri, nft_value, clientId);
                nftMap.put(new_nft.getId(), new_nft);
                System.out.println("\nNFT with id " + new_nft.getId() + " and value " + new_nft.getValue() + " minted\n");

            } else if (cmd.equalsIgnoreCase("SET_NFT_PRICE")) {

                String nft_name;
                float new_price;

                try {
                    nft_name = console.readLine("Enter the name of the NFT: ");
                    new_price = Float.parseFloat(console.readLine("Enter the new price of the NFT: "));
                } catch (NumberFormatException e) {
                    System.out.println("\tThe values were of an incorrect type!\n");
                    continue;
                }
                Collection<Nft> nfts = nftMap.values();
                for (Nft nft : nfts) {
                    if (nft.getOwner() == clientId && nft.getName().equals(nft_name)) {
                        nft.setValue(new_price);
                        nftMap.put(nft.getId(), nft);
                        System.out.println("\nNFT with id " + nft.getId() + " and value " + nft.getValue() + " updated\n");
                        return;
                    }
                }

                System.out.println("\tThe NFT with the name " + nft_name + " was not found!\n");

            } else if (cmd.equalsIgnoreCase("SEARCH_NFT")) {

                String nft_name;
                try {
                    nft_name = console.readLine("Enter the string of the NFT to search: ");
                } catch (NumberFormatException e) {
                    System.out.println("\tThe value is supposed to be a string!\n");
                    continue;
                }
                
                Collection<Nft> nfts = Utils.containsStr(nftMap.values(), nft_name);
                for (Nft nft : nfts) {
                    System.out.println(nft);
                }

            } else if (cmd.equalsIgnoreCase("BUY_NFT")) {

                int nft_id;
                ArrayList<Integer> coins_id_to_transfer = new ArrayList<Integer>();

                try {
                    nft_id = Integer.parseInt(console.readLine("Enter the ID of the NFT to buy: "));
                    String coin_ids = console.readLine("Enter the IDs of the coins to transfer (comma-separated): ");
                    coins_id_to_transfer = Utils.coinIdToArray(coin_ids.split(","));
                } catch (NumberFormatException e) {
                    System.out.println("\tThe format of the value or receiver ID is wrong!\n");
                    continue;
                }

                Nft nft = nftMap.get(nft_id);
                if (nft == null) {
                    System.out.println("\tThe NFT with the ID " + nft_id + " was not found!\n");
                    continue;
                }

                // TODO: Catch the exceptions!!!
                spend_coins(coinMap, clientId, nft.getValue(), nft.getOwner(), coins_id_to_transfer);

            } else {
                System.out.println("\tInvalid command :P\n");
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
     */
    private static void spend_coins(BFTMap<Integer, Coin> coinMap, int clientId, float transfer_value, int reciver_id, ArrayList<Integer> coins_id_to_transfer) {
        @SuppressWarnings("unchecked")
        ArrayList<Coin> coins_to_transfer = (ArrayList<Coin>) (ArrayList<?>) coinMap.getValues(coins_id_to_transfer);

        float coins_change = Utils.coins_change(coins_to_transfer, transfer_value);
        if (coins_change < 0) {
            System.out.println("\tThe sum of the coins is lower than the transfer value!\n");
            throw new IllegalArgumentException("The sum of the coins is lower than the transfer value!");
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
