package de.ltheinrich.tg2.sw;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
@Builder
public class OutputEntry extends Entry {

    @NonNull
    private final State Q;

    @NonNull
    private final Integer WriteA;
    @NonNull
    private final Integer WriteRAM;
    @NonNull
    private final Integer WriteADR1;
    @NonNull
    private final Integer WriteADR2;
    @NonNull
    private final Integer WriteOP; // wenn aktiviert, wird RAM als Command verwendet, sonst OpReg
    @NonNull
    private final Integer PC_LD; // Ausgabe ist jedoch für !PC_LD
    @NonNull
    private final Integer PC_EN; // Ausgabe ist jedoch für !PC_EN
    @NonNull
    private final Integer PC_UP;
    @NonNull
    private final Integer RegADRtoRAM;
    @NonNull
    private final Integer AluStart;
    @NonNull
    private final Integer SWR;

    private int[] arr;
    @Getter
    private final String[] inputNames = INPUT_NAMES;
    @Getter
    private static final String[] INPUT_NAMES = new String[]{
            "Q3", "Q2", "Q1", "Q0",
    };
    @Getter
    private final String[] outputNames = OUTPUT_NAMES;
    @Getter
    private static final String[] OUTPUT_NAMES = new String[]{
            "WriteA", "WriteRAM", "WriteADR1", "WriteADR2", "WriteOP",
            "!PC_LD", "!PC_EN", "PC_UP",
            "RegADRtoRAM", "AluStart", "SWR",
    };

    // WICHTIG! Veränderungen hier auch in INPUT_NAMES und OUTPUT_NAMES berücksichtigen!
    public int[] getArray() {
        if (arr == null) {
            if (Q.toString().length() != 4) throw new IllegalStateException();
            arr = new int[]{
                    charToValue(Q, 0), charToValue(Q, 1), charToValue(Q, 2), charToValue(Q, 3),
                    WriteA, WriteRAM, WriteADR1, WriteADR2, WriteOP,
                    invert(PC_LD), invert(PC_EN), PC_UP,
                    RegADRtoRAM, AluStart, SWR
            };
        }
        return arr;
    }

    private static int invert(int bit) {
        if (bit == 0) return 1;
        else if (bit == 1) return 0;
        else return bit;
    }

}
