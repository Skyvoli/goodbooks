package io.skyvoli.goodbooks.helper;

public class DimensionCalculator {

    private DimensionCalculator() {
        throw new IllegalStateException("Utility class");
    }

    public static String getRatio(int width, int height) {
        float result;
        if (height != 0) {
            result = (float) width / height;
        } else {
            result = 2.3f; // Handle divide by zero case
        }
        return String.valueOf(result);
    }
}