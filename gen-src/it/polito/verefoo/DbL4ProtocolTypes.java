package it.polito.verefoo;

public enum DbL4ProtocolTypes {

    ANY, TCP, UDP, OTHER;

    public String value() {
        return name();
    }

    public static DbL4ProtocolTypes fromValue(String v) {
        return valueOf(v);
    }

}
