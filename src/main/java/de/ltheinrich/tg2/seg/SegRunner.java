package de.ltheinrich.tg2.seg;

import java.util.List;
import java.util.Scanner;

public class SegRunner {
    public static void main(String[] args) {
        System.out.print("Enter 3 for Excess3-Code (default is standard binary); else ENTER: ");
        if (!new Scanner(System.in).nextLine().isBlank()) {
            SegTable.table = new boolean[][]{
                    new boolean[]{true, true, true /* dont care */, true, false, true, true, false, true, true, true, true, true, /* dont care */ true, true, true}, // A
                    new boolean[]{true, true, true /* dont care */, true, true, true, true, true, false, false, true, true, true, /* dont care */ true, true, true}, // B
                    new boolean[]{true, true, true /* dont care */, true, true, false, true, true, true, true, true, true, true, /* dont care */ true, true, true}, // C
                    new boolean[]{true, true, true /* dont care */, true, false, true, true, false, true, true, false, true, true, /* dont care */ true, true, true}, // D
                    new boolean[]{true, true, true /* dont care */, true, false, true, false, false, false, true, false, true, false, /* dont care */ true, true, true}, // E
                    new boolean[]{true, true, true /* dont care */, true, false, false, false, true, true, true, false, true, true, /* dont care */ true, true, true}, // F
                    new boolean[]{true, true, true /* dont care */, false, false, true, true, true, true, true, false, true, true, /* dont care */ true, true, true} // G
            };
            SegTable.dontCares = List.of(0, 1, 2, 13, 14, 15);
        }
        SegTable.printAll();
    }
}
