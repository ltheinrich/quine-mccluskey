package de.ltheinrich.tg2.sw;

public enum Command {
    ALU("0---"),
    AND("0000"),
    OR("0001"),
    NOT("0010"),
    ADD("0011"),
    SUB("0100"),
    MUL("0101"),
    RES1("0110"),
    RES2("0111"),

    JMP("1000"),
    JMPC("1001"),
    JMPO("1010"),
    JMPZ("1011"),
    LDA("1100"),
    SVA("1101"),
    SWR("1110"),
    END("1111"),

    ALL("----"),
    JMX("10--"),
    LSA("110-");

    private final String command;

    Command(final String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return command;
    }
}