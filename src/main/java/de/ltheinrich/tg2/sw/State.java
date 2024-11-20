package de.ltheinrich.tg2.sw;

public enum State {
    FETCH("0000"),
    END("0001"),
    SWR("0010"),

    ALU1("0100"),
    ALU2("0101"),

    JMXA1("1000"),
    JMXA2("1001"),
    LDA("1010"),
    SVA("1011"),
    SET_PC("1100"),

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