package de.ltheinrich.tg2.qmc;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public class FastQmc {

    public static void main(String[] args) {
        FastQmc qmc = new FastQmc(4, List.of(0, 4, 6, 11, 12), List.of(13, 14));
        QmcMinifier mini = qmc.runAndMinify(1000);
        System.out.println(qmc.unchecked);
    }

    private final int bits;
    private final Collection<Integer> reqIndices;
    private final Collection<Integer> dontCares;

    private final Lock lock = new ReentrantLock();
    // Groups g G, G'; Group group G0, G1; Minterms-List 1, 2; Value-List 0 0 1
    private final List<List<Set<List<Integer>>>> groups = new ArrayList<>();
    // Minterms not included in any other
    private final Set<List<Integer>> unchecked = new HashSet<>();

    public FastQmc(int bits, Collection<Integer> reqIndices, Collection<Integer> dontCares) {
        this.bits = bits;
        this.reqIndices = reqIndices;
        this.dontCares = dontCares;
        List<Set<List<Integer>>> g0 = createNewEmptyGroup();

        Stream.concat(reqIndices.stream(), dontCares.stream()).forEach(minterm -> {
            List<Integer> mintermBinary = QmcUtils.toBinaryPad(minterm, bits).chars().map(c -> c - 48).boxed().toList();
            int ones = (int) mintermBinary.stream().filter(b -> b == 1).count();
            g0.get(ones).add(mintermBinary);
            unchecked.add(mintermBinary);
        });
    }

    private int[][] generateBinaries(int n) {
        int size = (int) Math.pow(2, n);
        int[][] binaries = new int[size][];
        for (int i = 0; i < size; i++) {
            int[] binary = new int[n];
            String binaryString = QmcUtils.toBinaryPad(i, n);
            for (int j = 0; j < n; j++) {
                binary[j] = binaryString.charAt(j) - 48;
            }
            binaries[i] = binary;
        }
        return binaries;
    }

    private Set<List<Integer>> mintermIndices(Collection<List<Integer>> minterms) {
        var binaries = generateBinaries(minterms.iterator().next().size());
        Set<List<Integer>> mintermIndices = new HashSet<>();
        for (List<Integer> minterm : minterms) {
            List<Integer> indices = new ArrayList<>();
            binaryLoop:
            for (int b = 0; b < binaries.length; b++) {
                for (int i = 0; i < binaries[b].length; i++) {
                    if (minterm.get(i) != -1 && minterm.get(i) != binaries[b][i]) {
                        continue binaryLoop;
                    }
                }
                indices.add(b);
            }

            if (!indices.isEmpty() && !new HashSet<>(dontCares).containsAll(indices))
                mintermIndices.add(indices);
        }
        return mintermIndices;
    }

    public QmcMinifier runAndMinify(int limit) {
        long start = System.currentTimeMillis();
        run(limit);
        System.out.println("QmcTime1: " + (System.currentTimeMillis() - start) / 1000.0 + "s");
        QmcMinifier mini = new QmcMinifier(mintermIndices(unchecked), reqIndices, dontCares);
        mini.minify();
        System.out.println("QmcTime2: " + (System.currentTimeMillis() - start) / 1000.0 + "s");
        return mini;
    }

    public void run(int limit) {
        groups.subList(1, groups.size()).clear();
        for (int i = 0; i < limit; i++) {
            System.out.println("Iteration " + i);
            this.generateNextGroup();
            if (groups.getLast().stream().allMatch(Set::isEmpty))
                return;
        }
        throw new StackOverflowError("run limit exceeded");
    }

    private List<Set<List<Integer>>> createNewEmptyGroup() {
        List<Set<List<Integer>>> g = new ArrayList<>();
        for (int i = 0; i <= bits - groups.size(); i++) {
            g.add(new HashSet<>());
        }
        groups.add(g);
        return g;
    }

    private void generateNextGroup() {
        List<Set<List<Integer>>> prevG = groups.getLast();
        List<Set<List<Integer>>> nextG = createNewEmptyGroup();

        for (int i = 0; i < prevG.size() - 1; i++) {
            final int firstGroup = i;
            prevG.get(firstGroup).parallelStream().forEach(first ->
                    prevG.get(firstGroup + 1).parallelStream().forEach(second -> {
                                List<Integer> combined = combine(first, second);
                                if (combined != null) {
                                    lock.lock();
                                    nextG.get(firstGroup).add(combined);
                                    unchecked.add(combined);
                                    unchecked.remove(first);
                                    unchecked.remove(second);
                                    lock.unlock();
                                }
                            }
                    )
            );
        }
    }

    private List<Integer> combine(List<Integer> a, List<Integer> b) {
        int dontCare = -1;
        for (int i = 0; i < a.size(); i++) {
            // nur vergleichen, wenn don't care an gleicher Stelle
            if ((a.get(i) == -1) ^ (b.get(i) == -1))
                return null;

            // don't care setzen, wenn verschieden
            if (a.get(i) != b.get(i)) {
                if (dontCare != -1) return null;
                dontCare = i;
            }
        }

        List<Integer> c = new ArrayList<>(a);
        c.set(dontCare, -1);
        return c;
    }

}
