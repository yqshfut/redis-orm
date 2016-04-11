package cn.ibani.redis.dao.query;

import java.util.HashMap;
import java.util.Map;

public class PageQuery {

    private Map<String,String> queries = new HashMap<String, String>();
    private String sortField;
    private boolean sortAsc = true;
    
    public Map<String, String> getQueries() {
        return queries;
    }
    public void setQueries(Map<String, String> queries) {
        this.queries = queries;
    }
    public String getSortField() {
        return sortField;
    }
    public void setSortField(String sortField) {
        this.sortField = sortField;
    }
    public boolean isSortAsc() {
        return sortAsc;
    }
    public void setSortAsc(boolean sortAsc) {
        this.sortAsc = sortAsc;
    }
    
}
