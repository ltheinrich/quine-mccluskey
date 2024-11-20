package de.ltheinrich.tg2.sw;

import de.ltheinrich.tg2.qmc.QmcUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Generator {

    private final int outputBit;
    private final int outputBitsStartIndex;

    protected final List<Entry> entries = new ArrayList<>();

    @Getter
    private final Set<Integer> minterms = new LinkedHashSet<>();
    // outputBit is zero (not minterm), but not don't care
    private final Set<Integer> zeros = new LinkedHashSet<>();
    @Getter
    private final Set<Integer> dontCares = new LinkedHashSet<>();

    protected Generator(int outputBit, int outputBitsStartIndex) {
        this.outputBit = outputBit;
        this.outputBitsStartIndex = outputBitsStartIndex;
    }

    protected void generateMintermsAndZeros() {
        int bound = (int) Math.pow(2, outputBitsStartIndex);
        for (int minterm = 0; minterm < bound; minterm++) {
            int[] binary = QmcUtils.toBinaryPad(minterm, outputBitsStartIndex).chars().limit(outputBitsStartIndex).map(c -> c - 48).toArray();
            for (Entry entry : entries) {
                Entry.OutputType type = entry.getType(outputBit, binary);
                boolean alreadyMinterm = minterms.contains(minterm);
                boolean alreadyZero = zeros.contains(minterm);
                switch (type) {
                    case ONE -> {
                        if (alreadyZero) throw new IllegalStateException();
                        minterms.add(minterm);
                        dontCares.remove(minterm);
                    }
                    case ZERO -> {
                        if (alreadyMinterm) throw new IllegalStateException();
                        zeros.add(minterm);
                        dontCares.remove(minterm);
                    }
                    case UNKNOWN -> {
                        if (!alreadyMinterm && !alreadyZero) dontCares.add(minterm);
                    }
                }
            }
        }
    }

}
