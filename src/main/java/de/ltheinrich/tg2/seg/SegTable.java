package de.ltheinrich.tg2.seg;

import java.util.ArrayList;
import java.util.List;

public class SegTable {

    // Segment x Num
    static boolean[][] table = new boolean[][]{
            new boolean[]{true, false, true, true, false, true, true, true, true, true, /* dont care */ true, true, true, true, true, true}, // A
            new boolean[]{true, true, true, true, true, false, false, true, true, true, /* dont care */ true, true, true, true, true, true}, // B
            new boolean[]{true, true, false, true, true, true, true, true, true, true, /* dont care */ true, true, true, true, true, true}, // C
            new boolean[]{true, false, true, true, false, true, true, false, true, true, /* dont care */ true, true, true, true, true, true}, // D
            new boolean[]{true, false, true, false, false, false, true, false, true, false, /* dont care */ true, true, true, true, true, true}, // E
            new boolean[]{true, false, false, false, true, true, true, false, true, true, /* dont care */ true, true, true, true, true, true}, // F
            new boolean[]{false, false, true, true, true, true, true, false, true, true, /* dont care */ true, true, true, true, true, true} // G
    };

    public static List<Integer> function(int segment) {
        List<Integer> f = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (table[segment][i]) {
                f.add(i);
            }
        }
        return f;
    }

    static boolean minterm(boolean a, boolean b, boolean c, boolean d, boolean e, boolean f, boolean g, int n) {
        return (!a || table[0][n])
                && (!b || table[1][n])
                && (!c || table[2][n])
                && (!d || table[3][n])
                && (!e || table[4][n])
                && (!f || table[5][n])
                && (!g || table[6][n]);
    }

    static boolean[] terms(boolean a, boolean b, boolean c, boolean d, boolean e, boolean f, boolean g) {
        return new boolean[]{
                minterm(a, b, c, d, e, f, g, 0),
                minterm(a, b, c, d, e, f, g, 1),
                minterm(a, b, c, d, e, f, g, 2),
                minterm(a, b, c, d, e, f, g, 3),
                minterm(a, b, c, d, e, f, g, 4),
                minterm(a, b, c, d, e, f, g, 5),
                minterm(a, b, c, d, e, f, g, 6),
                minterm(a, b, c, d, e, f, g, 7),
                minterm(a, b, c, d, e, f, g, 8),
                minterm(a, b, c, d, e, f, g, 9),
                minterm(a, b, c, d, e, f, g, 10),
                minterm(a, b, c, d, e, f, g, 11),
                minterm(a, b, c, d, e, f, g, 12),
                minterm(a, b, c, d, e, f, g, 13),
                minterm(a, b, c, d, e, f, g, 14),
                minterm(a, b, c, d, e, f, g, 15)

        };
    }

    public static List<String> minterms(boolean a, boolean b, boolean c, boolean d, boolean e, boolean f, boolean g) {
        List<String> mintermsBinary = new ArrayList<>();
        boolean[] terms = terms(a, b, c, d, e, f, g);
        for (int n = 0; n < terms.length; n++) {
            if (terms[n]) {
                mintermsBinary.add(String.format("%04d", Integer.parseInt(toBinary(n))));
            }
        }
        return mintermsBinary;
    }

    public static List<List<String>> allFunctions() {
        List<List<String>> functions = new ArrayList<>();
        for (int i = 0; i < 128; i++) {
            String binary = String.format("%07d", Integer.parseInt(toBinary(i)));
            List<String> minterms = minterms(binary.charAt(0) == '1', binary.charAt(1) == '1', binary.charAt(2) == '1', binary.charAt(3) == '1', binary.charAt(4) == '1', binary.charAt(5) == '1', binary.charAt(6) == '1');
            functions.add(minterms);
        }
        return functions;
    }

    public static String toBinary(int a) {
        var b = "" + a % 2;
        var x = a / 2;
        while (x != 0) {
            b = x % 2 + b;
            x /= 2;
        }
        return b;
    }
}