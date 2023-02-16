package com.mochilafulfillment.server.position_scanner.dtos;

import java.util.Objects;

// Data read by Position Scanner
public class PositionScannerResponseRecord {
    private int absoluteXPosition;
    private int absoluteYPosition;
    private int tagId;
    private int columns;
    private int rows;
    private double xPosition;
    private double yPosition;
    private double tagAngle;
    private double tapeAngle;
    private boolean onTape;
    private boolean newPositionScannerRecord;

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public double getXPosition() {
        return xPosition;
    }

    public void setXPosition(double xPosition) {
        this.xPosition = xPosition;
    }

    public double getYPosition() {
        return yPosition;
    }

    public void setYPosition(double yPosition) {
        this.yPosition = yPosition;
    }

    public double getTagAngle() {
        return tagAngle;
    }

    public void setTagAngle(double tagAngle) {
        this.tagAngle = tagAngle;
    }

    public double getTapeAngle(){
        return tapeAngle;
    }

    public void setTapeAngle(double tapeAngle) {
        this.tapeAngle = tapeAngle;
    }

    public boolean isOnTape() {
        return onTape;
    }

    public void setOnTape(boolean onTape) {
        this.onTape = onTape;
    }

    public boolean isNewPositionScannerRecord(){
        return newPositionScannerRecord;
    }

    // Position scanner is ready for a record to be transferred
    public void setNewPositionScannerRecord(boolean newPositionScannerRecord){
        this.newPositionScannerRecord = newPositionScannerRecord;
    }

    public void setAbsoluteXPosition(int absoluteXPosition) {
        this.absoluteXPosition = absoluteXPosition;
    }

    public int getAbsoluteXPosition(){
        return absoluteXPosition;
    }

    public void setAbsoluteYPosition(int absoluteYPosition) {
        this.absoluteYPosition = absoluteYPosition;
    }

    public int getAbsoluteYPosition(){
        return absoluteYPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PositionScannerResponseRecord)) return false;
        PositionScannerResponseRecord that = (PositionScannerResponseRecord) o;
        return absoluteXPosition == that.absoluteXPosition && absoluteYPosition == that.absoluteYPosition && tagId == that.tagId && columns == that.columns && rows == that.rows && xPosition == that.xPosition && yPosition == that.yPosition && tagAngle == that.tagAngle && tapeAngle == that.tapeAngle && onTape == that.onTape && newPositionScannerRecord == that.newPositionScannerRecord;
    }

}
