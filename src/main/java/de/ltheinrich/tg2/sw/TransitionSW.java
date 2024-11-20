package de.ltheinrich.tg2.sw;

import de.ltheinrich.tg2.qmc.QmcUtils;
import lombok.Getter;

import java.util.*;

public class TransitionSW extends Generator {

    public TransitionSW(int outputBit, int outputBitsStartIndex) {
        super(outputBit, outputBitsStartIndex);

        // Reset
        TransitionEntry.builder().R(1)
                .Q(State.ALL).Command(Command.ALL).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.FETCH).build().addTo(entries);

        // fetch + ALU -> ALU1
        TransitionEntry.builder().R(0)
                .Q(State.FETCH).Command(Command.ALU).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.ALU1) .build().addTo(entries);

        // fetch + JMX -> JMXA1
        TransitionEntry.builder().R(0)
                .Q(State.FETCH).Command(Command.JMX).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.JMXA1).build().addTo(entries);

        // fetch + LDA/SVA -> JMXA1
        TransitionEntry.builder().R(0)
                .Q(State.FETCH).Command(Command.LSA).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.JMXA1).build().addTo(entries);

        // fetch + SWR -> SWR
        TransitionEntry.builder().R(0)
                .Q(State.FETCH).Command(Command.SWR).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.SWR).build().addTo(entries);

        // fetch + END -> END
        TransitionEntry.builder().R(0)
                .Q(State.FETCH).Command(Command.END).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.END).build().addTo(entries);

        // END -> fetch
        TransitionEntry.builder().R(0)
                .Q(State.END).Command(Command.ALL).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.FETCH).build().addTo(entries);

        // SWR -> fetch
        TransitionEntry.builder().R(0)
                .Q(State.SWR).Command(Command.ALL).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.FETCH).build().addTo(entries);

        // ALU1 + !BLA -> fetch; TODO: Anpassung für Busy statt BLA notwendig!!!
        TransitionEntry.builder().R(0)
                .Q(State.ALU1).Command(Command.ALL).BLA(0)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.FETCH).build().addTo(entries);

        // ALU1 + BLA -> ALU2
        TransitionEntry.builder().R(0)
                .Q(State.ALU1).Command(Command.ALL).BLA(1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.ALU2).build().addTo(entries);

        // ALU2 Ausgaben
        TransitionEntry.builder().R(0)
                .Q(State.ALU2).Command(Command.ALL).BLA(0)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.ALL).build().addTo(entries);

        // ALU2 + !BLA -> fetch
        TransitionEntry.builder().R(0)
                .Q(State.ALU2).Command(Command.ALL).BLA(0)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.FETCH).build().addTo(entries);

        // ALU2 + BLA -> ALU2
        TransitionEntry.builder().R(0)
                .Q(State.ALU2).Command(Command.ALL).BLA(1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.ALU2).build().addTo(entries);

        // JMXA1 -> JMXA2
        TransitionEntry.builder().R(0)
                .Q(State.JMXA1).Command(Command.ALL).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.JMXA2).build().addTo(entries);

        // JMXA2 + JMP -> SET_PC
        TransitionEntry.builder().R(0)
                .Q(State.JMXA2).Command(Command.JMP).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.SET_PC).build().addTo(entries);

        // JMXA2 + JMPC + !C -> FETCH
        TransitionEntry.builder().R(0)
                .Q(State.JMXA2).Command(Command.JMPC).BLA(-1)
                .Carry(0).Overflow(-1).Zero(-1)
                .Qnext(State.FETCH).build().addTo(entries);

        // JMXA2 + JMPC + C -> SET_PC
        TransitionEntry.builder().R(0)
                .Q(State.JMXA2).Command(Command.JMPC).BLA(-1)
                .Carry(1).Overflow(-1).Zero(-1)
                .Qnext(State.SET_PC).build().addTo(entries);

        // JMXA2 + JMPO + !O -> FETCH
        TransitionEntry.builder().R(0)
                .Q(State.JMXA2).Command(Command.JMPO).BLA(-1)
                .Carry(-1).Overflow(0).Zero(-1)
                .Qnext(State.FETCH).build().addTo(entries);

        // JMXA2 + JMPO + O -> SET_PC
        TransitionEntry.builder().R(0)
                .Q(State.JMXA2).Command(Command.JMPO).BLA(-1)
                .Carry(-1).Overflow(1).Zero(-1)
                .Qnext(State.SET_PC).build().addTo(entries);

        // JMXA2 + JMPZ + !Z -> FETCH
        TransitionEntry.builder().R(0)
                .Q(State.JMXA2).Command(Command.JMPZ).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(0)
                .Qnext(State.FETCH).build().addTo(entries);

        // JMXA2 + JMPZ + Z -> SET_PC
        TransitionEntry.builder().R(0)
                .Q(State.JMXA2).Command(Command.JMPZ).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(1)
                .Qnext(State.SET_PC).build().addTo(entries);

        // JMXA2 + LDA -> LDA
        TransitionEntry.builder().R(0)
                .Q(State.JMXA2).Command(Command.LDA).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.LDA).build().addTo(entries);

        // JMXA2 + SVA -> SVA
        TransitionEntry.builder().R(0)
                .Q(State.JMXA2).Command(Command.SVA).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.SVA).build().addTo(entries);

        // LDA -> FETCH
        TransitionEntry.builder().R(0)
                .Q(State.LDA).Command(Command.ALL).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.FETCH).build().addTo(entries);

        // SVA -> FETCH
        TransitionEntry.builder().R(0)
                .Q(State.SVA).Command(Command.ALL).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.FETCH).build().addTo(entries);

        // SET_PC -> FETCH
        TransitionEntry.builder().R(0)
                .Q(State.SET_PC).Command(Command.ALL).BLA(-1)
                .Carry(-1).Overflow(-1).Zero(-1)
                .Qnext(State.FETCH).build().addTo(entries);

        generateMintermsAndZeros();
    }

}
