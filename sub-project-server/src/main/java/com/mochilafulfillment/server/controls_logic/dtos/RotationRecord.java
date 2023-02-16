package com.mochilafulfillment.server.controls_logic.dtos;

public class RotationRecord {

    private boolean startedRotation = false;
    private int onTargetAngleCounter = 1;

    public boolean isStartedRotation() {
        return startedRotation;
    }

    public void setStartedRotation(boolean startedRotation) {
        this.startedRotation = startedRotation;
    }

    public int getOnTargetAngleCounter() {
        return onTargetAngleCounter; // Initialized to 0
    }

    public void setOnTargetAngleCounter(int onTargetAngleCounter) {
        this.onTargetAngleCounter = onTargetAngleCounter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RotationRecord)) return false;
        RotationRecord that = (RotationRecord) o;
        return startedRotation == that.startedRotation && onTargetAngleCounter == that.onTargetAngleCounter;
    }
}
