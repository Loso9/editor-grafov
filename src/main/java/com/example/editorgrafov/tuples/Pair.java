package com.example.editorgrafov;

import java.util.Objects;

public class Pair<P, Q> {
    private P first;
    private Q second;

    public Pair(P first, Q second) {
        this.first = first;
        this.second = second;
    }

    public P getFirst() {
        return first;
    }

    public Q getSecond() {
        return second;
    }

    public void setFirst(P first) {
        this.first = first;
    }

    public void setSecond(Q second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
