package io.github.michaelfedora.fedorasmarket.database;

/**
 * Created by Michael on 3/15/2016.
 */
public enum DatabaseQuery {
    AUTHOR("author", "uuid"),
    CATEGORY("category", "varchar(255)"),
    NAME("name", "varchar(255)"),
    DATA("data", "other");

    public final String v;
    public final String dbtype;

    DatabaseQuery(String dbentry, String dbtype) {
        this.v = dbentry;
        this.dbtype = dbtype;
    }

    static String makeConstructor() {
        StringBuilder constructor = new StringBuilder("(");
        int count = 0;
        for(DatabaseQuery q : values()) {
            constructor.append(q.v).append(" ").append(q.dbtype);
            if(++count < values().length)
                constructor.append(", ");
        }
        constructor.append(")");

        return constructor.toString();
    }
}
