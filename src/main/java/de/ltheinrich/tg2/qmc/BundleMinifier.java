package de.ltheinrich.tg2.qmc;

import java.util.*;

public class BundleMinifier {

    List<List<Integer>> groups;
    List<List<Integer>> minterms;
    List<Integer> dontCares;

    List<List<Boolean>> table = new ArrayList<>();
    List<List<Integer>> refCol;
    public List<Map.Entry<Integer, Integer>> refRow = new ArrayList<>(); // Group, Row
    public List<List<Integer>> reqTable = new ArrayList<>();
    int rows;

    public BundleMinifier(List<List<Integer>> minterms, List<List<Integer>> groups, List<Integer> dontCares, List<List<Boolean>> table, List<List<Integer>> refCol, List<Map.Entry<Integer, Integer>> refRow, List<List<Integer>> reqTable, int rows) {
        this.minterms = minterms;
        this.groups = groups;
        this.dontCares = dontCares;
        this.table = new ArrayList<>(table.stream().map(ArrayList::new).toList());
        this.refCol = new ArrayList<>(refCol.stream().map(ArrayList::new).toList());
        this.refRow = new ArrayList<>(refRow);
        this.reqTable = new ArrayList<>(reqTable.stream().map(ArrayList::new).toList());
        this.rows = rows;
    }

    public BundleMinifier(List<List<Integer>> minterms, List<List<Integer>> groups, List<Integer> dontCares) {
        rows = groups.stream().map(List::size).reduce(0, Integer::sum);
        for (List<Integer> minterm : minterms) {
            List<Boolean> column = new ArrayList<>(rows);
            for (List<Integer> group : groups) {
                if (!minterm.stream().filter(m -> !dontCares.contains(m)).allMatch(group::contains)) {
                    group.forEach(m -> column.add(false));
                    continue;
                }
                for (int m : group) {
                    column.add(minterm.contains(m));
                }
            }
            table.add(column);
        }
        for (int g = 0; g < groups.size(); g++) {
            for (int m = 0; m < groups.get(g).size(); m++) {
                refRow.add(Map.entry(g, m));
            }
        }
        this.refCol = new ArrayList<>(minterms);
        this.groups = groups;
        this.minterms = minterms;
        this.dontCares = dontCares;
    }

    public int extractRequired() {
        rowLoop:
        for (int row = 0; row < rows; row++) {
            int col = -1;
            for (int column = 0; column < table.size(); column++) {
                var c = table.get(column);
                if (c.get(row)) {
                    if (col != -1) {
                        continue rowLoop;
                    }
                    col = column;
                }
            }
            setRequired(col);
            return extractRequired() + 1;
        }
        return 0;
    }

    int colDominance() {
        for (int col1 = 0; col1 < table.size(); col1++) {
            for (int col2 = col1 + 1; col2 < table.size(); col2++) {
                if (isFirstDominantColumn(col1, col2)) {
                    removeColumn(col2);
                    return colDominance() + 1;
                } else if (isFirstDominantColumn(col2, col1)) {
                    removeColumn(col1);
                    return colDominance() + 1;
                }
            }
        }
        return 0;
    }

    boolean isFirstDominantColumn(int col1, int col2) {
        for (int row = 0; row < table.get(col1).size(); row++) {
            if (!table.get(col1).get(row) && table.get(col2).get(row)) {
                return false;
            }
        }
        return !table.get(col1).equals(table.get(col2));
    }

    public void setRequired(int column) {
        List<Integer> required = refCol.get(column);
        reqTable.add(required);
        for (int i = table.get(column).size() - 1; i >= 0; i--) {
            if (table.get(column).get(i)) {
                for (List<Boolean> col : table) {
                    col.remove(i);
                }
                rows -= 1;
                refRow.remove(i);
            }
        }
        removeColumn(column);
    }

    void removeColumn(int col) {
        table.remove(col);
        refCol.remove(col);
    }

    /*void removeRow(int row) {
        table.forEach(column -> column.remove(row));
        refRow.remove(row);
    }*/

    public boolean minify() {
        int lastExtraction = Integer.MAX_VALUE;
        int lastColDom = Integer.MAX_VALUE;
        extractRequired();
        while ((lastExtraction > 0 || lastColDom > 0) && !table.isEmpty()) {
            lastColDom = colDominance();
            lastExtraction = extractRequired();
        }
        return table.isEmpty();
    }

    void branches(List<Integer> selectedColumns) {
        selectedColumns.stream().sorted().toList().reversed().forEach(this::setRequired);
        minify();
    }

    public BundleMinifier[] allBranches() {
        List<List<Integer>> branches = subBranches(0);
        BundleMinifier[] bms = new BundleMinifier[branches.size()];
        for (int i = 0; i < branches.size(); i++) {
            BundleMinifier bm = new BundleMinifier(minterms, groups, dontCares, table, refCol, refRow, reqTable, rows);
            bm.branches(branches.get(i));
            bm.minify();
            bms[i] = bm;
        }
        return bms;
    }

    List<List<Integer>> subBranches(int current) {
        if (current == table.size() - 1)
            return List.of(List.of(current));

        List<List<Integer>> subBranches = subBranches(current + 1);
        List<List<Integer>> branches = new ArrayList<>(subBranches);
        for (List<Integer> subBranch : subBranches) {
            List<Integer> branch = new ArrayList<>(subBranch.size() + 1);
            branch.addAll(subBranch);
            branch.add(current);
            branches.add(branch);
        }
        branches.add(List.of(current));

        return branches;
    }

    public List<BundleMinifier> bestBranches() {
        BundleMinifier[] bms = allBranches();
        int min = Integer.MAX_VALUE;
        int minCount = Integer.MAX_VALUE;
        for (BundleMinifier bm : bms) {
            if (!bm.table.isEmpty() || bm.reqTable.size() > min)
                continue;

            int currentCount = (int) bm.reqTable.stream().mapToLong(Collection::size).sum();
            if (bm.reqTable.size() < min) {
                min = bm.reqTable.size();
                minCount = currentCount;
            } else if (currentCount < minCount) {
                minCount = currentCount;
            }
        }

        int finalMin = min;
        int finalMinCount = minCount;
        List<BundleMinifier> bestBranches = Arrays.stream(bms).filter(bm -> bm.table.isEmpty() && bm.reqTable.size() == finalMin && bm.reqTable.stream().mapToLong(Collection::size).sum() == finalMinCount).toList();
        List<BundleMinifier> reducedBranches = new ArrayList<>(bestBranches.size());
        outerLoop:
        for (int i = 0; i < bestBranches.size(); i++) {
            for (int j = i + 1; j < bestBranches.size(); j++) {
                if (new HashSet<>(bestBranches.get(i).reqTable).containsAll(bestBranches.get(j).reqTable)) {
                    continue outerLoop;
                }
            }
            reducedBranches.add(bestBranches.get(i));
        }
        return reducedBranches;
    }
}
