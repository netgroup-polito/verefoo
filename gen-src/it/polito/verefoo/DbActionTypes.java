package it.polito.verefoo;

public enum DbActionTypes {

    ALLOW,
    DENY;

    public String value() {
        return name();
    }

    public static DbActionTypes fromValue(String v) {
        return valueOf(v);
    }

}
