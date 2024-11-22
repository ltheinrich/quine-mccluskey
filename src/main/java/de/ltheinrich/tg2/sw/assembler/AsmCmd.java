package de.ltheinrich.tg2.sw.assembler;

import de.ltheinrich.tg2.sw.Command;
import lombok.Data;

@Data
public class AsmCmd {
    private final Command cmd;
    private final AsmAdr adr;
}

