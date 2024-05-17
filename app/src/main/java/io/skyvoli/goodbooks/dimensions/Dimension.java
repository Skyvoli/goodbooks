package io.skyvoli.goodbooks.dimensions;

public class Dimension {

    private final int width;
    private final int height;

    public Dimension(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException();
        }
        this.width = width;
        this.height = height;
    }

    public String getRatio() {
        float result;
        if (height != 0) {
            result = (float) width / height;
        } else {
            result = 2.3f; // Handle divide by zero case
        }
        return String.valueOf(result);
    }
}