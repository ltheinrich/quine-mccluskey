package de.ltheinrich.tg2.sw;

public enum State {
    FETCH("0000"),
    SET_PC("0010"),

    ALU1("0100"),
    ALU2("0101"),

    JMXA1("1000"),
    JMXA2("1001"),

    LDA("1100"),
    SVA("1101"),

    SWR("1110"),
    END("1111"),

    ALL("----");

    private final String state;

    State(final String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return state;
    }
}