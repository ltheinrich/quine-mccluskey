package de.ltheinrich.tg2.seg;

import de.ltheinrich.tg2.qmc.BundleMinifier;
import de.ltheinrich.tg2.qmc.QmcMinifier;
import de.ltheinrich.tg2.qmc.QmcUtils;
import de.ltheinrich.tg2.qmc.QuineMcCluskey;

import java.util.*;

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
    static List<Integer> dontCares = List.of(10, 11, 12, 13, 14, 15);

    public static void printBundle() {
        var start = System.currentTimeMillis();
        QuineMcCluskey[] qmcs = new QuineMcCluskey[128];
        var minis = qmcForAll(qmcs);
        Set<List<Integer>> allTerms = new HashSet<>();
        for (int i = 0; i < minis.length; i++) {
            //allTerms.addAll(mini.reqTable);
            allTerms.addAll(qmcs[i].mintermIndices(qmcs[i].unchecked()));
        }

        QmcUtils.printAllExtracted(minis);
        for (int i = 0; i < minis.length; i++) {
            System.out.println(i + ":" + String.join(";", qmcs[i].mintermIndices(qmcs[i].unchecked()).stream().map(term -> String.join(",", term.stream().map(String::valueOf).toArray(String[]::new))).toArray(String[]::new)));
        }

        /*System.out.println("Reduzierte Primimplikanten:");
        allTerms.forEach(QmcUtils::print);
        System.out.println();*/

        System.out.println("Bündel-Primimplikanten:");
        allTerms.stream().map(term -> String.join(", ", term.stream().map(String::valueOf).toArray(String[]::new))).forEach(System.out::println);
        System.out.println();

        List<List<Integer>> functions = List.of(function(0), function(1), function(2), function(3),
                function(4), function(5), function(6));
        BundleMinifier bm = new BundleMinifier(
                allTerms.stream().toList(),
                functions,
                List.of(10, 11, 12, 13, 14, 15)
        );
        bm.minify();
        System.out.println("Extracted (bundle):");
        bm.reqTable.forEach(terms -> {
            QmcUtils.printPlain(QmcUtils.termsToKonjunktion(terms, 4));
            System.out.println(terms);
        });
        System.out.println("Still required: " + bm.refRow.stream().map(entry -> (char) (entry.getKey() + 65) + " m" + entry.getValue()).toList());
        System.out.println();

        System.out.println("Branching (bundle):");
        bm.bestBranches().forEach(branch -> {
            System.out.println("Branch (bundle):");
            branch.reqTable.forEach(terms -> {
                QmcUtils.printPlain(QmcUtils.termsToKonjunktion(terms, 4));
                System.out.println(terms);
            });
            System.out.println();

            for (int f = 0; f < functions.size(); f++) {
                char seg = (char) (f + 65);
                System.out.println("Segment " + seg + ":");
                QmcMinifier mini = new QmcMinifier(List.of(), function(f));
                mini.minifyTable = new ArrayList<>(branch.reqTable);
                mini.minify();
                System.out.println("Extracted:");
                mini.reqTable.forEach(terms -> {
                    QmcUtils.printPlain(QmcUtils.termsToKonjunktion(terms, 4));
                    System.out.println(terms);
                });
                if (!mini.reqTable.isEmpty()) {
                    System.out.println("Still required: " + mini.reqIndices);
                }
                if (!mini.reqIndices.isEmpty()) {
                    System.out.println("Branching (seg " + seg + "):");
                    List<QmcMinifier> bestBranches = mini.bestBranches();
                    bestBranches.forEach(singleBranch -> {
                        System.out.println("Branch (seg " + seg + "):");
                        singleBranch.reqTable.forEach(terms -> {
                            QmcUtils.printPlain(QmcUtils.termsToKonjunktion(terms, 4));
                            System.out.println(terms);
                        });
                        if (!singleBranch.reqIndices.isEmpty()) {
                            throw new IllegalStateException("Still required: " + Arrays.toString(singleBranch.reqIndices.toArray()));
                        }
                    });
                    if (bestBranches.isEmpty()) {
                        throw new IllegalStateException("Still required, but no branches available");
                    }
                }
                System.out.println();
            }
            System.out.println("-------------------------------------");
            System.out.println();
        });

        System.out.println("Dauer: " + (System.currentTimeMillis() - start) + " ms");
    }


    public static Set<List<Integer>> printAll() {
        var start = System.currentTimeMillis();
        QuineMcCluskey[] qmcs = new QuineMcCluskey[128];
        var minis = qmcForAll(qmcs);
        Set<List<Integer>> allTerms = new HashSet<>();
        for (int i = 0; i < minis.length; i++) {
            //allTerms.addAll(mini.reqTable);
            allTerms.addAll(qmcs[i].mintermIndices(qmcs[i].unchecked()));
        }

        QmcUtils.printAllMintermsUnchecked(qmcs);
        for (int i = 0; i < minis.length; i++) {
            System.out.println(i + ":" + String.join(";", qmcs[i].mintermIndices(qmcs[i].unchecked()).stream().map(term -> String.join(",", term.stream().map(String::valueOf).toArray(String[]::new))).toArray(String[]::new)));
        }

        /*System.out.println("Reduzierte Primimplikanten:");
        allTerms.forEach(QmcUtils::print);
        System.out.println();*/

        System.out.println("Bündel-Primimplikanten:");
        allTerms.stream().map(term -> String.join(", ", term.stream().map(String::valueOf).toArray(String[]::new))).forEach(System.out::println);
        System.out.println();

        print7SegFinals(allTerms.stream().toList(), 'A');
        print7SegFinals(allTerms.stream().toList(), 'B');
        print7SegFinals(allTerms.stream().toList(), 'C');
        print7SegFinals(allTerms.stream().toList(), 'D');
        print7SegFinals(allTerms.stream().toList(), 'E');
        print7SegFinals(allTerms.stream().toList(), 'F');
        print7SegFinals(allTerms.stream().toList(), 'G');

        System.out.println("Dauer: " + (System.currentTimeMillis() - start) + " ms");
        return allTerms;
    }

    static QmcMinifier[] qmcForAll(QuineMcCluskey[] qmcs) {
        QmcMinifier[] minis = new QmcMinifier[128];
        var functions = SegTable.allFunctions();
        for (int i = 0; i < minis.length; i++) {
            var qmc = new QuineMcCluskey(dontCares, functions.get(i).toArray(String[]::new));
            QmcMinifier mini = qmc.runAndMinify(1000);
            /*if (!mini.reqIndices.isEmpty())
                throw new IllegalStateException(qmc.reqIndices.toString());*/
            if (!mini.reqIndices.isEmpty())
                System.out.println(i + ": Still required: " + qmc.reqIndices.toString());
            minis[i] = mini;
            if (qmcs != null)
                qmcs[i] = qmc;
        }
        return minis;
    }

    static void print7SegFinals(List<List<Integer>> allTerms, char segment) {
        QmcMinifier seg = new QmcMinifier(allTerms.stream().toList(), SegTable.function(segment - 65), dontCares);
        seg.minify();

        System.out.println("Segment " + segment + ":");
        System.out.println("MSB  LSB");
        seg.reqTable.forEach(terms -> {
            QmcUtils.printPlain(QmcUtils.termsToKonjunktion(terms, 4));
            System.out.println(terms);
        });
        if (!seg.reqIndices.isEmpty()) {
            System.out.println("Branching:");
            seg.bestBranches().forEach(branch -> {
                branch.reqTable.forEach(terms -> {
                    QmcUtils.printPlain(QmcUtils.termsToKonjunktion(terms, 4));
                    System.out.println(terms);
                });
                if (!branch.reqIndices.isEmpty()) {
                    throw new IllegalStateException("Still required: " + Arrays.toString(seg.reqIndices.toArray()));
                }
                System.out.println();
            });
        }
        System.out.println();
    }

    public static List<Integer> function(int segment) {
        List<Integer> f = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            if (table[segment][i] && !dontCares.contains(i)) {
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
                mintermsBinary.add(String.format("%04d", Integer.parseInt(QmcUtils.toBinary(n))));
            }
        }
        return mintermsBinary;
    }

    public static List<List<String>> allFunctions() {
        List<List<String>> functions = new ArrayList<>();
        for (int i = 0; i < 128; i++) {
            String binary = String.format("%07d", Integer.parseInt(QmcUtils.toBinary(i)));
            List<String> minterms = minterms(binary.charAt(0) == '1', binary.charAt(1) == '1', binary.charAt(2) == '1', binary.charAt(3) == '1', binary.charAt(4) == '1', binary.charAt(5) == '1', binary.charAt(6) == '1');
            functions.add(minterms);
        }
        return functions;
    }
}