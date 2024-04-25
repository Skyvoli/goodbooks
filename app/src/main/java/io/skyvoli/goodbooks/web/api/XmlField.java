package io.skyvoli.goodbooks.web.api;

import java.util.Objects;

public class XmlField {

    protected final String tag;
    protected final String code;

    protected XmlField(String tag, String code) {
        this.tag = tag;
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XmlField)) return false;
        XmlField xmlField = (XmlField) o;
        return Objects.equals(tag, xmlField.tag) && Objects.equals(code, xmlField.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, code);
    }
}