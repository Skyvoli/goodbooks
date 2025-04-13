package io.skyvoli.goodbooks.helper;

import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MissingNumberDetector {

    private MissingNumberDetector() {
        throw new IllegalStateException("Utility class");
    }

    public static List<Integer> findMissingNumbers(List<Integer> existing, int max) {
        BitSet allBitSet = new BitSet(max);
        BitSet presentBitSet = new BitSet(max);

        IntStream.rangeClosed(1, max)
                .forEach(allBitSet::set);

        existing.forEach(presentBitSet::set);
        allBitSet.and(presentBitSet);

        return IntStream.rangeClosed(1, max - 1)
                .filter(i -> !allBitSet.get(i))
                .boxed()
                .sorted()
                .collect(Collectors.toList());
    }
}