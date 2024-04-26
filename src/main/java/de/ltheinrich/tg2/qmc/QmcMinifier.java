package de.ltheinrich.tg2.qmc;

import java.util.ArrayList;
import java.util.List;

public class QmcMinifier {

    List<List<Integer>> minifyTable;
    List<List<Integer>> reqTable = new ArrayList<>();
    List<Integer> reqIndices;

    QmcMinifier(List<List<Integer>> minifyTable, List<Integer> reqIndices) {
        this(minifyTable, reqIndices, Integer.MAX_VALUE);
    }

    QmcMinifier(List<List<Integer>> minifyTable, List<Integer> reqIndices, int dcStart) {
        this.reqIndices = new ArrayList<>(reqIndices);
        this.minifyTable = new ArrayList<>(minifyTable.stream().filter(terms -> terms.stream().allMatch(i -> i >= dcStart || reqIndices.contains(i))).toList());
        //System.out.println("Size: " + minifyTable.size() + " to " + this.minifyTable.size());
    }

    int extractRequired() {
        reqIndexLoop:
        for (int reqIndex : reqIndices) {
            List<Integer> required = null;
            for (List<Integer> terms : minifyTable) {
                if (terms.contains(reqIndex)) {
                    if (required != null) {
                        continue reqIndexLoop;
                    } else {
                        required = terms;
                    }
                }
            }

            if (required != null && !reqTable.contains(required)) {
                reqTable.add(required);
                minifyTable.remove(required);
                required.forEach(reqIndices::remove);
                return extractRequired() + 1;
            }
        }
        return 0;
    }

    int rowDominance() {
        for (List<Integer> terms1 : minifyTable) {
            terms2Loop:
            for (List<Integer> terms2 : minifyTable) {
                if (terms1.equals(terms2))
                    continue;

                for (int reqIndex : reqIndices) {
                    if (!terms1.contains(reqIndex) && terms2.contains(reqIndex)) {
                        continue terms2Loop;
                    }
                }
                minifyTable.remove(terms2);
                return rowDominance() + 1;
            }
        }
        return 0;
    }

    List<List<Integer>> minify() {
        int lastExtraction = Integer.MAX_VALUE;
        int lastRowDom = Integer.MAX_VALUE;
        extractRequired();
        while ((lastExtraction > 0 || lastRowDom > 0) && !minifyTable.isEmpty() && !reqIndices.isEmpty()) {
            lastRowDom = rowDominance();
            lastExtraction = extractRequired();
        }
        return reqTable;
    }
}