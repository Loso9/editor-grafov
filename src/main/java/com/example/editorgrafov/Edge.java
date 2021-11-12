package com.example.editorgrafov;

public class Edge {

    private final String from;
    private final String to;

    public Edge(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        return this.getClass() == o.getClass() && from.equals(((Edge) o).from) && to.equals(((Edge) o).to);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((to == null) ? 0 : to.hashCode());
        return result;
    }
}
