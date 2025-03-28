package bftsmart.intol.bftTokens;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import Crypto.Coin;
import Crypto.Nft;

public class BFTTokenMessage implements Serializable {

    private BFTTokenRequestType type;

    // ----------------------------> Coins - Request
    private float coin_value; // MINT / SPEND
    private String reciver_id; // SPEND
    private ArrayList<Integer> coins_id; // SPEND / BUY_NFT

    // ----------------------------> Coins - Response
    private Collection<Coin> coins; // MY_COINS
    private int coin_id; // MINT / SPEND

    // ----------------------------> NFTs - Request
    private int nft_id; // SET_NFT_PRICE / BUY_NFT / MINT_NFT - Response / BUY_NFT - Response
    private String nft_name; // MINT_NFT
    private String nft_uri; // MINT_NFT
    private float nft_value; // MINT_NFT / SET_NFT_PRICE
    private String nft_text; // SEARCH_NFT

    // ----------------------------> NFTs - Response
    private Collection<Nft> nfts; // MY_NFTS / SEARCH_NFT

    public BFTTokenMessage() {
    }

    public static <K,V> byte[] toBytes(BFTTokenMessage message) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(message);

        objOut.flush();
        byteOut.flush();

        return byteOut.toByteArray();
    }

    public static <K,V> BFTTokenMessage fromBytes(byte[] rep) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(rep);
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        return (BFTTokenMessage) objIn.readObject();
    }

    public BFTTokenRequestType getType() {
        return type;
    }

    public void setType(BFTTokenRequestType type) {
        this.type = type;
    }

    // ----------------------------> Coins - Request
    public float getCoinValue() {
        return coin_value;
    }

    public void setCoinValue(float coin_value) {
        this.coin_value = coin_value;
    }

    public String getReciverId() {
        return reciver_id;
    }

    public void setReciverId(String reciver_id) {
        this.reciver_id = reciver_id;
    }

    public ArrayList<Integer> getCoinsId() {
        return coins_id;
    }

    public void setCoinsId(ArrayList<Integer> coins_id) {
        this.coins_id = coins_id;
    }

    // ----------------------------> Coins - Response
    public Collection<Coin> getCoins() {
        return coins;
    }

    public void setCoins(Collection<Coin> coins) {
        this.coins = coins;
    }

    public int getCoinId() {
        return coin_id;
    }

    public void setCoinId(int coin_id) {
        this.coin_id = coin_id;
    }

    // ----------------------------> NFTs
    public int getNftId() {
        return nft_id;
    }

    public void setNftId(int nft_id) {
        this.nft_id = nft_id;
    }

    public String getNftName() {
        return nft_name;
    }

    public void setNftName(String nft_name) {
        this.nft_name = nft_name;
    }

    public String getNftUri() {
        return nft_uri;
    }

    public void setNftUri(String nft_uri) {
        this.nft_uri = nft_uri;
    }

    public float getNftValue() {
        return nft_value;
    }

    public void setNftValue(float nft_value) {
        this.nft_value = nft_value;
    }

    public String getNftText() {
        return nft_text;
    }

    public void setNftText(String nft_text) {
        this.nft_text = nft_text;
    }

    public Collection<Nft> getNfts() {
        return nfts;
    }

    public void setNfts(Collection<Nft> nfts) {
        this.nfts = nfts;
    }
}
