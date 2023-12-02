package org.example;

import java.io.Serializable;

public class MqMessage implements Serializable {

    private String id;

    private Integer action;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }
}
