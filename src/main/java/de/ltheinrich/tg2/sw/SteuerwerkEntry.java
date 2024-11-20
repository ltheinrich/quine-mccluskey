package de.ltheinrich.tg2.sw;

import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;

import java.util.Collection;

@ToString
@Builder
public class SteuerwerkEntry {

    @NonNull
    private final Integer R;
    @NonNull
    private final State Q;
    @NonNull
    private final Command Command;
    @NonNull
    private final Integer BLA;
    @NonNull
    private final Integer Carry;
    @NonNull
    private final Integer Overflow;
    @NonNull
    private final Integer Zero;

    @NonNull
    private final State Qnext;

    private int[] arr;

    // WICHTIG! Veränderungen hier auch in SwRunner#inputIndexToName berücksichtigen!
    public int[] getArray() {
        if (arr == null) {
            if (Q.toString().length() != 4 || Command.toString().length() != 4 || Qnext.toString().length() != 4) throw new IllegalStateException();
            arr = new int[]{
                    R,
                    charToValue(Q, 0), charToValue(Q, 1), charToValue(Q, 2), charToValue(Q, 3),
                    charToValue(Command, 0), charToValue(Command, 1), charToValue(Command, 2), charToValue(Command, 3),
                    BLA,
                    Carry, Overflow, Zero,
                    charToValue(Qnext, 0), charToValue(Qnext, 1), charToValue(Qnext, 2), charToValue(Qnext, 3),
            };
        }
        return arr;
    }

    private static int charToValue(Object binary, int i) {
        char c = binary.toString().charAt(i);
        return c == '-' ? -1 : c - 48;
    }

    public int get(int i) {
        return getArray()[i];
    }

    public OutputType getType(int outputBit, int[] inputValues) {
        for (int i = 0; i < inputValues.length; i++) {
            if (get(i) != inputValues[i] && get(i) != -1)
                return OutputType.UNKNOWN;
        }
        return switch (get(outputBit)) {
            case 0 -> OutputType.ZERO;
            case 1 -> OutputType.ONE;
            default -> OutputType.UNKNOWN;
        };
    }

    public enum OutputType {
        ZERO,
        ONE,
        UNKNOWN;
    }

    public void addTo(Collection<SteuerwerkEntry> entries) {
        entries.add(this);
    }

}
