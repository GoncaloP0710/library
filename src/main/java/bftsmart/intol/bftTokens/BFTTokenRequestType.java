/**
 * BFT Map implementation (message types).
 * 
 */

package bftsmart.intol.bftTokens;

public enum BFTTokenRequestType {

    // ----------------------------> Coins
    MY_COINS,
    MINT,
    SPEND,

    // ----------------------------> NFTs
    MY_NFTS,
    MINT_NFT,
    SET_NFT_PRICE,
    SEARCH_NFT,
    BUY_NFT,
}