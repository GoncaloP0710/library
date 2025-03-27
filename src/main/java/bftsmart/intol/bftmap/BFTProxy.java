/**
 * BFT Map implementation (client side).
 *
 */
package bftsmart.intol.bftmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Crypto.Coin;
import Crypto.Nft;
import bftsmart.tom.ServiceProxy;

// TODO: O Gajo falou disto n ser preciso mas depois disse q era para o cliente n tar sempre a chamar proxy e ficar feio
public class BFTProxy<K, V> {  
    private final Logger logger = LoggerFactory.getLogger("bftsmart");
    private final ServiceProxy serviceProxy;

    public BFTProxy(int id) {
        serviceProxy = new ServiceProxy(id);
    }

    // TODO: Implement Rest of Methods
    
    // ----------------------------> Coins
    public Collection<Coin> My_Coins() {
        byte[] rep;
        try {
            BFTMapMessage request = new BFTMapMessage();
            request.setType(BFTMapRequestType.MY_COINS);

            //invokes BFT-SMaRt
            rep = serviceProxy.invokeOrdered(BFTMapMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send MY_COINS request");
            return null;
        }

        if (rep.length == 0) {
            return null;
        }
        try {
            BFTMapMessage response = BFTMapMessage.fromBytes(rep);
            return (Collection<Coin>) response.getCoins();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of MY_COINS request");
            return null;
        }
    }

    public int Mint_Coin(int coinValue) {
        byte[] rep;
        try {
            BFTMapMessage request = new BFTMapMessage();
            request.setType(BFTMapRequestType.MINT);
            request.setCoinValue(coinValue); // Set the coin value

            //invokes BFT-SMaRt
            rep = serviceProxy.invokeOrdered(BFTMapMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send MINT request");
            return -1;
        }

        if (rep.length == 0) {
            return -1;
        }
        try {
            BFTMapMessage response = BFTMapMessage.fromBytes(rep);
            return (int) response.getCoinValue();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of MINT request");
            return -1;
        }
    }

    public int Spend_Coin(int coinValue, String receiverId, String[] coinsId) {
        byte[] rep;
        try {
            BFTMapMessage request = new BFTMapMessage();
            request.setType(BFTMapRequestType.SPEND);
            request.setCoinValue(coinValue); // Set the coin value
            request.setCoinsId(coinsId); // Set the coins id
            request.setReciverId(receiverId); // Set the receiver id

            //invokes BFT-SMaRt
            rep = serviceProxy.invokeOrdered(BFTMapMessage.toBytes(request));
        } catch (IOException e) {
            logger.error("Failed to send SPEND request");
            return -1;
        }

        if (rep.length == 0) {
            return -1;
        }
        try {
            BFTMapMessage response = BFTMapMessage.fromBytes(rep);
            return (int) response.getCoinValue();
        } catch (ClassNotFoundException | IOException ex) {
            logger.error("Failed to deserialized response of SPEND request");
            return -1;
        }
    }

}
