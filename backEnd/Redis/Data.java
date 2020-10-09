package com.example.rwredis;

public class Data {
    private int number;
    private String query;
    private String weibo_id;

    public String getWeibo_id() {
        return weibo_id;
    }

    public void setWeibo_id(String weibo_id) {
        this.weibo_id = weibo_id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
    public Data(String query, String weibo_id)
    {
        this.query=query;
        this.weibo_id=weibo_id;
        this.number=0;
    }
    public Data()
    {
    }
}
