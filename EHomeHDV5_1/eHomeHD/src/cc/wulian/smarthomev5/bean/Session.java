package cc.wulian.smarthomev5.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/28.
 */

public class Session implements Serializable {
    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String bid;
    private String uid;
}
