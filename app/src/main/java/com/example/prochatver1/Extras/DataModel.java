package com.example.prochatver1.Extras;

public class DataModel {
    private String target;
    private String sender;
    private String data;
    private DataModelType type;
    String status;

    public DataModel(String target, String sender, String data, DataModelType type) {
        this.target = target;
        this.sender = sender;
        this.data = data;
        this.type = type;
    }
    public DataModel(){

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public DataModelType getType() {
        return type;
    }

    public void setType(DataModelType type) {
        this.type = type;
    }
}
