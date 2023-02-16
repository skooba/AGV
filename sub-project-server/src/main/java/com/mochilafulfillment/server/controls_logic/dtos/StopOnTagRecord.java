package com.mochilafulfillment.server.controls_logic.dtos;

public class StopOnTagRecord {
    private int xCounter;
    private int nextId; // API. 0 if none. Gets changed when leaving tag
    private boolean oneTagAway;
    private int lastId;
    private boolean firstPass = true;
    private int gridDirection = 1; // -1 for backwards on a tag, 1 for forwards on a tag, 0 for error

    public int getNextId() {
        return nextId;
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

    public boolean isOneTagAway() { // Validates whether or not the AGV is one tag away or not
        return oneTagAway;
    }

    public void setOneTagAway(boolean oneTagAway) { // Set to true if AGV is one tag away from the tag it should stop on
        this.oneTagAway = oneTagAway;
    }

    public int getXCounter() { // Always at initial value of 0 except in test scripts.
        return xCounter;
    }

    public void setXCounter(int xCounter) { // Initialized to 0. Never set otherwise anywhere except in Test scripts.
        this.xCounter = xCounter;
    }

    public int getLastId() { // will be the tag read before the most recent one. Initialized to 0.
        return lastId;
    }

    public void setLastId(int lastId) { // When scanner is on a new tag, this value updates to the previously read tag
        this.lastId = lastId;
    } // starts as 0 and changes whenever on a new tag

    public void setFirstPass(boolean firstPass){
        this.firstPass = firstPass;
    }

    public boolean isFirstPass() {
        return firstPass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StopOnTagRecord)) return false;
        StopOnTagRecord that = (StopOnTagRecord) o;
        return xCounter == that.xCounter && nextId == that.nextId && oneTagAway == that.oneTagAway && lastId == that.lastId && firstPass == that.firstPass && gridDirection == that.gridDirection;
    }
}
