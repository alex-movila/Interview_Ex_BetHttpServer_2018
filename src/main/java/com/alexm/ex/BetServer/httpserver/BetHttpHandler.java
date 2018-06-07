package com.alexm.ex.BetServer.httpserver;

import com.alexm.ex.BetServer.core.BetAPI;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BetHttpHandler implements HttpHandler {

    private static final Pattern GET_SESSION_PATTERN = Pattern.compile("/([^/]+)/session");
    private static final Pattern GET_HIGH_STAKES_PATTERN = Pattern.compile("/([^/]+)/highstakes");
    private static final Pattern POST_STAKE_PATTERN = Pattern.compile("/([^/]+)/stake");
    private static final String SESSION_KEY_QUERY_PARAM = "sessionkey";

    private static String getQueryParam(HttpExchange httpExchange, String queryParam) {
        String query = httpExchange.getRequestURI().getQuery();
        if (query != null) {
            for (String part : query.split("&")) {
                String[] keyValue = part.split("=");
                if (keyValue.length > 1 && keyValue[0].equals(queryParam)) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }

    private void handleAppsGET(HttpExchange httpExchange) throws IOException {
        BetHttpResponse<?> httpResponse = null;
        String path = httpExchange.getRequestURI().getPath();

        Matcher matcher;
        if ((matcher = GET_SESSION_PATTERN.matcher(path)).matches()) {
            String customerId = matcher.group(1);
            String sessionId = BetAPI.getNewSessionKey(customerId);

            httpResponse = BetHttpResponse.anBetHttpResponse(HttpServletResponse.OK, sessionId).build();
        } else if ((matcher = GET_HIGH_STAKES_PATTERN.matcher(path)).matches()) {
            String betOfferId = matcher.group(1);
            String highStakes = BetAPI.getBetOfferTopHighestStakeList(betOfferId);

            httpResponse = BetHttpResponse.anBetHttpResponse(HttpServletResponse.OK, highStakes).build();
        } else {
            httpExchange.sendResponseHeaders(HttpServletResponse.SC_NOT_FOUND, 0);
            return;
        }
        if (httpResponse == null) {
            httpResponse = BetHttpResponse.anBetHttpResponse(HttpServletResponse.SC_NOT_FOUND).build();
        }
        mapResponse(httpExchange, httpResponse);
    }

    private void handleAppsPost(HttpExchange httpExchange) throws IOException {
        BetHttpResponse<?> httpResponse;
        String path = httpExchange.getRequestURI().getPath();

        Matcher matcher;
        if ((matcher = POST_STAKE_PATTERN.matcher(path)).matches()) {
            String sessionId = getQueryParam(httpExchange, SESSION_KEY_QUERY_PARAM);
            String betOfferId = matcher.group(1);
            String stake = readRequestBody(httpExchange);

            if (!BetAPI.createCustomerStakeForBetOffer(sessionId, stake, betOfferId)) {
                httpExchange.sendResponseHeaders(HttpServletResponse.SC_NOT_FOUND, 0);
                return;
            }
            httpResponse = BetHttpResponse.anBetHttpResponse(HttpServletResponse.OK, sessionId).build();
        } else {
            httpExchange.sendResponseHeaders(HttpServletResponse.SC_NOT_FOUND, 0);
            return;
        }

        mapResponse(httpExchange, httpResponse);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        try {
            String method = httpExchange.getRequestMethod();
            if ("GET".equals(method)) {
                handleAppsGET(httpExchange);
            } else if ("POST".equals(method)) {
                handleAppsPost(httpExchange);
            } else {
                httpExchange.sendResponseHeaders(HttpServletResponse.SC_NOT_FOUND, 0);
            }

        } catch (Exception e) {
            System.out.println("HttpServer error: \n" + e.getMessage());
            httpExchange.sendResponseHeaders(500, 0);
        } finally {
            httpExchange.close();
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        String requestBody;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            requestBody = br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
        return requestBody;
    }


    private <T> void mapResponse(HttpExchange httpExchange, BetHttpResponse<T> response) throws IOException {
        // Add headers
        for (Map.Entry<String, String> headerEntry : response.getHeaders().entrySet()) {
            httpExchange.getResponseHeaders().add(headerEntry.getKey(), headerEntry.getValue());
        }

        if (response.getStatusCode() / 100 != 2) {
            httpExchange.sendResponseHeaders(response.getStatusCode(), 0);
            return;
        }

        // Prepare body, if any
        T entity = response.getEntity();
        byte[] body = null;
        if (entity != null) {
            body = entity.toString().getBytes();
        }

        // Set status and body length
        httpExchange.sendResponseHeaders(response.getStatusCode(), body == null ? 0 : body.length);

        // Send body
        if (body != null) {
            OutputStream responseStream = httpExchange.getResponseBody();
            try {
                responseStream.write(body);
                responseStream.flush();
            } finally {
                responseStream.close();
            }
        }
    }


}

