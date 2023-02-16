package com.mochilafulfillment.server.controls_logic.utils;

import com.mochilafulfillment.server.agv_utils.Constants;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.RiccatiEquationSolverImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lqr implements ControlLoop{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final double omegaRef = Constants.EFFECTIVE_0; // has to be 0 for straight line because we don't want and rotational angular velocity
    Array2DRowRealMatrix Q = Constants.LQR_Q;
    Array2DRowRealMatrix R = Constants.LQR_R;

    @Override
    public double[] iteration(double scannerYPosition, double scannerXPosition, double scannerAngle, int nominalVelocity) {
        //straight line rotation doesn't use xPosition information

        /*Transform the angles read from the scanner to be about 180degrees (pi radians)
        The scanner will read 0 degrees when going straight on the line
        0 radians results in the error in y-position term not affecting the control
        Therefore the reference angle is 180 degrees = PI radians and the angle read by the scanner is angle + PI
        */

        double scannerAngleRadians = transformAngle(scannerAngle);


//        System.out.println("angle: " + Math.toDegrees(scannerAngleRadians));
//        System.out.println("yPosition: " + scannerYPosition);
//        System.out.println("xPosition: " + scannerXPosition);

        double[] errors = transformErrors(scannerYPosition, scannerXPosition, scannerAngleRadians, Constants.SCANNER_Y_OFFSET,Constants.SCANNER_X_OFFSET);

        double errorYPosition = errors[0];
        double errorXPosition = errors[1];
        logger.debug("Error in x position is " + errorXPosition);
        logger.debug("Error in y position is " + errorYPosition);
//        System.out.println("errorYPosition: " + errorYPosition);
//        System.out.println("errorXPosition: " + errorXPosition);

        Array2DRowRealMatrix[] stateMatrices = calculateStateMatrix(errorYPosition, errorXPosition, scannerAngleRadians, nominalVelocity);
        Array2DRowRealMatrix A = stateMatrices[0];
        Array2DRowRealMatrix B = stateMatrices[1];
        Array2DRowRealMatrix errorMatrix = stateMatrices[2];

//        System.out.println("errorMatrix 1: " + errorMatrix.getEntry(0,0));
//        System.out.println("errorMatrix 2: " + errorMatrix.getEntry(1,0));
//        System.out.println("errorMatrix 3: " + errorMatrix.getEntry(2,0));

        Array2DRowRealMatrix gainMatrix = calculateGain(A,B);

//        System.out.println("k11: " + gainMatrix.getEntry(0,0));
//        System.out.println("k21: " + gainMatrix.getEntry(0,1));
//        System.out.println("k31: " + gainMatrix.getEntry(0,2));
//        System.out.println("k21: " + gainMatrix.getEntry(1,0));
//        System.out.println("k22: " + gainMatrix.getEntry(1,1));
//        System.out.println("k23: " + gainMatrix.getEntry(1,2));


        double[] closedLoopInputs = calculateClosedLoopInputs(gainMatrix, errorMatrix);
        double velocityClosedLoop = closedLoopInputs[0];
        double angularVelocityClosedLoop = closedLoopInputs[1];

//        System.out.println("closed loop velocity: " + velocityClosedLoop);
//        System.out.println("closed loop angular velocity: " + angularVelocityClosedLoop);

        double[] wheelForwardVelocities = calculateVelocities(nominalVelocity, scannerAngleRadians - omegaRef, velocityClosedLoop, angularVelocityClosedLoop);
        double wheel2ForwardVelocity = wheelForwardVelocities[0];
        double wheel1ForwardVelocity = wheelForwardVelocities[1];
        logger.debug("LQR calculated motor 1 velocity is " + wheel1ForwardVelocity);
        logger.debug("LQR calculated motor 2 velocity is " + wheel2ForwardVelocity);
//        System.out.println("wheel2ForwardVelocity: " + wheel2ForwardVelocity);
//        System.out.println("wheel1ForwardVelocity: " + wheel1ForwardVelocity);

        double motorVelocityRatio = wheel1ForwardVelocity/wheel2ForwardVelocity;
        logger.debug("LQR motor velocity ratio is " + motorVelocityRatio);
//        System.out.println("RATIO: " + motorVelocityRatio);
        return new double[] {motorVelocityRatio, wheel2ForwardVelocity, wheel1ForwardVelocity};
    }

    @Override
    public void resetTerms() {
        //no terms to reset with lqr
    }

    @Override
    public double[] transformErrors(double scannerYPosition, double scannerXPosition, double scannerAngle, int xOffset, int yOffset){
        //straight line rotation doesn't use xPosition information
        double errorY = (Math.cos(scannerAngle) * Math.cos(scannerAngle) * (scannerYPosition));
        double errorX = (Math.sin(scannerAngle) * Math.cos(scannerAngle) * (scannerYPosition));

        return new double[] {errorY, errorX};
    }

    public Array2DRowRealMatrix[] calculateStateMatrix(double errorYPosition, double errorXPosition, double scannerAngleRadians, int nominalVelocity){
        logger.debug("LQR for straight line");

        double scannerAngleError = scannerAngleRadians;

        Array2DRowRealMatrix A = new Array2DRowRealMatrix(
                new double[][]{
                        new double[] {0, omegaRef, 0},
                        new double[] {-omegaRef, 0, nominalVelocity},
                        new double[] {0, 0, 0}
                }
        );

        Array2DRowRealMatrix B = new Array2DRowRealMatrix(
                new double[][]{
                        new double[] {1, 0},
                        new double[] {0, 0},
                        new double[] {0, 1}
                }
        );

        //This is the error matrix
        // the Q matrix will determine the weight of the constant
        Array2DRowRealMatrix e = new Array2DRowRealMatrix(
                new double[][]{
                        new double[] {errorXPosition},
                        new double[] {errorYPosition},
                        new double[] {scannerAngleError * 1.5}
                }
        );

        Array2DRowRealMatrix transformationMatrix = new Array2DRowRealMatrix(
                new double[][] {
                        new double[] {Math.cos(scannerAngleError), Math.sin(scannerAngleError), 0},
                        new double[] {-Math.sin(scannerAngleError), Math.cos(scannerAngleError), 0},
                        new double[] {0, 0, 1}
                }
        );

        //Must transform to State Space coordinates
        Array2DRowRealMatrix eTransform = transformationMatrix.multiply(e);
        return new Array2DRowRealMatrix[]{A,B,eTransform};
    }

    public double[] calculateClosedLoopInputs(Array2DRowRealMatrix gainMatrix, Array2DRowRealMatrix errorMatrix){
        Array2DRowRealMatrix closedLoopInputMatrix = gainMatrix.multiply(errorMatrix);
        closedLoopInputMatrix = closedLoopInputMatrix.multiply(Constants.NEGATIVE_IDENTITY);
        double closedLoopVelocity = closedLoopInputMatrix.getEntry(0,0);
        double closedLoopAngularVelocity = closedLoopInputMatrix.getEntry(1,0);
        return new double[] {closedLoopVelocity, closedLoopAngularVelocity};
    }

    public double[] calculateVelocities(int nominalVelocity, double angleError, double closedLoopVelocity, double closedLoopAngularVelocity){
        double inputVelocity = nominalVelocity * Math.cos(angleError) - closedLoopVelocity;
        double inputAngularVelocity = omegaRef - closedLoopAngularVelocity;
        double wheel2ForwardVelocity = inputVelocity - (inputAngularVelocity * Constants.WHEEL_DISTANCE) / 2;
        double wheel1ForwardVelocity = inputVelocity + (inputAngularVelocity * Constants.WHEEL_DISTANCE) / 2;
        return new double[] {wheel2ForwardVelocity, wheel1ForwardVelocity};
    }

    public Array2DRowRealMatrix calculateGain(Array2DRowRealMatrix A,Array2DRowRealMatrix B) {
        RiccatiEquationSolverImpl lqrSolution = new RiccatiEquationSolverImpl(A,B, Q, R);
        Array2DRowRealMatrix gainMatrix = (Array2DRowRealMatrix)lqrSolution.getK();
        return gainMatrix;
    }

    public double transformAngle(double scannerAngle) {
        if(scannerAngle > 360){
            scannerAngle -= 360;
        } else if(scannerAngle < 0){
            scannerAngle += 360;
        }
        if (scannerAngle > 360 || scannerAngle < 0) {
            logger.error("Error: Unacceptable angle passed: " + scannerAngle);
        }
        double scannerAngleRadians;
        if (scannerAngle > 180) {
            scannerAngleRadians = Math.toRadians(360 - scannerAngle);
        } else {
            scannerAngleRadians = -Math.toRadians(scannerAngle);
        }
        return scannerAngleRadians;
    }

    public void setQ(Array2DRowRealMatrix Q){
        this.Q = Q;
    }

    public void setR(Array2DRowRealMatrix R){
        this.R = R;
    }
}
