package com.alexm.ex.BetServer.httpserver;

import java.util.Collections;
import java.util.Map;

public class BetHttpResponse<T> {
    private final int statusCode;
    private final T entity;
    private final Map<String, String> headers;


    private BetHttpResponse(BetHttpResponseBuilder<T> builder) {
        this.statusCode = builder.statusCode;
        this.entity = builder.entity;
        this.headers = builder.headers;
    }

    public static BetHttpResponseBuilder<Void> anBetHttpResponse(int statusCode) {

        return new BetHttpResponseBuilder<>(statusCode);
    }


    public static <T> BetHttpResponseBuilder<T> anBetHttpResponse(int statusCode, T entity) {
        return new BetHttpResponseBuilder<T>(statusCode).entity(entity);
    }

    public int getStatusCode() {
        return statusCode;
    }


    public Map<String, String> getHeaders() {
        return headers == null ? Collections.<String, String>emptyMap() : headers;
    }

    public T getEntity() {
        return entity;
    }

    public static final class BetHttpResponseBuilder<T> {

        private final int statusCode;
        private T entity;
        private Map<String, String> headers;

        private BetHttpResponseBuilder(int statusCode) {
            this.statusCode = statusCode;
        }

        public BetHttpResponseBuilder<T> entity(T entity) {
            this.entity = entity;
            return this;
        }

        public BetHttpResponse<T> build() {
            return new BetHttpResponse<T>(this);
        }
    }
}
