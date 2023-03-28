package icia.js.lostandfound.beans;

import java.io.Serializable;
import java.util.List;


public class MonthBean implements Serializable {
    private String month;

    public MonthBean(String month) {
        this.month = month;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public boolean isLast(List<MonthBean> list) {
        return list.indexOf(this) == list.size() - 1;
    }
}
