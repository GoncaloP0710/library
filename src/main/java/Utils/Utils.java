package Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import Crypto.Coin;
import Crypto.Nft;

public final class Utils {
    private Utils() {}

    public static int generateId(int owner) {
        Random random = new Random();
        int salt = owner * 31;
        return random.nextInt(100000) + salt;
    }

    public static ArrayList<Integer> coinIdToArray(String[] coin_id_array) {
        ArrayList<Integer> coins_to_transfer = new ArrayList<>();
        for (String coin_id_str : coin_id_array) {
            try {
                int coin_id = Integer.parseInt(coin_id_str.trim());
                coins_to_transfer.add(coin_id);
            } catch (NumberFormatException e) {
                System.out.println("\tInvalid coin ID format: " + coin_id_str + "\n");
            }
        }
        return coins_to_transfer;
    }

    public static float coins_change(ArrayList<Coin> coins, float value) {
        float sum = 0;
        for (Coin coin : coins) {
            sum += coin.getValue();
        }
        return value - sum;
    }

    public static boolean uniqueNftName(Collection<Nft> nfts, String name) {
        for (Nft nft : nfts) {
            if (nft.getName().equals(name)) {
                return false;
            }
        }
        return true;
    }

    public static ArrayList<Nft> containsStr(Collection<Nft> nfts, String name) {
        ArrayList<Nft> nfts_with_name = new ArrayList<>();
        String lowerCaseName = name.toLowerCase();
        for (Nft nft : nfts) {
            if (nft.getName().toLowerCase().contains(lowerCaseName)) {
                nfts_with_name.add(nft);
            }
        }
        return nfts_with_name;
    }
}