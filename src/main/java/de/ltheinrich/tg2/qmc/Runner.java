package de.ltheinrich.tg2.qmc;

import de.ltheinrich.tg2.seg.SegTable;

import java.util.*;

public class Runner {
    public static void mainOld(String[] args) {
        var functions = SegTable.allFunctions();
        for (String f : functions.get(66)) {
            System.out.println(f);
        }

        var minterms = functions.get(66).toArray(String[]::new);
        for (String s : minterms) {
            //System.out.println(s);
        }

        var qmc = new QuineMcCluskey(minterms);

        for (int i = 0; i < 1000; i++) {
            System.out.println("Gruppe " + i + ":");
            qmc.generateNextGroup();
            QmcUtils.printGroup(qmc, i);
            System.out.println();

            if (qmc.groups.getLast().isEmpty()) break;
        }

        System.out.println("Ergebnis:");
        QmcUtils.printIndices(qmc);

        qmc.mintermIndices(qmc.unchecked());
    }

    public static void mainTest1(String[] args) {
        var qmc = new QuineMcCluskey("0000", "0010", "0011", "0111", "1000", "1001", "1100", "1110", "1111");
        QmcMinifier mini = qmc.runAndMinify(1000);

        System.out.println("Ergebnis:");
        QmcUtils.printResults(qmc);
        QmcUtils.printMinifyTable(mini);

        System.out.println("Extracted");
        QmcUtils.printExtracted(mini);

        // extracted: 8,9 - 0,2 - 12,14 - 3,7 - 7,15
    }

    public static void mainTest2(String[] args) {
        var qmc = new QuineMcCluskey("0000", "0001", "0100", "0101", "0110", "0111", "1000", "1001", "1011", "1111");
        QmcMinifier mini = qmc.runAndMinify(1000);

        System.out.println("Ergebnis:");
        QmcUtils.printResults(qmc);
        QmcUtils.printMinifyTable(mini);

        System.out.println("Extracted");
        QmcUtils.printExtracted(mini);

        // extracted: 11,15 - 0,1,8,9 - 4,5,6,7
    }

    public static void main(String[] args) {
        var start = System.currentTimeMillis();
        var minis = qmcForAll();
        Set<List<Integer>> allTerms = new HashSet<>();
        for (QmcMinifier mini : minis) {
            allTerms.addAll(mini.reqTable);
        }

        QmcUtils.printAllExtracted(minis);
        System.out.println("Reduzierte Primimplikanten:");
        allTerms.forEach(QmcUtils::print);
        System.out.println();

        QmcUtils.print7SegFinals(allTerms.stream().toList(), 'A');
        QmcUtils.print7SegFinals(allTerms.stream().toList(), 'B');
        QmcUtils.print7SegFinals(allTerms.stream().toList(), 'C');
        QmcUtils.print7SegFinals(allTerms.stream().toList(), 'D');
        QmcUtils.print7SegFinals(allTerms.stream().toList(), 'E');
        QmcUtils.print7SegFinals(allTerms.stream().toList(), 'F');
        QmcUtils.print7SegFinals(allTerms.stream().toList(), 'G');

        System.out.println("Dauer: " + (System.currentTimeMillis() - start) + " ms");
    }

    static QmcMinifier[] qmcForAll() {
        QmcMinifier[] minis = new QmcMinifier[128];
        var functions = SegTable.allFunctions();
        for (int i = 0; i < minis.length; i++) {
            var qmc = new QuineMcCluskey(10, functions.get(i).toArray(String[]::new));
            QmcMinifier mini = qmc.runAndMinify(1000);
            if (!mini.reqIndices.isEmpty()) throw new IllegalStateException(qmc.reqIndices.toString());
            minis[i] = mini;
        }
        return minis;
    }
}