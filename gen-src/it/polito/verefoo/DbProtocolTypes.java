package it.polito.verefoo;

public enum DbProtocolTypes {

    HTTP_REQUEST("HTTP_REQUEST"),
    HTTP_RESPONSE("HTTP_RESPONSE"),
    POP_3_REQUEST("POP3_REQUEST"),
    POP_3_RESPONSE("POP3_RESPONSE");
    private final String value;

    DbProtocolTypes(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DbProtocolTypes fromValue(String v) {
        for (DbProtocolTypes c: DbProtocolTypes.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
