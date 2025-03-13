/**
 * BFT Map implementation (interactive client).
 *
 */
package bftsmart.intol.bftmap;

import Crypto.Coin;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class BFTMapInteractiveClient {

    public static void main(String[] args) throws IOException {
        int clientId = (args.length > 0) ? Integer.parseInt(args[0]) : 1001;
        BFTMap<Integer, String> bftMap = new BFTMap<>(clientId);
        BFTMap<Integer, Coin> coinMap = new BFTMap<>(clientId);

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

                System.out.println("\tYou are supposed to implement this command :)\n");

            } else if (cmd.equalsIgnoreCase("SIZE")) {

                //invokes the op on the servers
                int size = bftMap.size();

                System.out.println("\nSize of the map is " + size + "\n");

            } else if (cmd.equalsIgnoreCase("EXIT")) {

                System.out.println("\tEXIT: Bye bye!\n");
                System.exit(0);

            } else if (cmd.equalsIgnoreCase("MINT") && clientId == 4) { // Make sure only the client 4 can mint coins

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
                        System.out.println("\nCoin with id " + coin.getId() + " and value " + coin.getValue() + "\n");
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
                    coins_id_to_transfer = Utils.Utils.coinIdToArray(coin_ids.split(","));
                } catch (NumberFormatException e) {
                    System.out.println("\tThe format of the value or receiver ID is wrong!\n");
                    continue;
                }

                @SuppressWarnings("unchecked")
                ArrayList<Coin> coins_to_transfer = (ArrayList<Coin>) (ArrayList<?>) coinMap.getValues(coins_id_to_transfer);

                int coins_change = Utils.Utils.coins_change(coins_to_transfer, transfer_value);
                if (coins_change < 0) {
                    System.out.println("\tThe sum of the coins is lower than the transfer value!\n");
                    continue;
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

            } else {
                System.out.println("\tInvalid command :P\n");
            }
        }
    }

}
