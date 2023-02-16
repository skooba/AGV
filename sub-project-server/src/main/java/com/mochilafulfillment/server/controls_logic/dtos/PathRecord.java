package com.mochilafulfillment.server.controls_logic.dtos;

import com.mochilafulfillment.server.agv_utils.Constants;


public class PathRecord {
    private int tagId;
    private int rotateByDegrees;
    private boolean pickLine;
    private boolean liftLine;
    private boolean lowerLine;
    private boolean placeLine;

    public PathRecord(int tagId, int rotateToDegrees, int forkLiftLineType) {
        this.tagId = tagId;
        this.rotateByDegrees = rotateToDegrees;
        if(forkLiftLineType == Constants.PICK_LINE){
            this.pickLine = true;
        } else if(forkLiftLineType == Constants.LIFT_LINE){
            this.liftLine = true;
        } else if (forkLiftLineType == Constants.LOWER_LINE){
            this.lowerLine = true;
        } else if (forkLiftLineType == Constants.PLACE_LINE){
            this.placeLine = true;
        }
    }

    public boolean isPickLine() {return pickLine;}

    public boolean isLiftLine() {return liftLine;}

    public boolean isLowerLine() {return lowerLine;}

    public boolean isPlaceLine() {return placeLine;}

    public int getTagId() { // from the CSV file 
        return tagId;
    }

    public int getRotateToDegrees() { // get the final angle that the AGV will have to rotate, this is read from the CSV file
        return rotateByDegrees;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PathRecord)) return false;
        PathRecord that = (PathRecord) o;
        return tagId == that.tagId && rotateByDegrees == that.rotateByDegrees && pickLine == that.pickLine && liftLine == that.liftLine && lowerLine == that.lowerLine;
    }
}