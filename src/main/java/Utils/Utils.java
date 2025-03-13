package Utils;

import java.util.ArrayList;

import Crypto.Coin;

public final class Utils {
    private Utils() {}

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

    public static int coins_change(ArrayList<Coin> coins, float value) {
        float sum = 0;
        for (Coin coin : coins) {
            sum += coin.getValue();
        }
        return Float.compare(sum, value);
    }
}