/**
 * BFT Map implementation (message types).
 * 
 */

package bftsmart.intol.bftmap;

public enum BFTMapRequestType {

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