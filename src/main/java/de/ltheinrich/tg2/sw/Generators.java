package de.ltheinrich.tg2.sw;

import de.ltheinrich.tg2.qmc.FastQmc;
import de.ltheinrich.tg2.qmc.QmcMinifier;
import de.ltheinrich.tg2.qmc.QmcUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Generators {

    public static void main(String[] args) {
        Generators generators = new Generators(false);
        generators.printAll();
    }

    private final List<Generator> generators = new ArrayList<>();
    private FastQmc[] qmcs;
    private QmcMinifier[] minis;
    private List<QmcMinifier>[] branches;

    public Generators(boolean skipTransition) {
        if (!skipTransition) {
            IntStream.range(0, TransitionEntry.getOUTPUT_NAMES().length)
                    .mapToObj(i -> new TransitionSW(i, TransitionEntry.getINPUT_NAMES().length)).
                    forEach(generators::add);
        }

        IntStream.range(0, OutputEntry.getOUTPUT_NAMES().length)
                .mapToObj(i -> new OutputSW(i, OutputEntry.getINPUT_NAMES().length)).
                forEach(generators::add);
    }

    public void generate() {
        qmcs = new FastQmc[generators.size()];
        minis = new QmcMinifier[generators.size()];
        branches = new List[generators.size()];

        IntStream.range(0, generators.size()).forEach(i -> {
            System.out.println("Generating " + generators.get(i).getOutputName());
            FastQmc qmc = new FastQmc(generators.size(), generators.get(i).getMinterms(), generators.get(i).getDontCares());
            QmcMinifier mini = qmc.runAndMinify(1000);
            List<QmcMinifier> branches = null;
            if (!mini.reqIndices.isEmpty()) {
                System.out.println("Branching...");
                branches = mini.bestBranches();
            }

            synchronized (this) {
                qmcs[i] = qmc;
                minis[i] = mini;
                this.branches[i] = branches;
            }
        });
    }

    public void printAll() {
        if (qmcs == null) generate();

        for (int i = 0; i < generators.size(); i++) {
            System.out.println("Ergebnisse: " + generators.get(i).getOutputName());
            if (minis[i].reqIndices.isEmpty()) {
                printNamed(generators.get(i), minis[i]);
                //QmcUtils.printExtractedAndKonjunktion(mini, 13);
            } else {
                //System.out.println("First Branch for " + generators.get(i).getOutputName() + ":");
                printNamed(generators.get(i), branches[i].getFirst());
                //branches.forEach(branch -> printNamed(branch, inputNames));
            }
            System.out.println("----------------------------------");
        }
    }

    private void printNamed(Generator generator, QmcMinifier mini) {
        mini.reqTable.forEach(terms -> {
            List<Integer> konjunktion = QmcUtils.termsToKonjunktion(terms, generator.getInputBitsLength());
            String stringified = IntStream.range(0, generator.getInputBitsLength())
                    .filter(index -> konjunktion.get(index) >= 0)
                    .mapToObj(index -> inputIndexToName(konjunktion, index, generator.getInputNames()))
                    .reduce((acc, name) -> acc + " " + name).orElseThrow();
            if (stringified.length() - stringified.replace(" ", "").length() == 5) stringified = "1 " + stringified;
            if (stringified.length() - stringified.replace(" ", "").length() == 6) stringified = "1 " + stringified;
            System.out.println(stringified);
        });
        System.out.println("Still required: " + Arrays.toString(mini.reqIndices.toArray()));
    }

    private String inputIndexToName(List<Integer> terms, int index, String[] inputNames) {
        String name = inputNames[index];
        return terms.get(index) == 1 ? name : "!" + name;
    }

}
