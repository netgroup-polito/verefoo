package it.polito.verefoo;

public enum DbAllocationConstraintType {
    FORBIDDEN("forbidden"),
    FORCED("forced");

    private final String value;

    DbAllocationConstraintType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DbAllocationConstraintType fromValue(String v) {
        for (DbAllocationConstraintType c: DbAllocationConstraintType.values()) {
            if (c.value.equalsIgnoreCase(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
