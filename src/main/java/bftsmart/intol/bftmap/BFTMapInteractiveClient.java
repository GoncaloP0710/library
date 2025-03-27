/**
 * BFT Map implementation (interactive client).
 *
 */
package bftsmart.intol.bftmap;

import Crypto.Coin;
import Crypto.Nft;
import Utils.InterfaceHandler;
import Utils.Utils;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class BFTMapInteractiveClient {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int clientId = (args.length > 0) ? Integer.parseInt(args[0]) : 1001;
        BFTProxy coinProxy = new BFTProxy(Utils.generateId(clientId));
        BFTProxy nftProxy = new BFTProxy(Utils.generateId(clientId+1)+1);
        Console console = System.console();
        InterfaceHandler.startUp();
        InterfaceHandler.help();

        mainLoop:
        while (true) {
            String cmd = console.readLine("\n  > ");

            if (cmd.equalsIgnoreCase("HELP")) {

                InterfaceHandler.help();
                
            } else if (cmd.equalsIgnoreCase("MINT")) {
                float coin_value;
                try {
                    coin_value = Integer.parseInt(console.readLine("Enter the value of the coin: "));
                } catch (NumberFormatException e) {
                    InterfaceHandler.erro("\tThe value is supposed to be a float!\n");
                    continue mainLoop;
                }

                //invokes the op on the servers
                int coin_id = coinProxy.Mint_Coin(coin_value);
                
                if (coin_id == -1) {
                    InterfaceHandler.erro("\tCoin could not be minted!\n");
                    continue mainLoop;
                }
                
                InterfaceHandler.success("Coin with id " + coin_id + " and value " + coin_value + " minted\n");

            } else if (cmd.equalsIgnoreCase("MY_COINS")) {

                Collection<Coin> coins = coinProxy.My_Coins();

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
                String reciver_id;
                ArrayList<Integer> coins_id_to_transfer = new ArrayList<Integer>();

                try {
                    transfer_value = Float.parseFloat(console.readLine("Enter the value to transfer: "));
                    reciver_id = console.readLine("Enter the id of the receiver: ");
                    String coin_ids = console.readLine("Enter the IDs of the coins to transfer (comma-separated): ");
                    coins_id_to_transfer = Utils.coinIdToArray(coin_ids.split(","));

                    int response = coinProxy.Spend_Coin(transfer_value, reciver_id, coins_id_to_transfer);

                    if (response == -1) {
                        InterfaceHandler.erro("\tThe coins could not be spent!\n");
                        continue mainLoop;
                    }

                } catch (NumberFormatException e) {
                    InterfaceHandler.erro("\tThe format of the value or the ID is wrong!\n");
                    continue mainLoop;
                }

            } else if (cmd.equalsIgnoreCase("MY_NFTS")) {

                Collection<Nft> nfts = nftProxy.My_Nfts();

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
                    nft_uri = console.readLine("Enter the URI of the NFT: ");
                    nft_value = Float.parseFloat(console.readLine("Enter the value of the NFT: "));
                } catch (NumberFormatException e) {
                    InterfaceHandler.erro("\tThe value is supposed to be a float!\n");
                    continue mainLoop;
                }

                int nft_id = nftProxy.Mint_Nft(nft_name, nft_uri, nft_value);

                if (nft_id == -1) {
                    InterfaceHandler.erro("\tNFT could not be minted!\n");
                    continue mainLoop;
                }

                InterfaceHandler.success("NFT with id " + nft_id + " and value " + nft_value + " minted\n");

            } else if (cmd.equalsIgnoreCase("SET_NFT_PRICE")) {

                String nft_name;
                float new_price;

                try {
                    nft_name = console.readLine("Enter the name of the NFT: ");
                    new_price = Float.parseFloat(console.readLine("Enter the new price of the NFT: "));
                } catch (NumberFormatException e) {
                    InterfaceHandler.erro("\tThe values were of an incorrect type!\n");
                    continue mainLoop;
                }

                int response = nftProxy.Set_Nft_Price(nft_name, new_price);

                if (response == -1) {
                    InterfaceHandler.erro("\tThe price could not be set!\n");
                    continue mainLoop;
                }

                InterfaceHandler.success("Price of NFT " + nft_name + " set to " + new_price + "\n");

            } else if (cmd.equalsIgnoreCase("SEARCH_NFT")) {

                String nft_name;

                try {
                    nft_name = console.readLine("Enter the string of the NFT to search: ");
                } catch (NumberFormatException e) {
                    InterfaceHandler.erro("\tThe value is supposed to be a string!\n");
                    continue mainLoop;
                }
                
                Collection<Nft> nfts = nftProxy.Search_Nft(nft_name);

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

                    int response = nftProxy.Buy_Nft(nft_id, coins_id_to_transfer);

                    if (response == -1) {
                        InterfaceHandler.erro("\tThe NFT could not be bought!\n");
                        continue mainLoop;
                    }

                } catch (NumberFormatException e) {
                    InterfaceHandler.erro("\tThe format of the value or receiver ID is wrong!\n");
                    continue mainLoop;
                }
            } else {
                InterfaceHandler.erro("\tInvalid command :P\n");
            }
        }
    }
}
