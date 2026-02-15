package Tier1.DistributedURLShortener.util;

// ---------------------------------------------------------
// 2. COMPONENT: BASE62 ENCODER (The Core Logic)
// ---------------------------------------------------------
public class Base62 {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final char[] BASE_62_CHARS = ALPHABET.toCharArray();
    private static final int BASE = 62;

    public static String encode(long id) {
        if (id == 0) return String.valueOf(BASE_62_CHARS);

        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            int remainder = (int) (id % BASE);
            sb.append(BASE_62_CHARS[remainder]);
            id /= BASE;
        }
        return sb.reverse().toString();
    }

    public static long decode(String str) {
        long id = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            int val = ALPHABET.indexOf(c); // O(1) lookup in reality via int array
            id = id * BASE + val;
        }
        return id;
    }
}
