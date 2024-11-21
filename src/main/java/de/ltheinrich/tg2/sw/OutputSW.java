package de.ltheinrich.tg2.sw;

public class OutputSW extends Generator {

    public OutputSW(int outputBit, int outputBitsStartIndex) {
        super(outputBit, outputBitsStartIndex);

        // fetch
        OutputEntry.builder()
                .Q(State.FETCH)
                .WriteA(0).WriteRAM(0).WriteADR1(0).WriteADR2(0).WriteOP(1)
                .PC_LD(0).PC_EN(1).PC_UP(1).RegADRtoRAM(0).RegOPtoC(0).AluStart(0).SWR(0)
                .build().addTo(entries);

        // END
        OutputEntry.builder()
                .Q(State.END)
                .WriteA(0).WriteRAM(0).WriteADR1(0).WriteADR2(0).WriteOP(-1)
                .PC_LD(0).PC_EN(1).PC_UP(0).RegADRtoRAM(0).RegOPtoC(-1).AluStart(0).SWR(0)
                .build().addTo(entries);

        // SWR
        OutputEntry.builder()
                .Q(State.SWR)
                .WriteA(0).WriteRAM(0).WriteADR1(0).WriteADR2(0).WriteOP(-1)
                .PC_LD(0).PC_EN(0).PC_UP(-1).RegADRtoRAM(0).RegOPtoC(-1).AluStart(0).SWR(1)
                .build().addTo(entries);

        // ALU1
        OutputEntry.builder()
                .Q(State.ALU1)
                .WriteA(0).WriteRAM(0).WriteADR1(0).WriteADR2(0).WriteOP(-1)
                .PC_LD(0).PC_EN(0).PC_UP(-1).RegADRtoRAM(0).RegOPtoC(-1).AluStart(1).SWR(0)
                .build().addTo(entries);

        // ALU2
        OutputEntry.builder()
                .Q(State.ALU2)
                .WriteA(0).WriteRAM(0).WriteADR1(0).WriteADR2(0).WriteOP(-1)
                .PC_LD(0).PC_EN(0).PC_UP(-1).RegADRtoRAM(0).RegOPtoC(-1).AluStart(0).SWR(0)
                .build().addTo(entries);
        
        // JMXA1
        OutputEntry.builder()
                .Q(State.JMXA1)
                .WriteA(0).WriteRAM(0).WriteADR1(1).WriteADR2(0).WriteOP(0)
                .PC_LD(0).PC_EN(1).PC_UP(1).RegADRtoRAM(0).RegOPtoC(1).AluStart(0).SWR(0)
                .build().addTo(entries);

        // JMXA2
        OutputEntry.builder()
                .Q(State.JMXA2)
                .WriteA(0).WriteRAM(0).WriteADR1(0).WriteADR2(1).WriteOP(0)
                .PC_LD(0).PC_EN(1).PC_UP(1).RegADRtoRAM(0).RegOPtoC(1).AluStart(0).SWR(0)
                .build().addTo(entries);

        // LDA
        OutputEntry.builder()
                .Q(State.LDA)
                .WriteA(1).WriteRAM(0).WriteADR1(0).WriteADR2(0).WriteOP(-1)
                .PC_LD(0).PC_EN(0).PC_UP(-1).RegADRtoRAM(1).RegOPtoC(-1).AluStart(0).SWR(0)
                .build().addTo(entries);

        // SVA
        OutputEntry.builder()
                .Q(State.SVA)
                .WriteA(0).WriteRAM(1).WriteADR1(0).WriteADR2(0).WriteOP(-1)
                .PC_LD(0).PC_EN(0).PC_UP(-1).RegADRtoRAM(1).RegOPtoC(-1).AluStart(0).SWR(0)
                .build().addTo(entries);

        // SET_PC
        OutputEntry.builder()
                .Q(State.SET_PC)
                .WriteA(0).WriteRAM(0).WriteADR1(0).WriteADR2(0).WriteOP(-1)
                .PC_LD(1).PC_EN(0).PC_UP(-1).RegADRtoRAM(0).RegOPtoC(-1).AluStart(0).SWR(0)
                .build().addTo(entries);

        generateMintermsAndZeros();
    }

}
