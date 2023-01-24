package com.syedee.graphqlbackend.controllers.graphql;

import java.util.List;

class Connection<T> {
    private final List<Edge<T>> edges;
    private final PageInfo pageInfo;

    protected Connection(final List<Edge<T>> edges, final PageInfo pageInfo) {
        this.edges = edges;
        this.pageInfo = pageInfo;
    }

    public List<Edge<T>> getEdges() {
        return edges;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }
}

class Edge<T> {
    final String cursor;
    final T node;

    public Edge(final String cursor, final T node) {
        this.cursor = cursor;
        this.node = node;
    }

    public String getCursor() {
        return cursor;
    }

    public T getNode() {
        return node;
    }
}

class PageInfo {
    private final boolean hasNextPage;
    private final boolean hasPreviousPage;
    private final String startCursor;
    private final String endCursor;

    public PageInfo(final boolean hasNextPage, final boolean hasPreviousPage, final String startCursor, final String endCursor) {
        this.hasPreviousPage = hasPreviousPage;
        this.hasNextPage = hasNextPage;
        this.startCursor = startCursor;
        this.endCursor = endCursor;
    }

    public static PageInfo of(final boolean hasNextPage) {
        return new PageInfo(hasNextPage, false, null, null);
    }

    public static PageInfo of(
            final boolean hasNextPage,
            final String startCursor,
            final String endCursor
    ) {
        return new PageInfo(hasNextPage, false, startCursor, endCursor);
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public String getStartCursor() {
        return startCursor;
    }

    public String getEndCursor() {
        return endCursor;
    }
}


abstract class MutationResponse {
    private final Boolean successful;
    private final List<String> errorMessages;

    public MutationResponse(Boolean successful, List<String> errorMessages) {
        this.successful = successful;
        this.errorMessages = errorMessages;
    }

    public Boolean getSuccessful() {
        return successful;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}

