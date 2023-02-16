package com.mochilafulfillment.server.dtos;

import com.mochilafulfillment.server.agv_utils.Constants;

public class RemoteGuiRecord {
    private boolean moveForward;
    private boolean moveBackwards;
    private boolean moveCCW;
    private boolean moveCW;
    private boolean forksUp;
    private boolean forksDown;
    private boolean newRemoteGuiToAGVRecord;
    private int velocity;
    private int acceleration;
    private int rotationVelocity = Constants.ROTATE_SPEED;
    private int rotationAcceleration = Constants.ROTATE_ACCEL;
    private boolean clientFinished;
    private boolean firstPassInRemoteControlMode = true;

    public boolean isMoveForward() {
        return moveForward;
    }

    public void setMoveForward(boolean moveForward) {
        this.moveForward = moveForward;
    }

    public boolean isMoveBackwards() {
        return moveBackwards;
    }

    public void setMoveBackwards(boolean moveBackwards) {
        this.moveBackwards = moveBackwards;
    }

    public boolean isMoveCCW() {
        return moveCCW;
    }

    public void setMoveCCW(boolean moveCCW) {
        this.moveCCW = moveCCW;
    }

    public boolean isMoveCW() {
        return moveCW;
    }

    public void setMoveCW(boolean moveCW) {
        this.moveCW = moveCW;
    }

    public boolean isForksUp() {
        return forksUp;
    }

    public void setForksUp(boolean forksUp) {
        this.forksUp = forksUp;
    }

    public boolean isForksDown() {
        return forksDown;
    }

    public void setForksDown(boolean forksDown) {
        this.forksDown = forksDown;
    }

    public boolean isNewRemoteGuiToAgvRecord() {
        return newRemoteGuiToAGVRecord;
    }

    public void setNewRemoteGuiToAGVRecord(boolean newRemoteGuiToAGVRecord) {
        this.newRemoteGuiToAGVRecord = newRemoteGuiToAGVRecord;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public int getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(int acceleration) {
        this.acceleration = acceleration;
    }

    public int getRotationVelocity() {
        return rotationVelocity;
    }

    public void setRotationVelocity(int rotationVelocity) {
        this.rotationVelocity = rotationVelocity;
    }

    public int getRotationAcceleration() {
        return rotationAcceleration;
    }

    public void setRotationAcceleration(int rotationAcceleration) {
        this.rotationAcceleration = rotationAcceleration;
    }

    public boolean isClientFinished() {
        return clientFinished;
    }

    public void setClientFinished(boolean guiClosed) {
        this.clientFinished = guiClosed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RemoteGuiRecord)) return false;
        RemoteGuiRecord that = (RemoteGuiRecord) o;
        return moveForward == that.moveForward && moveBackwards == that.moveBackwards && moveCCW == that.moveCCW && moveCW == that.moveCW && forksUp == that.forksUp && forksDown == that.forksDown && newRemoteGuiToAGVRecord == that.newRemoteGuiToAGVRecord && velocity == that.velocity && acceleration == that.acceleration && clientFinished == that.clientFinished;
    }

    public boolean isFirstPassInRemoteControlMode() {
        return firstPassInRemoteControlMode;
    }

    public void setFirstPassInRemoteControlMode(boolean firstPassInRemoteControlMode){
        this.firstPassInRemoteControlMode = firstPassInRemoteControlMode;
    }
}
