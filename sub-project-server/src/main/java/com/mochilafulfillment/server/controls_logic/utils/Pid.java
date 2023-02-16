package com.mochilafulfillment.server.controls_logic.utils;

import com.mochilafulfillment.server.agv_utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Pid implements ControlLoop {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private double lastProportionalError, lastIntegralError, kp, ki, kd, dt;
    private List<Double> errorArray = new ArrayList();
    private long lastTime = -1;
    private long currentTime = -1;
    private int integralType = Constants.INTEGRAL_TYPE;

    private int ki_loops = (Constants.KI_SECONDS * 1000) / Constants.TIME_STEP; // todo: update to use System.nanoTime()

    public Pid(double kp, double ki, double kd) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
    }

    @Override
    public double[] iteration(double scannerYPosition, double scannerXPosition, double scannerAngle, int nominalVelocity){
        //straight line rotation doesn't use xPosition information

        //Adjusted y position to account for theoretical offset of position scanner
        double angleRadians = transformAngle(scannerAngle);

        double adjustedYPosition = transformErrors(scannerYPosition, scannerXPosition, angleRadians, Constants.SCANNER_X_OFFSET, Constants.SCANNER_Y_OFFSET)[0];

        //Calculate elapsed time since last iteration
        this.currentTime = System.nanoTime();
        this.dt = (this.currentTime - this.lastTime) / (1e9);
        this.lastTime = this.currentTime;
        logger.debug("elapsed time (dt) is " + dt);

        double proportionalError = calculateProportional(adjustedYPosition);
        double derivativeError = calculateDerivative(proportionalError);
        double integralError;
        if (this.integralType == 0) {
            integralError = calculateStandardIntegral(proportionalError);
        } else if (this.integralType == 1) {
            integralError = calculateTruncatedIntegral(proportionalError);
        } else {
            integralError = calculateScaledIntegral(proportionalError);
        }
        this.lastIntegralError = integralError;
        this.lastProportionalError = proportionalError;

        double pidResult = kp * proportionalError + ki * integralError + kd * derivativeError;
        double motorVelocityRatio = (pidResult + nominalVelocity) / nominalVelocity;

        logger.debug("PID motor velocity ratio is " + motorVelocityRatio);

        return new double[] {motorVelocityRatio, 0, 0};
    }

    @Override
    public void resetTerms(){
        this.lastTime = 0;
        this.lastIntegralError = 0;
        this.lastProportionalError = 0;
    }

    @Override
    public double[] transformErrors(double scannerYPosition, double scannerXPosition, double scannerAngle, int xOffset, int yOffset) {
        double errorY = modifyYPosition(scannerYPosition, scannerAngle, 0, 0);
        return new double[] {errorY, 0d}; //No x error in PID algorithm
    }

    @Override
    public double transformAngle(double scannerAngle){
        double angleRadians = Math.toRadians(scannerAngle);
        return  angleRadians;
    }

    public double modifyYPosition(double initY, double angle, int xOffset, int yOffset) {
        double xOffsetAngleAdjusted = -xOffset * Math.sin(angle);
        double yOffsetAngleAdjusted = yOffset * Math.cos(angle);
        double pgvReadAngleAdjusted = initY * Math.cos(angle);
        double modifiedY = xOffsetAngleAdjusted + yOffsetAngleAdjusted + pgvReadAngleAdjusted;
        logger.debug("modified Y position is " + modifiedY);
        return modifiedY;
    }

    public double calculateProportional(double scannerYDirectionOffset){
        double proportional = 0 - scannerYDirectionOffset;
        logger.debug("proportional term is " + proportional);
        return proportional;
    }

    public double calculateStandardIntegral(double proportionalError){
        double integralError = lastIntegralError + proportionalError;
        logger.debug("integral (standard) term is " + integralError);
        return integralError;
    }

    public double calculateTruncatedIntegral(double lastProportionalError) {
        double integralError = lastIntegralError + lastProportionalError;
        if (integralError > Constants.MAX_INTEGRAL_ALLOWED){
            return Constants.MAX_INTEGRAL_ALLOWED;
        } else if (integralError < Constants.MIN_INTEGRAL_ALLOWED){
            return Constants.MIN_INTEGRAL_ALLOWED;
        } else {
            logger.debug("integral (truncated) term is " + integralError);
            return integralError;
        }
    }

    public double calculateScaledIntegral(double lastProportionalError){
        errorArray.add(0, lastProportionalError);
        while(errorArray.size() > ki_loops) {
            errorArray.remove(errorArray.size() -1 );
        }
        double integralError = 0;
        for(double value : errorArray) {
            integralError += value / (ki_loops * Constants.INTEGRAL_WEIGHT_FACTOR);
        }
        logger.debug("integral (scaled) term is " + integralError);
        return integralError;
    }

    public double calculateDerivative(double proportionalError){
        if (lastTime == -1) {
            lastTime =System.nanoTime();
            return 0;
        }
        double derivativeError = (lastProportionalError - proportionalError)/(this.dt);
        logger.debug("derivative error is " + derivativeError);
        return derivativeError;
    }


    public void setLastProportionalError(double lastProportionalError) {
        this.lastProportionalError = lastProportionalError;
    }

    public void setLastIntegralError(double lastIntegralError) {
        this.lastIntegralError = lastIntegralError;
    }

    public void setLastTime(long lastTime){
        this.lastTime = lastTime;
    }

    public double getLastIntegralError() {
        return lastIntegralError;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setDt(double dt) {
        this.dt = dt;
    }

    public void setErrorArray(List<Double> errorArray) {
        this.errorArray = errorArray;
    }

    public void setKi_loops(int ki_loops) {
        this.ki_loops = ki_loops;
    }

    public void setIntegralType(int integralType) {this.integralType = integralType;}
}
