package com.mochilafulfillment.server.controls_logic.dtos;

// Class to store AGV internal attribute modifications
// Unlike MotorControllerRecord, can be updated asynchronously since not getting executed
public class StraightLineRecord {
    // Keep looping through and updating it with new values

    // LineMode specific parameters
    private double lastProportionalError = 0;
    private double lastIntegralError = 0;
    private int zeroYCounts = 0;

    // PickMode specific parameters

    public int getZeroYCounts() {
        return zeroYCounts;
    }

    public void setZeroYCounts(int zeroYCounts) {
        this.zeroYCounts = zeroYCounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StraightLineRecord)) return false;
        StraightLineRecord that = (StraightLineRecord) o;
        return Double.compare(that.lastProportionalError, lastProportionalError) == 0 && Double.compare(that.lastIntegralError, lastIntegralError) == 0 && zeroYCounts == that.zeroYCounts;
    }

}
