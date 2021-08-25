package com.ming.blog.cache;

/**
 * @author unknown
 */
public class ValueScorePair<V> {
    private V value;
    private Double score;

    public ValueScorePair(V value, Double score) {
        this.value = value;
        this.score = score;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}

