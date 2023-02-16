package com.mochilafulfillment.server.dtos;

import java.util.Objects;

// All inputs from agv sensors
public class AgvInputsRecord {
    private double x;
    private double y;
    private int tagId;
    private double tagAngle;
    private double tapeAngle;
    private int columnNumber;
    private int rowNumber;
    private boolean onTape;
    private boolean bottomSensorTriggered;
    private boolean middleSensorTriggered;
    private boolean topSensorTriggered;

    public boolean isBottomSensorTriggered(){
        return bottomSensorTriggered;
    }

    public void setBottomSensorTriggered(boolean bottomSensorTriggered){
        this.bottomSensorTriggered = bottomSensorTriggered;
    }

    public boolean isMiddleSensorTriggered(){
        return middleSensorTriggered;
    }

    public void setMiddleSensorTriggered(boolean middleSensorTriggered){
        this.middleSensorTriggered = middleSensorTriggered;
    }

    public boolean isTopSensorTriggered(){
        return topSensorTriggered;
    }

    public void setTopSensorTriggered(boolean topSensorTriggered){
        this.topSensorTriggered = topSensorTriggered;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) { // from position scanner
        this.x = x;
    }

    public double getY() {  // get for moving on straight line
        return y;
    }

    public void setY(double y) { // from position scanner
        this.y = y;
    } // set in LaptopPick mode run() method

    public void setOnTape(boolean onTape) {this.onTape = onTape;} // on tape determined by the position scanner

    public boolean isOnTape() {return onTape; }

    public int getTagId() { // tagID read from the position scanner. Will be 0 if not on a tag.
        return tagId;
    }

    public void setTagId(int tagId) { // from position scanner. tagID stays at the same value until a new tagID is read by the scanner.
        this.tagId = tagId;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public double getTagAngle() { // The angle picked up from the position scanner
        return tagAngle;
    }

    public void setTagAngle(double tagAngle) { // from position scanner set the angle
        this.tagAngle = tagAngle;
    }

    public double getTapeAngle() { // The angle picked up from the position scanner
        return tapeAngle;
    }

    public void setTapeAngle(double tapeAngle) { // from position scanner set the angle
        this.tapeAngle = tapeAngle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgvInputsRecord)) return false;
        AgvInputsRecord that = (AgvInputsRecord) o;
        return x == that.x && y == that.y && tagId == that.tagId && tagAngle == that.tagAngle && tapeAngle == that.tapeAngle && columnNumber == that.columnNumber && rowNumber == that.rowNumber && onTape == that.onTape && bottomSensorTriggered == that.bottomSensorTriggered && middleSensorTriggered == that.middleSensorTriggered && topSensorTriggered == that.topSensorTriggered;
    }

}
