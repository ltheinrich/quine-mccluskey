package de.ltheinrich.tg2.sw;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.Collection;

@ToString
@Builder
public class TransitionEntry extends Entry {

    @NonNull
    private final Integer R;
    @NonNull
    private final State Q;
    @NonNull
    private final Command Command;
    @NonNull
    private final Integer BLA;
    @NonNull
    private final Integer Busy;
    @NonNull
    private final Integer Carry;
    @NonNull
    private final Integer Overflow;
    @NonNull
    private final Integer Zero;

    @NonNull
    private final State Qnext;

    private int[] arr;
    @Getter
    private final String[] inputNames = INPUT_NAMES;
    @Getter
    private final static String[] INPUT_NAMES = new String[]{
            "R",
            "Q3", "Q2", "Q1", "Q0",
            "C3", "C2", "C1", "C0",
            "BLA", "Busy",
            "Carry", "Overflow", "Zero"
    };
    @Getter
    private final String[] outputNames = OUTPUT_NAMES;
    @Getter
    private final static String[] OUTPUT_NAMES = new String[]{
            "D3", "D2", "D1", "D0",
    };

    // WICHTIG! Veränderungen hier auch in SwRunner#inputIndexToName berücksichtigen!
    public int[] getArray() {
        if (arr == null) {
            if (Q.toString().length() != 4 || Command.toString().length() != 4 || Qnext.toString().length() != 4)
                throw new IllegalStateException();
            arr = new int[]{
                    R,
                    charToValue(Q, 0), charToValue(Q, 1), charToValue(Q, 2), charToValue(Q, 3),
                    charToValue(Command, 0), charToValue(Command, 1), charToValue(Command, 2), charToValue(Command, 3),
                    BLA, Busy,
                    Carry, Overflow, Zero,
                    charToValue(Qnext, 0), charToValue(Qnext, 1), charToValue(Qnext, 2), charToValue(Qnext, 3),
            };
        }
        return arr;
    }

}
