package it.polito.verefoo;

public enum DbTypeOfHost {
    CLIENT,
    SERVER,
    MIDDLEBOX;

    public String value() {
        return name();
    }

    public static DbTypeOfHost fromValue(String v) {
        return valueOf(v);
    }
}
