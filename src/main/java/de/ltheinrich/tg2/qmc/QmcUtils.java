package de.ltheinrich.tg2.qmc;

import de.ltheinrich.tg2.seg.SegTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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

    static void printExtracted(QmcMinifier mini) {
        mini.reqTable.forEach(QmcUtils::print);
        System.out.println("Still required: " + Arrays.toString(mini.reqIndices.toArray()));
    }

    static void printExtractedKonjunktion(QmcMinifier mini, int pad) {
        mini.reqTable.stream().map(terms -> QmcUtils.termsToKonjunktion(terms, pad)).forEach(QmcUtils::print);
        System.out.println("Still required: " + Arrays.toString(mini.reqIndices.toArray()));
    }

    static void print7SegFinals(List<List<Integer>> allTerms, char segment) {
        QmcMinifier seg = new QmcMinifier(allTerms.stream().toList(), SegTable.function(segment - 65), 10);
        seg.minify();

        System.out.println("Segment " + segment + ":");
        System.out.println("MSB  LSB");
        seg.reqTable.forEach(terms -> {
            QmcUtils.printPlain(QmcUtils.termsToKonjunktion(terms, 4));
            System.out.println(terms);
        });
        System.out.println();

        if (!seg.reqIndices.isEmpty())
            System.out.println("Still required: " + Arrays.toString(seg.reqIndices.toArray()));
    }

    static void printAllIndices(QuineMcCluskey[] qmcs) {
        for (int i = 0; i < qmcs.length; i++) {
            System.out.println(i + ":");
            printIndices(qmcs[i]);
            System.out.println();
        }
    }

    static void printAllExtracted(QmcMinifier[] minis) {
        for (int i = 0; i < minis.length; i++) {
            System.out.println(i + ":");
            printExtracted(minis[i]);
            System.out.println();
        }
    }

    static void print(List<Integer> c) {
        printPlain(c);
        System.out.println();
    }

    static void printPlain(List<Integer> c) {
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

    static String toBinaryPad(int i, int pad) {
        return String.format("%" + String.format("%02d", pad) + "d", Integer.parseInt(SegTable.toBinary(i)));
    }

    static List<Integer> termsToKonjunktion(List<Integer> terms, int pad) {
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