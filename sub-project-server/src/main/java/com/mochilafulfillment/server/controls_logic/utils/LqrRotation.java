package com.mochilafulfillment.server.controls_logic.utils;

import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.dtos.AgvInputsRecord;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.RiccatiEquationSolverImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LqrRotation extends Lqr implements ControlLoop{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private final double vRef = Constants.EFFECTIVE_0; // has to be 0 for straight line because we don't want and rotational angular velocity

    static Array2DRowRealMatrix Q;
    static Array2DRowRealMatrix R;

    private int closestNinety = -1;

    //Constructor uses variable hiding so these values of Q and R are used instead
    public LqrRotation() {
        Q = Constants.LQR_Q_ROTATION;
        R = Constants.LQR_R_ROTATION;
    }

    @Override
    public Array2DRowRealMatrix[] calculateStateMatrix(double errorYPosition, double errorXPosition, double scannerAngleRadians, int rotationVelocity){
        logger.debug("LQR for rotation");
//        System.out.println("Q00: " + Q.getEntry(0,0));
//        System.out.println("Q11: " + Q.getEntry(1,1));
//        System.out.println("Q22: " + Q.getEntry(2,2));


        Array2DRowRealMatrix A = new Array2DRowRealMatrix(
                new double[][]{
                        new double[] {0, rotationVelocity, 0},
                        new double[] {-rotationVelocity, 0, vRef},
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

        Array2DRowRealMatrix e = new Array2DRowRealMatrix(
                new double[][]{
                        new double[] {errorXPosition},
                        new double[] {errorYPosition},
                        new double[] {-Math.abs(rotationVelocity)}
                }
        );

        Array2DRowRealMatrix transformationMatrix = new Array2DRowRealMatrix(
                new double[][] {
                        new double[] {Math.cos(scannerAngleRadians), Math.sin(scannerAngleRadians), 0},
                        new double[] {-Math.sin(scannerAngleRadians), Math.cos(scannerAngleRadians), 0},
                        new double[] {0, 0, 1}
                }
        );


        //Must transform to State Space coordinates
        Array2DRowRealMatrix eTransform = transformationMatrix.multiply(e);
        return new Array2DRowRealMatrix[]{A,B,eTransform};

    }

    @Override
    public double[] transformErrors(double scannerYPosition, double scannerXPosition, double scannerAngle, int xOffset, int yOffset) {
        //need to transform angles from straight line mode to absolute angles between 0-90

        double errorX = -(scannerXPosition * Math.cos(scannerAngle) + scannerYPosition * Math.sin(scannerAngle)) - xOffset;
        double errorY = -(- scannerXPosition * Math.sin(scannerAngle) + scannerYPosition * Math.cos(scannerAngle)) - yOffset;

        if((scannerAngle >= (Math.PI/4.)) ) {//Transform 90
            logger.debug("Transforming errors by 90 degrees");
            double tempErrorX = errorX;
            errorX = -errorY;
            errorY = tempErrorX;
        } else if(scannerAngle < (-Math.PI/4.)) { //Transform -90
            logger.debug("Transforming errors by -90 degrees");
            double tempErrorX = errorX;
            errorX = errorY;
            errorY = -tempErrorX;
        }

        //        System.out.println("angle: " + Math.toDegrees(scannerAngle) + ", errorY: " + errorY + ", errorX: "  + errorX);

        return new double[] {errorY, errorX};
    }

    @Override
    public double[] calculateVelocities(int nominalVelocity, double angleError, double closedLoopVelocity, double closedLoopAngularVelocity){
        double inputVelocity = - closedLoopVelocity;
//        System.out.println("inputVelocity: " + inputVelocity);
        double inputAngularVelocity = nominalVelocity - closedLoopAngularVelocity;
//        System.out.println("inputAngularVelocity: " + inputAngularVelocity);
        double wheel2ForwardVelocity = inputVelocity - (inputAngularVelocity * Constants.WHEEL_DISTANCE) / 2;
        double wheel1ForwardVelocity = inputVelocity + (inputAngularVelocity * Constants.WHEEL_DISTANCE) / 2;
//        System.out.println("wheel1: " + wheel1ForwardVelocity);
//        System.out.println("wheel2: " + wheel2ForwardVelocity);
        return new double[] {wheel2ForwardVelocity, wheel1ForwardVelocity};
    }

    @Override
    public Array2DRowRealMatrix calculateGain(Array2DRowRealMatrix A,Array2DRowRealMatrix B) {
        RiccatiEquationSolverImpl lqrSolution = new RiccatiEquationSolverImpl(A,B, Q, R);
        Array2DRowRealMatrix gainMatrix = (Array2DRowRealMatrix)lqrSolution.getK();
        return gainMatrix;
    }

    @Override
    public double transformAngle(double scannerAngle) {
        double scannerAngleRadians;
        if (closestNinety == -1){
            double numberOfNineties = scannerAngle / 90d;
            closestNinety = ((int) Math.round(numberOfNineties)) * 90;
            closestNinety = closestNinety % 360;
        }

        if(closestNinety == 0 && scannerAngle > 180){
            scannerAngle -= 360;
        }

        //CCW angles from the x-axis are positive
        scannerAngleRadians = Math.toRadians(closestNinety-scannerAngle);
        logger.debug("Transformed angle is " + Math.toDegrees(scannerAngleRadians));

        return scannerAngleRadians;
    }

    @Override
    public void resetTerms() {
        closestNinety = -1;
    }

    @Override
    public void setQ(Array2DRowRealMatrix q) {
        Q = q;
    }

    @Override
    public void setR(Array2DRowRealMatrix r) {
        R = r;
    }

    public void setClosestNinety(int closestNinety) {
        this.closestNinety = closestNinety;
    }
}
