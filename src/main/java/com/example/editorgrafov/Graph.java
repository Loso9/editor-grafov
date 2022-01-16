package com.example.editorgrafov;

public interface Graph {
    int getNumberOfVertices();
    int getNumberOfEdges();
    boolean existsEdge();
    Iterable<String> outgoingEdgesDestinations(String vertex);
}
