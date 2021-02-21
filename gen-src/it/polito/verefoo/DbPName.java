package it.polito.verefoo;

public enum DbPName {

    ISOLATION_PROPERTY("isolation_property"),
    REACHABILITY_PROPERTY("reachability_property");
    private final String value;

    DbPName(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DbPName fromValue(String v) {
        for (DbPName c: DbPName.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
