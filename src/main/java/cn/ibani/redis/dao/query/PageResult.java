package cn.ibani.redis.dao.query;

import java.util.List;

public class PageResult<T> {
    private long totalResult;
    private int page;
    private List<T> results;
    public long getTotalResult() {
        return totalResult;
    }
    public void setTotalResult(long totalResult) {
        this.totalResult = totalResult;
    }
    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public List<T> getResults() {
        return results;
    }
    public void setResults(List<T> results) {
        this.results = results;
    }
}
