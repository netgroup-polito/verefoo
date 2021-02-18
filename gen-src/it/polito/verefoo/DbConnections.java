package it.polito.verefoo;

import java.util.ArrayList;
import java.util.List;


public class DbConnections {
    
    protected List<DbConnection> connection;

    /**
     * Gets the value of the connection property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the connection property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConnection().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Connection }
     * 
     * 
     */
    public List<DbConnection> getConnection() {
        if (connection == null) {
            connection = new ArrayList<DbConnection>();
        }
        return this.connection;
    }

}
