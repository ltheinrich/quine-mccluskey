package de.ltheinrich.tg2.sw;

import java.util.Collection;

public abstract class Entry {

    public abstract int[] getArray();
    public abstract String[] getInputNames();
    public abstract String[] getOutputNames();

    protected static int charToValue(Object binary, int i) {
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

    public void addTo(Collection<Entry> entries) {
        entries.add(this);
    }

}
