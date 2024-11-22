package de.ltheinrich.tg2.sw.assembler;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AsmData {

    private final Integer data;
    private final AsmAdr adr;

    public AsmData(Integer data) {
        this.data = data;
        this.adr = new AsmAdr(null);
    }

}
