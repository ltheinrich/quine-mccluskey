package de.ltheinrich.tg2.sw;

import de.ltheinrich.tg2.qmc.FastQmc;
import de.ltheinrich.tg2.qmc.QmcMinifier;
import de.ltheinrich.tg2.qmc.QmcUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class SwRunner {

    private static final int DIFF = 11;
    private static final boolean OUTPUT_MODE = true;
    private static final int INPUT_SIZE = OUTPUT_MODE ? 4 : 13;

    public static void main(String[] args) {
        // OutputBit 13 + 0 -> Q3 (MSB)
        // OutputBit 13 + 1 -> Q2
        // OutputBit 13 + 2 -> Q1
        // OutputBit 13 + 3-> Q0 (LSB)
        Generator gen;
        if (OUTPUT_MODE) {
            gen = new OutputSW(INPUT_SIZE + DIFF, INPUT_SIZE);
        } else {
            gen = new TransitionSW(INPUT_SIZE + DIFF, INPUT_SIZE);
        }
        FastQmc qmc = new FastQmc(INPUT_SIZE, gen.getMinterms(), gen.getDontCares());
        QmcMinifier mini = qmc.runAndMinify(1000);
        System.out.println("Ergebnisse:");
        printNamed(mini);
        //QmcUtils.printExtractedAndKonjunktion(mini, 13);

        if (!mini.reqIndices.isEmpty()) {
            System.out.println("First Branch:");
            List<QmcMinifier> branches = mini.bestBranches();
            SwRunner.printNamed(branches.getFirst());
            //branches.forEach(SwRunner::printNamed);
        }
    }

    private static void printNamed(QmcMinifier mini) {
        mini.reqTable.forEach(terms -> {
            List<Integer> konjunktion = QmcUtils.termsToKonjunktion(terms, INPUT_SIZE);
            String stringified = IntStream.range(0, INPUT_SIZE)
                    .filter(index -> konjunktion.get(index) >= 0)
                    .mapToObj(index -> inputIndexToName(konjunktion, index))
                    .reduce((acc, name) -> acc + " " + name).orElseThrow();
            if (stringified.length() - stringified.replace(" ", "").length() == 5) stringified = "1 " + stringified;
            if (stringified.length() - stringified.replace(" ", "").length() == 6) stringified = "1 " + stringified;
            System.out.println(stringified);
        });
        System.out.println("Still required: " + Arrays.toString(mini.reqIndices.toArray()));
    }

    private static String inputIndexToName(List<Integer> terms, int index) {
        String name = switch (OUTPUT_MODE ? index + 1 : index) {
            case 0:
                yield "R";
            case 1:
                yield "Q3";
            case 2:
                yield "Q2";
            case 3:
                yield "Q1";
            case 4:
                yield "Q0";
            case 5:
                yield "C3";
            case 6:
                yield "C2";
            case 7:
                yield "C1";
            case 8:
                yield "C0";
            case 9:
                yield "BLA";
            case 10:
                yield "Carry";
            case 11:
                yield "Overflow";
            case 12:
                yield "Zero";
            default:
                throw new IllegalArgumentException("invalid input index");
        };
        return terms.get(index) == 1 ? name : "!" + name;
    }

}
