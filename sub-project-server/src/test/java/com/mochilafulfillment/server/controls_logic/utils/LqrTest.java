package com.mochilafulfillment.server.controls_logic.utils;

import com.mochilafulfillment.server.agv_utils.Constants;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.RealMatrix;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LqrTest {

    Lqr testClass;
    @BeforeEach
    public void init(){
        testClass = new Lqr();
    }

    @Test
    public void calculateStateMatrixTest(){
        int testYPosition = 10;
        int testXPosition = 5;
        double testScannerAngle = Math.toRadians(5);
        int testNominalVelocity = 42;
        double omegaRef = Constants.EFFECTIVE_0;

        Array2DRowRealMatrix[] result = testClass.calculateStateMatrix(testYPosition, testXPosition, testScannerAngle, testNominalVelocity);
        Array2DRowRealMatrix ATestResult = result[0];
        Array2DRowRealMatrix BTestResult = result[1];
        Array2DRowRealMatrix eTestResult = result[2];

        Array2DRowRealMatrix ATest =  new Array2DRowRealMatrix(
                new double[][] {
                        new double[] {0, omegaRef, 0},
                        new double[] {-omegaRef, 0, testNominalVelocity},
                        new double[] {0, 0, 0}
                });

        Array2DRowRealMatrix BTest =  new Array2DRowRealMatrix(
                new double[][] {
                        new double[] {1, 0},
                        new double[] {0, 0},
                        new double[] {0, 1}
                }
        );

        Array2DRowRealMatrix eTransformTest = new Array2DRowRealMatrix(
                new double[][]{
                        new double[] {Math.cos(testScannerAngle) * testXPosition + Math.sin(testScannerAngle) * testYPosition},
                        new double[] {-Math.sin(testScannerAngle) * testXPosition + Math.cos(testScannerAngle) * testYPosition},
                        new double[] {testScannerAngle * 1.5},
                });

        Assertions.assertEquals(ATest, ATestResult);
        Assertions.assertEquals(BTest, BTestResult);
        Assertions.assertEquals(eTransformTest, eTestResult);

    }

    @Test
    public void calculateClosedLoopInputsTest(){
        int k11 = 5;
        int k12 = 4;
        int k13 = 10;
        int k21 = 6;
        int k22 = 12;
        int k23 = 1;
        int testYPosition = 10;
        double testScannerAngle = Math.toRadians(5);

        Array2DRowRealMatrix kTest = new Array2DRowRealMatrix(
            new double[][]{
                    new double[] {k11,k12,k13},
                    new double[] {k21,k22,k23},
            });

        Array2DRowRealMatrix eTransformTest = new Array2DRowRealMatrix(
                new double[][]{
                        new double[] {Math.cos(Math.toRadians(testScannerAngle)) * testYPosition + Math.sin(Math.toRadians(testScannerAngle)) * 1},
                        new double[] {-Math.sin(Math.toRadians(testScannerAngle)) * testYPosition + Math.cos(Math.toRadians(testScannerAngle)) * 1},
                        new double[] {testScannerAngle},
                });

        double[] closedLoopInputs = testClass.calculateClosedLoopInputs(kTest, eTransformTest);
        double closedLoopVelocityResult = closedLoopInputs[0];
        double closedLoopAngularVelocityResult = closedLoopInputs[1];

        double closedLoopVelocity = -(k11 * (Math.cos(Math.toRadians(testScannerAngle)) * testYPosition + Math.sin(Math.toRadians(testScannerAngle)) * 1)
                + k12 * (-Math.sin(Math.toRadians(testScannerAngle)) * testYPosition + Math.cos(Math.toRadians(testScannerAngle)) * 1)
                + k13 * (testScannerAngle));

        double closedLoopAngularVelocity = -(k21 * (Math.cos(Math.toRadians(testScannerAngle)) * testYPosition + Math.sin(Math.toRadians(testScannerAngle)) * 1)
                + k22 * (-Math.sin(Math.toRadians(testScannerAngle)) * testYPosition + Math.cos(Math.toRadians(testScannerAngle)) * 1)
                + k23 * (testScannerAngle));

        Assertions.assertEquals(closedLoopVelocity, closedLoopVelocityResult);
        Assertions.assertEquals(closedLoopAngularVelocity, closedLoopAngularVelocityResult);
    }

    @Test
    public void calculateVelocitiesTest(){
        int testScannerAngle = 0;
        int testNominalVelocity = 30;
        double closedLoopVelocity = 5.5;
        double closedLoopAngularVelocity = -1;

        double wheel1Velocity = 24.5 - (closedLoopAngularVelocity * Constants.WHEEL_DISTANCE) / 2;
        double wheel2Velocity = 24.5 + (closedLoopAngularVelocity * Constants.WHEEL_DISTANCE) / 2;


        double[] wheelVelocitiesTest = testClass.calculateVelocities(testNominalVelocity, testScannerAngle, closedLoopVelocity, closedLoopAngularVelocity);
        double wheel2VelocityTest = wheelVelocitiesTest[0];
        double wheel1VelocityTest = wheelVelocitiesTest[1];

        Assertions.assertEquals(wheel1Velocity, wheel1VelocityTest);
        Assertions.assertEquals(wheel2Velocity, wheel2VelocityTest);
    }

    @Test
    public void iterationPositiveAngleTest(){
        int testYPosition = 10;
        int testXPosition = 5;
        int testScannerAngle = 355;
        int testNominalVelocity = 42;

        Array2DRowRealMatrix Q = new Array2DRowRealMatrix(
                new double[][] {
                        new double[] {1, 0, 0},
                        new double[] {0, 1, 0},
                        new double[] {0, 0, 10}
                }
        );

        Array2DRowRealMatrix R = new Array2DRowRealMatrix(
                new double[][] {
                        new double[] {0.0001, 0},
                        new double[] {0, 0.0001}
                }
        );

        testClass.setQ(Q);
        testClass.setR(R);

        double[] result = testClass.iteration(testYPosition,testXPosition, testScannerAngle, testNominalVelocity);

        double v = 214.8231601;
        double omega = 1024.15788;
        double wheel2ForwardVelocity = v - (omega * Constants.WHEEL_DISTANCE) / 2;
        double wheel1ForwardVelocity = v + (omega * Constants.WHEEL_DISTANCE) / 2;

        double motorVelocityRatio = wheel1ForwardVelocity/wheel2ForwardVelocity;


        Assertions.assertEquals(motorVelocityRatio, result[0], .0001);

    }

    @Test
    public void iterationNegativeAngleTest(){
        int testYPosition = 10;
        int testXPosition = 10;
        int testScannerAngle = 5;
        int testNominalVelocity = 42;

        Array2DRowRealMatrix Q = new Array2DRowRealMatrix(
                new double[][] {
                        new double[] {1, 0, 0},
                        new double[] {0, 1, 0},
                        new double[] {0, 0, 10}
                }
        );

        Array2DRowRealMatrix R = new Array2DRowRealMatrix(
                new double[][] {
                        new double[] {0.0001, 0},
                        new double[] {0, 0.0001}
                }
        );
        testClass.setQ(Q);
        testClass.setR(R);

        double[] result = testClass.iteration(testYPosition, testXPosition, testScannerAngle, testNominalVelocity);

        double v = 172.9873439;
        double omega = -952.3250069;
        double wheel2ForwardVelocity = v - (omega * Constants.WHEEL_DISTANCE) / 2;
        double wheel1ForwardVelocity = v + (omega * Constants.WHEEL_DISTANCE) / 2;

        double motorVelocityRatio = wheel1ForwardVelocity/wheel2ForwardVelocity;

        Assertions.assertEquals(motorVelocityRatio, result[0], .01);

    }

    @Test
    public void transformAngleNoOffsetPositiveAngleTest(){
        int scannerYPosition = 10;
        int scannerXPosition = 5;
        double scannerAngle = Math.toRadians(5);
        int xOffset = 0;
        int yOffset = 0;
        double[] transformedTestValues = testClass.transformErrors(scannerYPosition,scannerXPosition,scannerAngle,xOffset,yOffset);
        double yTest = transformedTestValues[0];
        double xTest = transformedTestValues[1];

        Assertions.assertEquals(yTest, 9.9240, .0001);
        Assertions.assertEquals(xTest, 0.8682, .0001);

    }

    @Test
    public void transformAngleNoOffsetNegativeAngleTest(){
        int scannerYPosition = 10;
        int scannerXPosition = 5;
        double scannerAngle = Math.toRadians(-5);
        int xOffset = 0;
        int yOffset = 0;
        double[] transformedTestValues = testClass.transformErrors(scannerYPosition,scannerXPosition,scannerAngle,xOffset,yOffset);
        double yTest = transformedTestValues[0];
        double xTest = transformedTestValues[1];

        Assertions.assertEquals(yTest, 9.9240, .0001);
        Assertions.assertEquals(xTest, -0.8682, .0001);
    }

    @Test
    public void transformAngleTest(){
        int scannerAngle1 = 1;
        int scannerAngle2 = 359;
        int scannerAngle3 = 179;
        int scannerAngle4 = 181;
        int scannerAngle5 = 385;
        int scannerAngle6 = -20;

        double response1 = testClass.transformAngle(scannerAngle1);
        double response2 = testClass.transformAngle(scannerAngle2);
        double response3 = testClass.transformAngle(scannerAngle3);
        double response4 = testClass.transformAngle(scannerAngle4);
        double response5 = testClass.transformAngle(scannerAngle5);
        double response6 = testClass.transformAngle(scannerAngle6);

        double compare1 = Math.toRadians(-1);
        double compare2 = Math.toRadians(360 - scannerAngle2);
        double compare3 = Math.toRadians(-179);
        double compare4 = Math.toRadians(360 - scannerAngle4);
        double compare5 = Math.toRadians(-(scannerAngle5 - 360));
        double compare6 = Math.toRadians(360 - (scannerAngle6 + 360));

        Assertions.assertEquals(response1, compare1);
        Assertions.assertEquals(response2, compare2);
        Assertions.assertEquals(response3, compare3);
        Assertions.assertEquals(response4, compare4);
        Assertions.assertEquals(response5, compare5);
        Assertions.assertEquals(response6, compare6);

    }
}
