package com.mochilafulfillment.host.dtos;

public class ClientRecord {
    private boolean startGui;
    private boolean endGui;

    public void setStartGui(boolean startGui) {
        this.startGui = startGui;
    }

    public boolean isStartGui(){
        return this.startGui;
    }

    public void setEndGui(boolean endGui) {
        this.endGui = endGui;
    }

    public boolean isEndGui() {
        return this.endGui;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientRecord)) return false;
        ClientRecord that = (ClientRecord) o;
        return startGui == that.startGui && endGui == that.endGui;
    }
}