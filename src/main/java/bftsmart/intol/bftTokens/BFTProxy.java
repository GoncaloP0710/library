/**
 * BFT Map implementation (client side).
 *
 */
package bftsmart.intol.bftTokens;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Crypto.Coin;
import Crypto.Nft;
import bftsmart.tom.ServiceProxy;

public class BFTProxy {  
    private final Logger logger = LoggerFactory.getLogger("bftsmart");
    private final ServiceProxy serviceProxy;

    public BFTProxy(int id) {
        serviceProxy = new ServiceProxy(id);
    }
    
    // ----------------------------> Coins
    public Collection<Coin> My_Coins() {
        byte[] rep;
        try {
            BFTTokenMessage request = new BFTTokenMessage();
            request.setType(BFTTokenRequestType.MY_COINS);

            //invokes BFT-SMaRt
            rep = serviceProxy.invokeOrdered(BFTTokenMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send MY_COINS request");
            return null;
        }

        if (rep.length == 0) {
            return null;
        }
        try {
            BFTTokenMessage response = BFTTokenMessage.fromBytes(rep);
            return (Collection<Coin>) response.getCoins();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of MY_COINS request");
            return null;
        }
    }

    public int Mint_Coin(float coinValue) {
        byte[] rep;
        try {
            BFTTokenMessage request = new BFTTokenMessage();
            request.setType(BFTTokenRequestType.MINT);
            request.setCoinValue(coinValue); // Set the coin value

            //invokes BFT-SMaRt
            rep = serviceProxy.invokeOrdered(BFTTokenMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send MINT request");
            return -1;
        }

        if (rep.length == 0) {
            return -1;
        }
        try {
            BFTTokenMessage response = BFTTokenMessage.fromBytes(rep);
            return (int) response.getCoinId();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of MINT request");
            return -1;
        }
    }

    public int Spend_Coin(float coinValue, String receiverId, ArrayList<Integer> coinsId) {
        byte[] rep;
        try {
            BFTTokenMessage request = new BFTTokenMessage();
            request.setType(BFTTokenRequestType.SPEND);
            request.setCoinValue(coinValue); // Set the coin value
            request.setCoinsId(coinsId); // Set the coins id
            request.setReciverId(receiverId); // Set the receiver id

            //invokes BFT-SMaRt
            rep = serviceProxy.invokeOrdered(BFTTokenMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send SPEND request");
            return -1;
        }

        if (rep.length == 0) {
            return -1;
        }
        
        try {
            BFTTokenMessage response = BFTTokenMessage.fromBytes(rep);
            return (int) response.getCoinValue();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of SPEND request");
            return -1;
        }
    }

    // ----------------------------> Nfts
    public Collection<Nft> My_Nfts() {
        byte[] rep;
        try {
            BFTTokenMessage request = new BFTTokenMessage();
            request.setType(BFTTokenRequestType.MY_NFTS);

            //invokes BFT-SMaRt
            rep = serviceProxy.invokeOrdered(BFTTokenMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send MY_NFTS request");
            return null;
        }

        if (rep.length == 0) {
            return null;
        }

        try {
            BFTTokenMessage response = BFTTokenMessage.fromBytes(rep);
            return (Collection<Nft>) response.getNfts();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of MY_NFTS request");
            return null;
        }
    }

    public int Mint_Nft(String nftName, String nftUri, float nftPrice) {
        byte[] rep;

        try {
            BFTTokenMessage request = new BFTTokenMessage();
            request.setType(BFTTokenRequestType.MINT_NFT);
            request.setNftName(nftName); // Set nft values
            request.setNftUri(nftUri);
            request.setNftValue(nftPrice);

            //invokes BFT-SMaRt
            rep = serviceProxy.invokeOrdered(BFTTokenMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send MINT_NFT request");
            return -1;
        }

        if (rep.length == 0) {
            return -1;
        }
        try {
            BFTTokenMessage response = BFTTokenMessage.fromBytes(rep);
            return (int) response.getNftId();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of MINT_NFT request");
            return -1;
        }
        
    }

    public int Set_Nft_Price(int nftId, float nftPrice) {
        byte[] rep;

        try {
            BFTTokenMessage request = new BFTTokenMessage();
            request.setType(BFTTokenRequestType.SET_NFT_PRICE);
            request.setNftId(nftId); // Set nft values
            request.setNftValue(nftPrice);

            //invokes BFT-SMaRt
            rep = serviceProxy.invokeOrdered(BFTTokenMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send SET_NFT_PRICE request");
            return -1;
        }

        if (rep.length == 0) {
            return -1;
        }
        try {
            BFTTokenMessage response = BFTTokenMessage.fromBytes(rep);
            return (int) response.getNftId();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of SET_NFT_PRICE request");
            return -1;
        }
    }

    public int Buy_Nft(int nftId, ArrayList<Integer> coinsId) {
        byte[] rep;

        try {
            BFTTokenMessage request = new BFTTokenMessage();
            request.setType(BFTTokenRequestType.BUY_NFT);
            request.setNftId(nftId); // Set nft values
            request.setCoinsId(coinsId);

            //invokes BFT-SMaRt
            rep = serviceProxy.invokeOrdered(BFTTokenMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send BUY_NFT request");
            return -1;
        }

        if (rep.length == 0) {
            return -1;
        }
        try {
            BFTTokenMessage response = BFTTokenMessage.fromBytes(rep);
            return (int) response.getCoinId();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of BUY_NFT request");
            return -1;
        }
    }

    public Collection<Nft> Search_Nft(String nftValue) {
        byte[] rep;
        try {
            BFTTokenMessage request = new BFTTokenMessage();
            request.setType(BFTTokenRequestType.SEARCH_NFT);
            request.setNftName(nftValue);

            //invokes BFT-SMaRt
            rep = serviceProxy.invokeOrdered(BFTTokenMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send SEARCH_NFT request");
            return null;
        }

        if (rep.length == 0) {
            return null;
        }

        try {
            BFTTokenMessage response = BFTTokenMessage.fromBytes(rep);
            return (Collection<Nft>) response.getNfts();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of SEARCH_NFT request");
            return null;
        }
    }



}
