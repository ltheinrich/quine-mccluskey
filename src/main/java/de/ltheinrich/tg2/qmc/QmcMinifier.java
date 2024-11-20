package de.ltheinrich.tg2.qmc;

import org.w3c.dom.ls.LSOutput;

import java.util.*;

public class QmcMinifier {

    public List<List<Integer>> minifyTable;
    public List<List<Integer>> reqTable = new ArrayList<>();
    public List<Integer> reqIndices;

    public QmcMinifier(List<List<Integer>> minifyTable, List<Integer> reqIndices) {
        this(minifyTable, reqIndices, List.of());
    }

    public QmcMinifier(Collection<List<Integer>> minifyTable, Collection<Integer> reqIndices, Collection<Integer> dontCares) {
        this.reqIndices = new ArrayList<>(reqIndices);
        this.minifyTable = new ArrayList<>(minifyTable.stream().filter(terms -> terms.stream().allMatch(i -> dontCares.contains(i) || reqIndices.contains(i))).toList());
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
                setRequired(required);
                return extractRequired() + 1;
            }
        }
        return 0;
    }

    void setRequired(List<Integer> required) {
        reqTable.add(required);
        minifyTable.remove(required);
        required.forEach(reqIndices::remove);
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

                if (terms2.stream().filter(reqIndices::contains).count() < terms1.stream().filter(reqIndices::contains).count()) {
                    minifyTable.remove(terms2);
                    return rowDominance() + 1;
                }
            }
        }
        return 0;
    }

    public boolean minify() {
        int lastExtraction = Integer.MAX_VALUE;
        int lastRowDom = Integer.MAX_VALUE;
        extractRequired();
        while ((lastExtraction > 0 || lastRowDom > 0) && !minifyTable.isEmpty() && !reqIndices.isEmpty()) {
            lastRowDom = rowDominance();
            lastExtraction = extractRequired();
        }
        return minifyTable.isEmpty() && reqIndices.isEmpty();
    }

    void branch(int selection) {
        List<Integer> selected = minifyTable.get(selection);
        setRequired(selected);
        minify();
    }

    void branches(List<Integer> selections) {
        selections.stream().map(minifyTable::get).toList().forEach(this::setRequired);
        minify();
    }

    public QmcMinifier[] allBranches() {
        List<List<Integer>> branches = subBranches(0);
        QmcMinifier[] minis = new QmcMinifier[branches.size()];
        for (int i = 0; i < branches.size(); i++) {
            QmcMinifier mini = new QmcMinifier(List.of(), reqIndices);
            mini.minifyTable = new ArrayList<>(minifyTable);
            mini.reqTable.addAll(reqTable);
            mini.branches(branches.get(i));
            mini.minify();
            minis[i] = mini;
        }
        return minis;
    }

    List<List<Integer>> subBranches(int current) {
        if (current == minifyTable.size() - 1)
            return List.of(List.of(current));
        else if (current >= minifyTable.size()) {
            System.out.println(minifyTable.size() + " " + current);
        }

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

    public List<QmcMinifier> bestBranches() {
        QmcMinifier[] minis = allBranches();
        int min = Integer.MAX_VALUE;
        int maxCount = Integer.MIN_VALUE;
        for (QmcMinifier mini : minis) {
            if (!mini.reqIndices.isEmpty() || mini.reqTable.size() > min)
                continue;

            int currentCount = (int) mini.reqTable.stream().mapToLong(Collection::size).sum();
            if (mini.reqTable.size() < min) {
                min = mini.reqTable.size();
                maxCount = currentCount;
            } else if (currentCount > maxCount) {
                maxCount = currentCount;
            }
        }

        int finalMin = min;
        int finalMaxCount = maxCount;
        List<QmcMinifier> bestBranches = Arrays.stream(minis).filter(mini -> mini.reqIndices.isEmpty() && mini.reqTable.size() == finalMin && mini.reqTable.stream().mapToLong(Collection::size).sum() == finalMaxCount).toList();
        List<QmcMinifier> reducedBranches = new ArrayList<>(bestBranches.size());
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