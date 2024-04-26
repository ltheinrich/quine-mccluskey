package de.ltheinrich.tg2.qmc;

import de.ltheinrich.tg2.seg.SegTable;

import java.util.*;

public class QuineMcCluskey {

    // Zeile x Spalte
    Set<List<Integer>> table;
    List<Set<List<Integer>>> groups = new ArrayList<>();
    Set<List<Integer>> checked = new HashSet<>();

    int dcStart;
    List<Integer> reqIndices;

    QuineMcCluskey(String... rawTable) {
        this(Integer.MAX_VALUE, rawTable);
    }

    QuineMcCluskey(int dcStart, String... rawTable) {
        this.dcStart = dcStart;
        table = new HashSet<>(rawTable.length);
        reqIndices = new ArrayList<>(rawTable.length);
        for (String s : rawTable) {
            int d = QmcUtils.toDecimal(s);
            if (d < dcStart)
                reqIndices.add(d);
            List<Integer> z = new ArrayList<>(s.length());
            s.chars().map(c -> c < 48 ? -1 : c - 48).forEach(z::add);
            table.add(z);
        }
    }

    void generateNextGroup() {
        if (groups.isEmpty()) {
            groups.add(table);
            return;
        }

        groups.add(new HashSet<>());
        groups.get(groups.size() - 2).forEach(a -> {
            groups.get(groups.size() - 2).forEach(b -> {
                if (!a.equals(b)) {
                    var c = combine(a, b);
                    if (c != null) {
                        groups.getLast().add(c);
                        checked.add(a);
                        checked.add(b);
                    }
                }
            });
        });
    }

    int singleDiffIndex(List<Integer> a, List<Integer> b) {
        int index = -1;
        for (int i = 0; i < a.size(); i++) {
            if (!a.get(i).equals(b.get(i))) {
                if (index != -1 || a.get(i) == -1 || b.get(i) == -1) return -1;
                index = i;
            }
        }
        return index;
    }

    List<Integer> combine(List<Integer> a, List<Integer> b) {
        int i = singleDiffIndex(a, b);
        if (i == -1) return null;
        List<Integer> c = new ArrayList<>(List.copyOf(a));
        c.set(i, -1);
        return c;
    }

    Set<List<Integer>> unchecked() {
        Set<List<Integer>> unchecked = new HashSet<>();
        groups.forEach(group -> {
            unchecked.addAll(group.stream().filter(a -> !checked.contains(a)).toList());
        });
        return unchecked;
    }

    int[][] generateBinaries(int n) {
        int size = (int) Math.pow(2, n);
        int[][] binaries = new int[size][];
        for (int i = 0; i < size; i++) {
            int[] binary = new int[n];
            String binaryString = String.format("%" + (n < 10 ? "0" + n : n) + "d", Integer.parseInt(SegTable.toBinary(i)));
            for (int j = 0; j < n; j++) {
                binary[j] = binaryString.charAt(j) - 48;
            }
            binaries[i] = binary;
        }
        return binaries;
    }

    Set<List<Integer>> mintermIndices(Set<List<Integer>> minterms) {
        var binaries = generateBinaries(minterms.iterator().next().size());
        Set<List<Integer>> mintermIndices = new HashSet<>();
        for (List<Integer> minterm : minterms) {
            List<Integer> indices = new ArrayList<>();
            binaryLoop:
            for (int b = 0; b < binaries.length; b++) {
                if (indices.isEmpty() && b >= dcStart)
                    continue;

                for (int i = 0; i < binaries[b].length; i++) {
                    if (minterm.get(i) != -1 && minterm.get(i) != binaries[b][i]) {
                        continue binaryLoop;
                    }
                }
                indices.add(b);
            }
            if (!indices.isEmpty())
                mintermIndices.add(indices);
        }
        return mintermIndices;
    }

    void runUntilEmptyOr(int limit) {
        for (int i = 0; i < limit; i++) {
            this.generateNextGroup();
            if (groups.getLast().isEmpty())
                return;
        }
        throw new StackOverflowError("runUntilEmptyOr limit exceeded");
    }

    List<List<Integer>> generateMinifyTable() {
        if (!groups.getLast().isEmpty())
            throw new IllegalStateException("last group not empty");
        return new ArrayList<>(mintermIndices(unchecked()));
    }

    QmcMinifier runAndMinify(int limit) {
        runUntilEmptyOr(limit);
        QmcMinifier mini = new QmcMinifier(generateMinifyTable(), reqIndices, dcStart);
        mini.minify();
        return mini;
    }
}