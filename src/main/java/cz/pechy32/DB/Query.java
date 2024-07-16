package cz.pechy32.DB;

/**
 * Třída pro sestavení SQL dotazu.
 */
public class Query {
    private StringBuilder query;

    /**
     * Inicializuje nový SQL dotaz pro odstranění záznamů z určené tabulky.
     *
     * @param table název tabulky
     * @return aktuální instanci Query
     */
    public Query delete(String table){
        if(query == null) {
            query = new StringBuilder();
        }
        query.append("DELETE FROM ");
        query.append(table);
        return this;
    }

    /**
     * Přidá podmínku WHERE do SQL dotazu.
     *
     * @param requirement podmínka
     * @return aktuální instanci Query
     */
    public Query where(String requirement){
        if (requirement != null) {
            query.append(" WHERE ");
            query.append(requirement);
        }
        return this;
    }

    /**
     * Specifikuje zdroj dat pro SQL dotaz.
     *
     * @param table název tabulky
     * @return aktuální instanci Query
     */
    public Query from(String table){
        query.append(" FROM ");
        query.append(table);
        return this;
    }

    /**
     * Inicializuje nový SQL dotaz pro aktualizaci záznamů v určené tabulce.
     *
     * @param table název tabulky
     * @return aktuální instanci Query
     */
    public Query update(String table){
        if(query == null) {
            query = new StringBuilder();
        }
        query.append("UPDATE ");
        query.append(table);
        query.append(" SET ");
        return this;
    }

    /**
     * Doplní sloupce pro aktualizaci v SQL dotazu.
     *
     * @param columns pole názvů sloupců
     * @return aktuální instanci Query
     */
    public Query set(String[] columns){
        int count = columns.length;
        if(count == 0)
            throw new IllegalArgumentException("Neplatný počet parametrů");

        for(String column : columns){
            query.append(column);
            query.append(" = ");
            query.append("?");
            query.append(",");
        }
        query.deleteCharAt(query.lastIndexOf(","));
        return this;
    }

    /**
     * Inicializuje nový SQL dotaz pro vložení záznamů do určené tabulky.
     *
     * @param table název tabulky
     * @return aktuální instanci Query
     */
    public Query insert(String table){
        if(query == null) {
            query = new StringBuilder();
        }
        query.append("INSERT INTO ");
        query.append(table);
        return this;
    }

    /**
     * Doplní hodnoty pro vložení v SQL dotazu.
     *
     * @param params pole hodnot
     * @return aktuální instanci Query
     */
    public Query values(Object[] params){
        query.append(" VALUES(");

        int count = params.length;

        if(count == 0)
            throw new IllegalArgumentException("Neplatný počet parametrů");

        for (int i = 0; i<count; i++) {
            query.append("?,");
        }
        //odstaníme poslední čárku
        query.deleteCharAt(query.lastIndexOf(","));
        query.append(");");
        return this;
    }

    /**
     * Specifikuje sloupce pro výběr v SQL dotazu.
     *
     * @param columns pole názvů sloupců
     * @return aktuální instanci Query
     */
    public Query select(Object[] columns) {
        if(query == null) {
            query = new StringBuilder();
        }
        query.append("SELECT ");
        if (columns != null && columns.length > 0) {
            for (Object column : columns) {
                query.append(column);
                query.append(",");
            }
            query.deleteCharAt(query.lastIndexOf(","));
        } else {
            query.append("*");
        }
        return this;
    }

    /**
     * Vrátí sestavený SQL dotaz jako řetězec.
     *
     * @return SQL dotaz
     */
    public String getQuery(){
        if(query == null) {
            query = new StringBuilder();
        }
        return query.toString();
    }
}
