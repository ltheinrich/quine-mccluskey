package de.ltheinrich.tg2.qmc;

import java.util.*;

public class QmcRunner {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Bits: ");
        int bits = in.nextInt();
        in.nextLine();
        System.out.print("Minterme: ");
        List<Integer> minterms = new ArrayList<>(Arrays.stream(in.nextLine().split(",")).filter(line -> !line.isBlank()).map(String::trim).map(Integer::parseInt).toList());
        System.out.print("Don't Cares: ");
        List<Integer> dontCares = Arrays.stream(in.nextLine().split(",")).filter(line -> !line.isBlank()).map(String::trim).map(Integer::parseInt).toList();
        //minterms.addAll(dontCares);
        //QuineMcCluskey qmc = new QuineMcCluskey(dontCares, bits, minterms.stream().mapToInt(i -> i).toArray());
        FastQmc qmc = new FastQmc(bits, minterms, dontCares);
        QmcMinifier mini = qmc.runAndMinify(1000);
        System.out.println("Ergebnisse:");
        QmcUtils.printExtractedAndKonjunktion(mini, bits);

        if (!mini.reqIndices.isEmpty()) {
            System.out.println("Branching:");
            List<QmcMinifier> branches = mini.bestBranches();
            branches.forEach(branch -> QmcUtils.printExtractedAndKonjunktion(branch, bits));
        }
    }
}