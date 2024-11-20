package de.ltheinrich.tg2.sw;

import de.ltheinrich.tg2.qmc.FastQmc;
import de.ltheinrich.tg2.qmc.QmcMinifier;
import de.ltheinrich.tg2.qmc.QmcUtils;

import java.util.ArrayList;
import java.util.List;

public class SwRunner {

    public static void main(String[] args) {
        // OutputBit 13 -> Q3 (MSB)
        // OutputBit 14 -> Q2
        // OutputBit 15 -> Q1
        // OutputBit 16 -> Q0 (LSB)
        Steuerwerk sw = new Steuerwerk(13, 13);
        List<Integer> mintermsAndDontCares = new ArrayList<>(sw.getMinterms());
        mintermsAndDontCares.addAll(sw.getDontCares());
        //QuineMcCluskey qmc = new QuineMcCluskey(sw.getDontCares().stream().toList(), 13, mintermsAndDontCares.stream().mapToInt(i -> i).toArray());
        FastQmc qmc = new FastQmc(13, sw.getMinterms(), sw.getDontCares());
        QmcMinifier mini = qmc.runAndMinify(1000);
        System.out.println("Ergebnisse:");
        QmcUtils.printExtractedAndKonjunktion(mini, 13);

        if (!mini.reqIndices.isEmpty()) {
            System.out.println("Branching:");
            List<QmcMinifier> branches = mini.bestBranches();
            branches.forEach(branch -> QmcUtils.printExtractedAndKonjunktion(branch, 13));
        }
    }

}
