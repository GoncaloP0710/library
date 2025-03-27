/**
 * BFT Map implementation (server side).
 *
 */
package bftsmart.intol.bftmap;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Crypto.Coin;
import Crypto.Nft;
import Utils.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

public class BFTMapServer<K, V> extends DefaultSingleRecoverable {
    private final Logger logger = LoggerFactory.getLogger("bftsmart");
    private TreeMap<K, V> replicaMapCoin;
    private TreeMap<K, V> replicaMapNft;

    //The constructor passes the id of the server to the super class
    public BFTMapServer(int id) {
        replicaMapCoin = new TreeMap<>();
        replicaMapNft = new TreeMap<>();

        //turn-on BFT-SMaRt'replica
        new ServiceReplica(id, this, this);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Use: java BFTMapServer <server id>");
            System.exit(-1);
        }
        new BFTMapServer<Integer, Coin>(Integer.parseInt(args[0]));
    }

    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        //all operations must be defined here to be invoked by BFT-SMaRt
        try {
            BFTMapMessage response = new BFTMapMessage();
            BFTMapMessage request = BFTMapMessage.fromBytes(command);
            BFTMapRequestType cmd = request.getType();

            logger.info("Ordered execution of a {} request from {}", cmd, msgCtx.getSender());

            switch (cmd) {
                // -----------------------------------------> Coins
                case MY_COINS:
                    System.out.println("MY_COINS request from " + msgCtx.getSender()); 
                    Collection<Coin> coins = new ArrayList<>();
                    for (V coin : replicaMapCoin.values()) {
                        if (coin instanceof Coin && ((Coin) coin).getOwner() == msgCtx.getSender())
                            coins.add((Coin) coin);
                    }
                    response.setCoins(coins);
                    return BFTMapMessage.toBytes(response);

                case MINT:
                    System.out.println("MINT request from " + msgCtx.getSender()); 
                    if (msgCtx.getSender() == 4) {
                        float value = request.getCoinValue();
                        if (value > 0) {
                            Coin new_coin = new Coin(value, msgCtx.getSender());
                            replicaMapCoin.put((K) Integer.valueOf(new_coin.getId()), (V) new_coin);
                            response.setCoinId(new_coin.getId());
                        } else {
                            response.setCoinId(-1);
                        }
                    } else {
                        response.setCoinId(-1);
                    }
                    return BFTMapMessage.toBytes(response);

                case SPEND:
                    System.out.println("SPEND request from " + msgCtx.getSender());
                    // Get the coins to spend
                    ArrayList<Coin> coins_to_spend = new ArrayList<>();
                    for (Integer coin_id : request.getCoinsId()) {
                        Coin coin = (Coin) replicaMapCoin.get((K) coin_id);
                        if (coin != null && coin.getOwner() == msgCtx.getSender()) {
                            coins_to_spend.add(coin);
                        }
                    }

                    float coins_change = Utils.coins_change(coins_to_spend, request.getCoinValue());
                    if (coins_change < 0) {
                        response.setCoinId(-1);
                        return BFTMapMessage.toBytes(response);
                    } else {
                        for (Coin coin : coins_to_spend) { // Remove the coins from the sender
                            replicaMapCoin.remove((K) Integer.valueOf(coin.getId()));
                        }

                        // Create new coin for the receiver
                        Coin new_coin = new Coin(request.getCoinValue(), Integer.parseInt(request.getReciverId()));
                        replicaMapCoin.put((K) Integer.valueOf(new_coin.getId()), (V) new_coin);
                    
                        if (coins_change > 0) { // Create new coin for the sender with the change
                            Coin change = new Coin(coins_change, msgCtx.getSender());
                            replicaMapCoin.put((K) Integer.valueOf(change.getId()), (V) change);
                        }
                        response.setCoinId(new_coin.getId());
                        return BFTMapMessage.toBytes(response);
                    }
                    
                // -----------------------------------------> NFTs
                case MY_NFTS:
                    System.out.println("MY_NFTS request from " + msgCtx.getSender());
                    Collection<Nft> nfts = new ArrayList<>();
                    for (V nft : replicaMapNft.values()) {
                        if (nft instanceof Nft && ((Nft) nft).getOwner() == msgCtx.getSender())
                        nfts.add((Nft) nft);
                    }
                    response.setNfts(nfts);
                    return BFTMapMessage.toBytes(response);

                case MINT_NFT:
                    System.out.println("MINT_NFT request from " + msgCtx.getSender());
                    float value = request.getNftValue();
                    String name = request.getNftName();
                    String uri = request.getNftUri();
                    if (value > 0) {

                        for (V nft : replicaMapNft.values()) {
                            if (nft instanceof Nft && ((Nft) nft).getName().equals(name)) {
                                response.setNftId(-1);
                                return BFTMapMessage.toBytes(response);
                            }
                        }

                        Nft new_nft = new Nft(name, uri, value, msgCtx.getSender());
                        replicaMapNft.put((K) Integer.valueOf(new_nft.getId()), (V) new_nft);
                        response.setNftId(new_nft.getId());
                    } else {
                        response.setCoinId(-1);
                    }
                    
                    return BFTMapMessage.toBytes(response);

                case SET_NFT_PRICE:
                    System.out.println("SET_NFT_PRICE request from " + msgCtx.getSender());
                    int nft_id = request.getNftId();
                    float nft_value = request.getNftValue();
                    Nft nft = (Nft) replicaMapNft.get((K) Integer.valueOf(nft_id));
                    
                    if (nft == null || nft.getOwner() != msgCtx.getSender()) {
                        response.setNftId(-1);
                        return BFTMapMessage.toBytes(response);
                    }

                    nft.setValue(nft_value);
                    replicaMapNft.put((K) Integer.valueOf(nft_id), (V) nft);
                    response.setNftId(nft_id);
                    return BFTMapMessage.toBytes(response);

                case SEARCH_NFT:
                    System.out.println("SEARCH_NFT request from " + msgCtx.getSender());
                    String nft_name = request.getNftName();
                    Collection<Nft> nfts_found = new ArrayList<>();
                    for (V nft_search : replicaMapNft.values()) {
                        if (nft_search instanceof Nft && ((Nft) nft_search).getName().toLowerCase().contains(nft_name.toLowerCase()))
                            nfts_found.add((Nft) nft_search);
                    }
                    response.setNfts(nfts_found);
                    return BFTMapMessage.toBytes(response);

                case BUY_NFT:
                    System.out.println("BUY_NFT request from " + msgCtx.getSender());
                    Nft nft_to_buy = (Nft) replicaMapNft.get((K) Integer.valueOf(request.getNftId()));
                    if (nft_to_buy == null) {
                        response.setCoinId(-1);
                        return BFTMapMessage.toBytes(response);
                    }

                    ArrayList<Coin> coins_to_buy = new ArrayList<>();
                    for (Integer coin_id : request.getCoinsId()) {
                        Coin coin = (Coin) replicaMapCoin.get((K) coin_id);
                        if (coin != null && coin.getOwner() == msgCtx.getSender()) {
                            coins_to_buy.add(coin);
                        }
                    }

                    float coins_change2 = Utils.coins_change(coins_to_buy, request.getCoinValue());
                    if (coins_change2 < 0) {
                        response.setCoinId(-1);
                        return BFTMapMessage.toBytes(response);
                    } else {
                        for (Coin coin : coins_to_buy) { // Remove the coins from the sender
                            replicaMapCoin.remove((K) Integer.valueOf(coin.getId()));
                        }

                        // Create new coin for the receiver
                        Coin new_coin = new Coin(request.getCoinValue(), Integer.parseInt(request.getReciverId()));
                        replicaMapCoin.put((K) Integer.valueOf(new_coin.getId()), (V) new_coin);
                    
                        if (coins_change2 > 0) { // Create new coin for the sender with the change
                            Coin change = new Coin(coins_change2, msgCtx.getSender());
                            replicaMapCoin.put((K) Integer.valueOf(change.getId()), (V) change);
                        }

                        nft_to_buy.setOwner(msgCtx.getSender());
                        replicaMapNft.put((K) Integer.valueOf(nft_to_buy.getId()), (V) nft_to_buy);
                        response.setCoinId(new_coin.getId());
                        return BFTMapMessage.toBytes(response);
                    }
            }
            return null;
        }catch (IOException | ClassNotFoundException ex) {
            logger.error("Failed to process ordered request", ex);
            return new byte[0];
        }
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
        logger.info("Unordered execution of a request from {}", msgCtx.getSender());
        return new byte[0]; // Return an appropriate response
    }

    @Override
    public byte[] getSnapshot() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(replicaMapCoin);
            out.writeObject(replicaMapNft);
            out.flush();
            bos.flush();
            return bos.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace(); //debug instruction
            return new byte[0];
        }
    }

    @Override
    public void installSnapshot(byte[] state) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(state);
            ObjectInput in = new ObjectInputStream(bis)) {
                replicaMapCoin = (TreeMap<K, V>) in.readObject();
                replicaMapNft = (TreeMap<K, V>) in.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            ex.printStackTrace(); //debug instruction
        }
    }
}
