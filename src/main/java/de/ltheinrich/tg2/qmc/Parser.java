package de.ltheinrich.tg2.qmc;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Parser {

    public final List<String> namesInput = new ArrayList<>();
    public final List<String> namesOutput = new ArrayList<>();

    public final Set<Integer>[] zeros;
    public final Set<Integer>[] ones;
    public final Set<Integer>[] dontCares;

    @SneakyThrows
    public Parser(String file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            var header = splitLine(br.readLine());
            namesInput.addAll(header.getKey());
            namesOutput.addAll(header.getValue());
            zeros = new Set[namesOutput.size()];
            ones = new Set[namesOutput.size()];
            dontCares = new Set[namesOutput.size()];

            for (int output = 0; output < namesOutput.size(); output++) {
                ones[output] = new HashSet<>();
                zeros[output] = new HashSet<>();
                dontCares[output] = new HashSet<>();
            }

            var line = splitLine(br.readLine());
            while (line != null) {
                for (int output = 0; output < namesOutput.size(); output++) {
                    for (int n = 0; n < 1 << namesInput.size(); n++) {
                        if (checkMatch(n, line.getKey())) {
                            checkConflictAndAdd(n, output, line.getValue().get(output));
                        }
                    }
                }
                line = splitLine(br.readLine());
            }
        }

        checkComplete();
    }

    public boolean checkMatch(int n, List<String> input) {
        String binary = String.format("%" + namesInput.size() + "s", Integer.toBinaryString(n)).replace(" ", "0");
        for (int j = 0; j < namesInput.size(); j++) {
            if (binary.charAt(j) != input.get(j).charAt(0) && !input.get(j).equals("x")) {
                return false;
            }
        }
        return true;
    }

    public void checkConflictAndAdd(int n, int output, String outputValue) {
        switch (outputValue) {
            case "0" -> {
                if (ones[output].contains(n)) {
                    throw new IllegalStateException("conflict: 0, but found 1");
                } else if (dontCares[output].contains(n)) {
                    throw new IllegalStateException("conflict: 0, but found x");
                }
                zeros[output].add(n);
            }
            case "1" -> {
                if (zeros[output].contains(n)) {
                    throw new IllegalStateException("conflict: 1, but found 0");
                } else if (dontCares[output].contains(n)) {
                    throw new IllegalStateException("conflict: 1, but found x");
                }
                ones[output].add(n);
            }
            case "x" -> {
                if (zeros[output].contains(n)) {
                    throw new IllegalStateException("conflict: x, but found 0");
                } else if (ones[output].contains(n)) {
                    throw new IllegalStateException("conflict: x, but found 1");
                }
                dontCares[output].add(n);
            }
            default -> throw new IllegalArgumentException("unknown input character");
        }
    }

    public void checkComplete() {
        for (int output = 0; output < namesOutput.size(); output++) {
            for (int n = 0; n < 1 << namesInput.size(); n++) {
                if (!zeros[output].contains(n) && !ones[output].contains(n) && !dontCares[output].contains(n)) {
                    throw new IllegalStateException("incomplete: missing " + n);
                }
            }
        }
    }

    public Map.Entry<List<String>, List<String>> splitLine(String line) {
        if (line == null) {
            return null;
        }
        String[] split = removeDoubleSpace(line).replace(" | ", "|").split("\\|");
        return Map.entry(Arrays.stream(split[0].split(" ")).toList(), Arrays.stream(split[1].split(" ")).toList());
    }

    public String removeDoubleSpace(String line) {
        if (line.contains("  ")) {
            return removeDoubleSpace(line.replace("  ", " "));
        } else {
            return line;
        }
    }

}
