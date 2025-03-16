package Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InterfaceHandler {

    // ANSI escape codes for colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_UNDERLINE = "\u001B[4m";

    /**
     * Constructor
     */
    public InterfaceHandler() {
        startUp();
        help();
    }

    /**
     * Print the startup message
     */
    public static void startUp() {
System.out.println(ANSI_GREEN + "  _   _                _      _____            _     \n" +
                   " | | | | __ ___      _| | __ |_   _|   _  __ _| |__  \n" +
                   " | |_| |/ _` \\ \\ /\\ / / |/ /   | || | | |/ _` | '_ \\ \n" +
                   " |  _  | (_| |\\ V  V /|   <    | || |_| | (_| | | | |\n" +
                   " |_| |_|\\__,_| \\_/\\_/ |_|\\_\\   |_| \\__,_|\\__,_|_| |_|\n" +
                   "                                                     " + ANSI_RESET);  
        System.out.println();                                             
    }

    /**
     * Get the current date and time
     */
    private static String getCurrentDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        return dtf.format(LocalDateTime.now());
    }

    /**
     * Print a error message to the user
     * 
     * @param s Error message
     */
    public static void erro(String s) {
        String dateTime = getCurrentDateTime();
        System.out.println(ANSI_RED + ANSI_UNDERLINE + dateTime + ANSI_RESET + " | " + ANSI_RED + "[e]Error:" + " " + s + ANSI_RESET);
    }

    /**
     * Print a info message to the user
     */
    public static void info(String s) {
        String dateTime = getCurrentDateTime();
        System.out.println(ANSI_YELLOW + ANSI_UNDERLINE + dateTime + ANSI_RESET + " | " + ANSI_YELLOW + "[i]Info:" + " " + s + ANSI_RESET);
        
    }

    /**
     * Print a success message to the user
     */
    public static void success(String s) {
        String dateTime = getCurrentDateTime();
        System.out.println(ANSI_GREEN + ANSI_UNDERLINE + dateTime + ANSI_RESET + " | " + ANSI_GREEN + "[s]Success:" + " " + s + ANSI_RESET);
    }

    /**
     * Print the help menu
     */
    public static void help() {
        System.out.println(ANSI_GREEN + "1 - (MINT) " + ANSI_UNDERLINE + "Mint a new coin" + ANSI_RESET + ANSI_PURPLE + " - Mint a new coin" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "2 - (MY_COINS) " + ANSI_UNDERLINE + "List the coins owned by the client" + ANSI_RESET + ANSI_PURPLE + " - List the coins owned by the client" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "3 - (SPEND) " + ANSI_UNDERLINE + "Spend coins to another client" + ANSI_RESET + ANSI_PURPLE + " - Spend coins to another client" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "4 - (MY_NFTS) " + ANSI_UNDERLINE + "List the NFTs owned by the client" + ANSI_RESET + ANSI_PURPLE + " - List the NFTs owned by the client" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "5 - (MINT_NFT) " + ANSI_UNDERLINE + "Mint a new NFT" + ANSI_RESET + ANSI_PURPLE + " - Mint a new NFT" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "6 - (SET_NFT_PRICE) " + ANSI_UNDERLINE + "Set the price of an NFT" + ANSI_RESET + ANSI_PURPLE + " - Set the price of an NFT" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "7 - (SEARCH_NFT) " + ANSI_UNDERLINE + "Search for an NFT by name" + ANSI_RESET + ANSI_PURPLE + " - Search for an NFT by name" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "8 - (BUY_NFT) " + ANSI_UNDERLINE + "Buy an NFT" + ANSI_RESET + ANSI_PURPLE + " - Buy an NFT" + ANSI_RESET);
    }
}