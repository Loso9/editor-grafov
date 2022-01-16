package com.example.editorgrafov.tuples;

import java.util.Objects;

public class Triplet<P, Q, R> {
    private P first;
    private Q second;
    private R third;

    public Triplet(P first, Q second, R third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public P getFirst() {
        return first;
    }

    public Q getSecond() {
        return second;
    }

    public R getThird() {
        return third;
    }

    public void setFirst(P first) {
        this.first = first;
    }

    public void setSecond(Q second) {
        this.second = second;
    }

    public void setThird(R third) {
        this.third = third;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triplet<?, ?, ?> triplet = (Triplet<?, ?, ?>) o;
        return Objects.equals(first, triplet.first) && Objects.equals(second, triplet.second) && Objects.equals(third, triplet.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }
}
