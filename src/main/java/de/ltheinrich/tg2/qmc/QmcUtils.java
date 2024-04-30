package de.ltheinrich.tg2.qmc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QmcUtils {
    static void printGroup(QuineMcCluskey qmc, int n) {
        qmc.groups.get(n).forEach(QmcUtils::print);
    }

    static void printResults(QuineMcCluskey qmc) {
        qmc.unchecked().forEach(QmcUtils::print);
    }

    static void printIndices(QuineMcCluskey qmc) {
        qmc.mintermIndices(qmc.unchecked()).forEach(indices -> {
            System.out.println(Arrays.toString(indices.toArray()));
        });
    }

    static void printMinifyTable(QmcMinifier mini) {
        mini.minifyTable.forEach(indices -> {
            System.out.println(Arrays.toString(indices.toArray()));
        });
    }

    public static void printExtracted(QmcMinifier mini) {
        mini.reqTable.forEach(QmcUtils::print);
        System.out.println("Still required: " + Arrays.toString(mini.reqIndices.toArray()));
    }

    public static void printMintermsUnchecked(QuineMcCluskey qmc) {
        qmc.mintermIndices(qmc.unchecked()).forEach(QmcUtils::print);
    }

    static void printExtractedAndKonjunktion(QmcMinifier mini, int pad) {
        mini.reqTable.forEach(terms -> {
            QmcUtils.printPlain(QmcUtils.termsToKonjunktion(terms, pad));
            System.out.println(terms);
        });
        System.out.println("Still required: " + Arrays.toString(mini.reqIndices.toArray()));
    }

    static void printAllIndices(QuineMcCluskey[] qmcs) {
        for (int i = 0; i < qmcs.length; i++) {
            System.out.println(i + ":");
            printIndices(qmcs[i]);
            System.out.println();
        }
    }

    public static void printAllExtracted(QmcMinifier[] minis) {
        for (int i = 0; i < minis.length; i++) {
            System.out.println(i + ":");
            printExtracted(minis[i]);
            System.out.println();
        }
    }

    public static void printAllMintermsUnchecked(QuineMcCluskey[] qmcs) {
        for (int i = 0; i < qmcs.length; i++) {
            System.out.println(i + ":");
            printMintermsUnchecked(qmcs[i]);
            System.out.println();
        }
    }

    public static void print(List<Integer> c) {
        printPlain(c);
        System.out.println();
    }

    public static void printPlain(List<Integer> c) {
        for (int i : c) {
            System.out.print((i == -1 ? "-" : i) + " ");
        }
    }

    static int toDecimal(String binary) {
        var decimal = 0;

        while (!binary.isEmpty()) {
            decimal *= 2;
            decimal += binary.charAt(0) == '1' ? 1 : 0;
            binary = binary.substring(1, binary.length());
        }

        return decimal;
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

    static String toBinaryPad(int i, int pad) {
        return String.format("%" + String.format("%02d", pad) + "d", Integer.parseInt(toBinary(i)));
    }

    public static List<Integer> termsToKonjunktion(List<Integer> terms, int pad) {
        List<Integer> konjunktion = new ArrayList<>();
        List<String> minterme = terms.stream().map(minterm -> toBinaryPad(minterm, pad)).toList();
        for (String minterm : minterme) {
            char[] chars = minterm.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                int d = chars[i] - 48;
                if (konjunktion.size() <= i) {
                    konjunktion.add(d);
                } else if (konjunktion.get(i) != -1 && konjunktion.get(i) != d) {
                    konjunktion.set(i, -1);
                }
            }
        }
        return konjunktion;
    }
}