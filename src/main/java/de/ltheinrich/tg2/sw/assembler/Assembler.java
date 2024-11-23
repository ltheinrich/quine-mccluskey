package de.ltheinrich.tg2.sw.assembler;

import de.ltheinrich.tg2.sw.Command;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Assembler {

    private final List<AsmCmd> commands = new ArrayList<>();
    private final List<AsmData> data = new ArrayList<>();

    public static void main(String[] args) {
        // n^2 - m
        Assembler assembler = new Assembler();
        var one = new AsmData(1);
        var n = new AsmData(4);
        var m = new AsmData(6);
        var msb = new AsmAdr();
        var lsb = new AsmAdr();
        var subCarryFromMsb = new AsmAdr();
        var jmpLoop = new AsmAdr();
        assembler
                .LDA(n)
                .SWR()
                .LDA(n)
                .MUL()
                .SVA(msb)
                .LDA(m)
                .SWR()
                .SUB()
                .SVA(lsb)
                .JMPC(subCarryFromMsb)
                .END()
                .LDA(subCarryFromMsb, one)
                .SWR()
                .LDA(msb)
                .SUB()
                .SVA(msb)
                .JMPC(jmpLoop, jmpLoop)
                .END()
                .assembleAndPrint();
    }

    public static void main1(String[] args) {
        Assembler assembler = new Assembler();
        var a = new AsmData(4);
        var b = new AsmData(5);
        var c = new AsmData(3);
        var d = new AsmAdr(null);
        assembler
                .LDA(a)
                .SWR()
                .LDA(b)
                .ADD()
                .SWR()
                .LDA(c)
                .SWR()
                .SUB()
                .SVA(d)
                .END()
                .assembleAndPrint();
    }

    public Assembler() {
    }

    public Assembler AND() {
        addCmd(Command.AND);
        return this;
    }

    public Assembler AND(AsmAdr ownAdr) {
        addCmd(ownAdr, Command.AND);
        return this;
    }

    public Assembler OR() {
        addCmd(Command.OR);
        return this;
    }

    public Assembler OR(AsmAdr ownAdr) {
        addCmd(ownAdr, Command.OR);
        return this;
    }

    public Assembler NOT() {
        addCmd(Command.NOT);
        return this;
    }

    public Assembler NOT(AsmAdr ownAdr) {
        addCmd(ownAdr, Command.NOT);
        return this;
    }

    public Assembler ADD() {
        addCmd(Command.ADD);
        return this;
    }

    public Assembler ADD(AsmAdr ownAdr) {
        addCmd(ownAdr, Command.ADD);
        return this;
    }

    public Assembler SUB() {
        addCmd(Command.SUB);
        return this;
    }

    public Assembler SUB(AsmAdr ownAdr) {
        addCmd(ownAdr, Command.SUB);
        return this;
    }

    public Assembler MUL() {
        addCmd(Command.MUL);
        return this;
    }

    public Assembler MUL(AsmAdr ownAdr) {
        addCmd(ownAdr, Command.MUL);
        return this;
    }

    public Assembler RES1() {
        addCmd(Command.RES1);
        return this;
    }

    public Assembler RES1(AsmAdr ownAdr) {
        addCmd(ownAdr, Command.RES1);
        return this;
    }

    public Assembler RES2() {
        addCmd(Command.RES2);
        return this;
    }

    public Assembler RES2(AsmAdr ownAdr) {
        addCmd(ownAdr, Command.RES2);
        return this;
    }

    public Assembler JMP(AsmAdr adr) {
        addCmd(Command.JMP, adr);
        return this;
    }

    public Assembler JMP(AsmAdr ownAdr, AsmAdr adr) {
        addCmd(ownAdr, Command.JMP, adr);
        return this;
    }

    public Assembler JMPC(AsmAdr adr) {
        addCmd(Command.JMPC, adr);
        return this;
    }

    public Assembler JMPC(AsmAdr ownAdr, AsmAdr adr) {
        addCmd(ownAdr, Command.JMPC, adr);
        return this;
    }

    public Assembler JMPO(AsmAdr adr) {
        addCmd(Command.JMPO, adr);
        return this;
    }

    public Assembler JMPO(AsmAdr ownAdr, AsmAdr adr) {
        addCmd(ownAdr, Command.JMPO, adr);
        return this;
    }

    public Assembler JMPZ(AsmAdr adr) {
        addCmd(Command.JMPZ, adr);
        return this;
    }

    public Assembler JMPZ(AsmAdr ownAdr, AsmAdr adr) {
        addCmd(ownAdr, Command.JMPZ, adr);
        return this;
    }

    public Assembler LDA(AsmAdr adr) {
        addCmd(Command.LDA, adr);
        return this;
    }

    public Assembler LDA(AsmAdr ownAdr, AsmAdr adr) {
        addCmd(ownAdr, Command.LDA, adr);
        return this;
    }

    public Assembler LDA(AsmData data) {
        WriteData(data);
        addCmd(Command.LDA, data.getAdr());
        return this;
    }

    public Assembler LDA(AsmAdr ownAdr, AsmData data) {
        WriteData(data);
        addCmd(ownAdr, Command.LDA, data.getAdr());
        return this;
    }

    public Assembler SVA(AsmAdr adr) {
        addCmd(Command.SVA, adr);
        return this;
    }

    public Assembler SVA(AsmAdr ownAdr, AsmAdr adr) {
        addCmd(ownAdr, Command.SVA, adr);
        return this;
    }

    public Assembler SVA(AsmData data) {
        WriteData(data);
        addCmd(Command.SVA, data.getAdr());
        return this;
    }

    public Assembler SVA(AsmAdr ownAdr, AsmData data) {
        WriteData(data);
        addCmd(ownAdr, Command.SVA, data.getAdr());
        return this;
    }

    public Assembler SWR() {
        addCmd(Command.SWR);
        return this;
    }

    public Assembler SWR(AsmAdr ownAdr) {
        addCmd(ownAdr, Command.SWR);
        return this;
    }

    public Assembler END() {
        addCmd(Command.END);
        return this;
    }

    public Assembler END(AsmAdr ownAdr) {
        addCmd(ownAdr, Command.END);
        return this;
    }

    public Assembler WriteData(AsmData d) {
        if ((d.getData() & 15) != d.getData()) throw new IllegalArgumentException();
        if (!this.data.contains(d))
            this.data.add(d);
        return this;
    }

    private void addCmd(Command cmd) {
        commands.add(new AsmCmd(new AsmAdr(), cmd, new AsmAdr()));
    }

    private void addCmd(Command cmd, AsmAdr adr) {
        commands.add(new AsmCmd(new AsmAdr(), cmd, adr));
    }

    private void addCmd(AsmAdr ownAdr, Command cmd) {
        commands.add(new AsmCmd(ownAdr, cmd, new AsmAdr()));
    }

    private void addCmd(AsmAdr ownAdr, Command cmd, AsmAdr adr) {
        commands.add(new AsmCmd(ownAdr, cmd, adr));
    }

    public void assembleAndPrint() {
        int nextAdr = 0;
        int size = commands.stream()
                .mapToInt(cmd -> isAdrCmd(cmd) ? 3 : 1)
                .sum();
        int dataIndex = 0;
        for (AsmCmd cmd : commands) {
            cmd.getOwnAdr().setAdr(nextAdr);
            nextAdr += 1;
            if (isAdrCmd(cmd)) {
                nextAdr += 2;

                if (commands.subList(0, commands.indexOf(cmd)).stream().noneMatch(otherCmd -> otherCmd.getAdr() == cmd.getAdr()) && !cmd.getCmd().toString().startsWith("10")) {
                    int decAdr = cmd.getAdr().getAdr() == null ? size + dataIndex : cmd.getAdr().getAdr();
                    cmd.getAdr().setAdr(decAdr);
                    if ((decAdr & 255) != decAdr) throw new IllegalArgumentException();
                    dataIndex += 1;
                }
            }
        }
        for (AsmCmd cmd : commands) {
            System.out.println(Integer.toHexString(Integer.parseInt(cmd.getCmd().toString(), 2)));
            if (isAdrCmd(cmd)) {
                System.out.println(Integer.toHexString(cmd.getAdr().getAdr() / 16));
                System.out.println(Integer.toHexString(cmd.getAdr().getAdr() % 16));
            }
        }
        data.sort(Comparator.comparing(d -> d.getAdr().getAdr()));
        for (AsmData data : data) {
            while (nextAdr++ != data.getAdr().getAdr()) {
                System.out.println("X");
            }
            System.out.println(Integer.toHexString(data.getData()));
        }
    }

    private boolean isAdrCmd(AsmCmd cmd) {
        return switch (cmd.getCmd()) {
            case JMP, JMPC, JMPO, JMPZ, LDA, SVA -> true;
            default -> false;
        };
    }

}
