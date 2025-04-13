package io.skyvoli.goodbooks.web.api.dnb;


public class Resolved<T> {

    private T value;
    protected final XmlField with;


    protected Resolved(T resolved, XmlField with) {
        this.value = resolved;
        this.with = with;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}