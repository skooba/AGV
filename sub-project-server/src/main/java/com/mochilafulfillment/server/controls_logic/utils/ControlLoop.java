package com.mochilafulfillment.server.controls_logic.utils;

import com.mochilafulfillment.server.dtos.AgvInputsRecord;

public interface ControlLoop {
    double[] iteration(double scannerYPosition, double scannerXPosition, double scannerAngle, int nominalVelocity);
    void resetTerms();
    double[] transformErrors(double scannerYPosition, double scannerXPosition, double scannerAngle, int xOffset, int yOffset );
    double transformAngle(double scannerAngle);
}
