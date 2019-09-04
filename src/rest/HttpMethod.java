package rest;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    NONE;

    public static boolean isHttpMethod(String method) {
        try {
            HttpMethod.valueOf(method.toUpperCase());
            return true;
        }catch (IllegalArgumentException e) {
            return false;
        }
    }
}
