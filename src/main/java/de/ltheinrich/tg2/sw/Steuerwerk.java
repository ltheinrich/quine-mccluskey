package de.ltheinrich.tg2.sw;

import de.ltheinrich.tg2.qmc.QmcUtils;
import lombok.Getter;
import lombok.ToString;

import java.util.*;

public class Steuerwerk {

    private final int outputBit;
    private final int outputBitsStartIndex;

    private final List<SteuerwerkEntry> entries = new ArrayList<>();

    @Getter
    private final Set<Integer> minterms = new LinkedHashSet<>();
    // outputBit is zero (not minterm), but not don't care
    private final Set<Integer> zeros = new LinkedHashSet<>();
    @Getter
    private final Set<Integer> dontCares = new LinkedHashSet<>();

    public Steuerwerk(int outputBit, int outputBitsStartIndex) {
        this.outputBit = outputBit;
        this.outputBitsStartIndex = outputBitsStartIndex;

        // Reset
        SteuerwerkEntry.builder().R(1)
                .Q(State.ALL).Command(Command.ALL).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.FETCH).build().addTo(entries);

        // fetch + ALU -> ALU1
        SteuerwerkEntry.builder().R(0)
                .Q(State.FETCH).Command(Command.ALU).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.ALU1).build().addTo(entries);

        // fetch + JMX -> JMXA1
        SteuerwerkEntry.builder().R(0)
                .Q(State.FETCH).Command(Command.JMX).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.JMXA1).build().addTo(entries);

        // fetch + LDA/SVA -> JMXA1
        SteuerwerkEntry.builder().R(0)
                .Q(State.FETCH).Command(Command.LSA).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.JMXA1).build().addTo(entries);

        // fetch + SWR -> SWR
        SteuerwerkEntry.builder().R(0)
                .Q(State.FETCH).Command(Command.SWR).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.SWR).build().addTo(entries);

        // fetch + END -> END
        SteuerwerkEntry.builder().R(0)
                .Q(State.FETCH).Command(Command.END).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.END).build().addTo(entries);

        // END -> fetch
        SteuerwerkEntry.builder().R(0)
                .Q(State.END).Command(Command.ALL).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.FETCH).build().addTo(entries);

        // SWR -> fetch
        SteuerwerkEntry.builder().R(0)
                .Q(State.SWR).Command(Command.ALL).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.FETCH).build().addTo(entries);

        // ALU1 + !BLA -> fetch; TODO: Anpassung für Busy statt BLA notwendig!!!
        SteuerwerkEntry.builder().R(0)
                .Q(State.ALU1).Command(Command.ALL).BLA(0)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.FETCH).build().addTo(entries);

        // ALU1 + BLA -> ALU2
        SteuerwerkEntry.builder().R(0)
                .Q(State.ALU1).Command(Command.ALL).BLA(1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.ALU2).build().addTo(entries);

        // ALU2 + !BLA -> fetch
        SteuerwerkEntry.builder().R(0)
                .Q(State.ALU2).Command(Command.ALL).BLA(0)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.FETCH).build().addTo(entries);

        // ALU2 + BLA -> ALU2
        SteuerwerkEntry.builder().R(0)
                .Q(State.ALU2).Command(Command.ALL).BLA(1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.ALU2).build().addTo(entries);

        // JMXA1 -> JMXA2
        SteuerwerkEntry.builder().R(0)
                .Q(State.JMXA1).Command(Command.ALL).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.JMXA2).build().addTo(entries);

        // JMXA2 + JMP -> SET_PC
        SteuerwerkEntry.builder().R(0)
                .Q(State.JMXA2).Command(Command.JMP).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.SET_PC).build().addTo(entries);

        // JMXA2 + JMPC + !C -> FETCH
        SteuerwerkEntry.builder().R(0)
                .Q(State.JMXA2).Command(Command.JMPC).BLA(-1)
                .Carry(0).Overflow(-1).Zero(-1)
                .Qnext(State.FETCH).build().addTo(entries);

        // JMXA2 + JMPC + C -> SET_PC
        SteuerwerkEntry.builder().R(0)
                .Q(State.JMXA2).Command(Command.JMPC).BLA(-1)
                .Carry(1).Overflow(-1).Zero(-1)
                .Qnext(State.SET_PC).build().addTo(entries);

        // JMXA2 + JMPO + !O -> FETCH
        SteuerwerkEntry.builder().R(0)
                .Q(State.JMXA2).Command(Command.JMPO).BLA(-1)
                .Carry(-1).Overflow(0).Zero(-1)
                .Qnext(State.FETCH).build().addTo(entries);

        // JMXA2 + JMPO + O -> SET_PC
        SteuerwerkEntry.builder().R(0)
                .Q(State.JMXA2).Command(Command.JMPO).BLA(-1)
                .Carry(-1).Overflow(1).Zero(-1)
                .Qnext(State.SET_PC).build().addTo(entries);

        // JMXA2 + JMPZ + !Z -> FETCH
        SteuerwerkEntry.builder().R(0)
                .Q(State.JMXA2).Command(Command.JMPZ).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(0)
                .Qnext(State.FETCH).build().addTo(entries);

        // JMXA2 + JMPZ + Z -> SET_PC
        SteuerwerkEntry.builder().R(0)
                .Q(State.JMXA2).Command(Command.JMPZ).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(1)
                .Qnext(State.SET_PC).build().addTo(entries);

        // JMXA2 + LDA -> LDA
        SteuerwerkEntry.builder().R(0)
                .Q(State.JMXA2).Command(Command.LDA).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.LDA).build().addTo(entries);

        // JMXA2 + SVA -> SVA
        SteuerwerkEntry.builder().R(0)
                .Q(State.JMXA2).Command(Command.SVA).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.SVA).build().addTo(entries);

        // LDA -> FETCH
        SteuerwerkEntry.builder().R(0)
                .Q(State.LDA).Command(Command.ALL).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.FETCH).build().addTo(entries);

        // SVA -> FETCH
        SteuerwerkEntry.builder().R(0)
                .Q(State.SVA).Command(Command.ALL).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.FETCH).build().addTo(entries);

        generateMintermsAndZeros();
    }

    private void generateMintermsAndZeros() {
        int bound = (int) Math.pow(2, outputBitsStartIndex);
        for (int minterm = 0; minterm < bound; minterm++) {
            int[] binary = QmcUtils.toBinaryPad(minterm, outputBitsStartIndex).chars().limit(outputBitsStartIndex).map(c -> c - 48).toArray();
            for (SteuerwerkEntry entry : entries) {
                SteuerwerkEntry.OutputType type = entry.getType(outputBit, binary);
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
