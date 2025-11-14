package de.ltheinrich.tg2.qmc;

import java.util.ArrayList;
import java.util.List;

public class QmcRunner2 {
    public static void main(String[] args) {
        String file = "dram.qmc";
        Parser parser = new Parser(file);
        int bits = parser.namesInput.size();

        for (int output = 0; output < parser.namesOutput.size(); output++) {
            List<Integer> minterms = new ArrayList<>(parser.ones[output]);
            List<Integer> dontCares = new ArrayList<>(parser.dontCares[output]);

            FastQmc qmc = new FastQmc(bits, minterms, dontCares);
            QmcMinifier mini = qmc.runAndMinify(1000);
            System.out.println();
            System.out.println("Ergebnisse %s:".formatted(parser.namesOutput.get(output)));
            QmcUtils.printExtractedAndKonjunktion(mini, bits, parser.namesInput);

            if (!mini.reqIndices.isEmpty()) {
                System.out.println("Branching:");
                List<QmcMinifier> branches = mini.bestBranches();
                branches.forEach(branch -> QmcUtils.printExtractedAndKonjunktion(branch, bits, parser.namesInput));
            }
            System.out.println();
        }
    }
}