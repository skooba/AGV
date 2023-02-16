package com.mochilafulfillment.server.controls_logic.utils;

import com.mochilafulfillment.server.agv_utils.Constants;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LqrRotationTest {
    LqrRotation testClass;
    @BeforeEach
    public void init(){
        testClass = new LqrRotation();
    }

    @Test
    public void calculateStateMatrixPositiveAngleTest(){
        int testYPosition = 10;
        int testXPosition = 5;
        double testScannerAngle = Math.toRadians(5);
        int testRotationVelocity = 42;
        double vRef = Constants.EFFECTIVE_0;

        Array2DRowRealMatrix[] result = testClass.calculateStateMatrix(testYPosition, testXPosition, testScannerAngle, testRotationVelocity);
        Array2DRowRealMatrix ATestResult = result[0];
        Array2DRowRealMatrix BTestResult = result[1];
        Array2DRowRealMatrix eTestResult = result[2];

        Array2DRowRealMatrix ATest =  new Array2DRowRealMatrix(
                new double[][] {
                        new double[] {0, testRotationVelocity, 0},
                        new double[] {-testRotationVelocity, 0, vRef},
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
                        new double[] {-Math.abs(testRotationVelocity)},
                });

        Assertions.assertEquals(ATest, ATestResult);
        Assertions.assertEquals(BTest, BTestResult);
        Assertions.assertEquals(eTransformTest, eTestResult);

    }

    @Test
    public void calculateVelocitiesTest(){
        int nominalVelocity = 20;
        double angle = Math.toRadians(10);
        double closedLoopVelocity = .4301;
        double closedLoopAngularVelocity = 4;

        double[] motorVelocities = testClass.calculateVelocities(nominalVelocity, angle,closedLoopVelocity,closedLoopAngularVelocity);

        double inputVelocity = - closedLoopVelocity;
        double inputAngularVelocity = nominalVelocity - closedLoopAngularVelocity;
        double wheel2ForwardVelocity = inputVelocity - (inputAngularVelocity * Constants.WHEEL_DISTANCE) / 2;
        double wheel1ForwardVelocity = inputVelocity + (inputAngularVelocity * Constants.WHEEL_DISTANCE) / 2;

        Assertions.assertEquals(wheel2ForwardVelocity, motorVelocities[0]);
        Assertions.assertEquals(wheel1ForwardVelocity, motorVelocities[1]);

    }


    @Test
    public void transformErrorsLargeAngleNoOffsetTest(){
        int scannerYPosition = 10;
        int scannerXPosition = 5;
        double scannerAngle = Math.toRadians(60);
        int xOffset = 0;
        int yOffset = 0;
        double[] transformedTestValues = testClass.transformErrors(scannerYPosition,scannerXPosition,scannerAngle,xOffset,yOffset);
        double yTest = transformedTestValues[0];
        double xTest = transformedTestValues[1];

        Assertions.assertEquals(yTest, -11.160254037844, .0001);
        Assertions.assertEquals(xTest, 0.66987298107781, .0001);

        int scannerYPosition2 = -10;
        int scannerXPosition2 = -5;
        double scannerAngle2 = Math.toRadians(60);
        int xOffset2 = 0;
        int yOffset2 = 0;
        double[] transformedTestValues2 = testClass.transformErrors(scannerYPosition2,scannerXPosition2,scannerAngle2,xOffset2,yOffset2);
        double yTest2 = transformedTestValues2[0];
        double xTest2 = transformedTestValues2[1];

        Assertions.assertEquals(yTest2, 11.160254037844, .0001);
        Assertions.assertEquals(xTest2, -0.66987298107781, .0001);

        int scannerYPosition3 = -10;
        int scannerXPosition3 = 5;
        double scannerAngle3 = Math.toRadians(60);
        int xOffset3 = 0;
        int yOffset3 = 0;
        double[] transformedTestValues3 = testClass.transformErrors(scannerYPosition3,scannerXPosition3,scannerAngle3,xOffset3,yOffset3);
        double yTest3 = transformedTestValues3[0];
        double xTest3 = transformedTestValues3[1];

        Assertions.assertEquals(yTest3, 6.1602540378444, .0001);
        Assertions.assertEquals(xTest3, -9.3301270189222, .0001);

        int scannerYPosition4 = 10;
        int scannerXPosition4 = -5;
        double scannerAngle4 = Math.toRadians(60);
        int xOffset4 = 0;
        int yOffset4 = 0;
        double[] transformedTestValues4 = testClass.transformErrors(scannerYPosition4,scannerXPosition4,scannerAngle4,xOffset4,yOffset4);
        double yTest4 = transformedTestValues4[0];
        double xTest4 = transformedTestValues4[1];

        Assertions.assertEquals(yTest4, -6.1602540378444, .0001);
        Assertions.assertEquals(xTest4, 9.3301270189222, .0001);

    }

    @Test
    public void transformErrorsSmallNoOffsetTest(){
        int scannerYPosition = 10;
        int scannerXPosition = 5;
        double scannerAngle = Math.toRadians(5);
        int xOffset = 0;
        int yOffset = 0;
        double[] transformedTestValues = testClass.transformErrors(scannerYPosition,scannerXPosition,scannerAngle,xOffset,yOffset);
        double yTest = transformedTestValues[0];
        double xTest = transformedTestValues[1];

        Assertions.assertEquals(yTest, -9.5261682671792, .0001);
        Assertions.assertEquals(xTest, -5.8525309179353, .0001);

        int scannerYPosition2 = -10;
        int scannerXPosition2 = -5;
        double scannerAngle2 = Math.toRadians(5);
        int xOffset2 = 0;
        int yOffset2 = 0;
        double[] transformedTestValues2 = testClass.transformErrors(scannerYPosition2,scannerXPosition2,scannerAngle2,xOffset2,yOffset2);
        double yTest2 = transformedTestValues2[0];
        double xTest2 = transformedTestValues2[1];

        Assertions.assertEquals(yTest2, 9.5261682671792, .0001);
        Assertions.assertEquals(xTest2, 5.8525309179353, .0001);

        int scannerYPosition3 = -10;
        int scannerXPosition3 = 5;
        double scannerAngle3 = Math.toRadians(5);
        int xOffset3 = 0;
        int yOffset3 = 0;
        double[] transformedTestValues3 = testClass.transformErrors(scannerYPosition3,scannerXPosition3,scannerAngle3,xOffset3,yOffset3);
        double yTest3 = transformedTestValues3[0];
        double xTest3 = transformedTestValues3[1];

        Assertions.assertEquals(yTest3, 10.397725694656, .0001);
        Assertions.assertEquals(xTest3, -4.1094160629821, .0001);

        int scannerYPosition4 = 10;
        int scannerXPosition4 = -5;
        double scannerAngle4 = Math.toRadians(5);
        int xOffset4 = 0;
        int yOffset4 = 0;
        double[] transformedTestValues4 = testClass.transformErrors(scannerYPosition4,scannerXPosition4,scannerAngle4,xOffset4,yOffset4);
        double yTest4 = transformedTestValues4[0];
        double xTest4 = transformedTestValues4[1];

        Assertions.assertEquals(yTest4, -10.397725694656, .0001);
        Assertions.assertEquals(xTest4, 4.1094160629821, .0001);
    }

    @Test
    public void transformErrorsLargeAngleOffsetTest(){

        int scannerYPosition = 10;
        int scannerXPosition = 5;
        double scannerAngle = Math.toRadians(60);
        int xOffset = 4;
        int yOffset = 5;
        double[] transformedTestValues = testClass.transformErrors(scannerYPosition,scannerXPosition,scannerAngle,xOffset,yOffset);
        double yTest = transformedTestValues[0];
        double xTest = transformedTestValues[1];

        Assertions.assertEquals(yTest, -11.160254037844 - xOffset, .0001);
        Assertions.assertEquals(xTest, 0.66987298107781 + yOffset, .0001);
    }

    @Test
    public void transformErrorsSmallAngleOffsetTest(){
        int scannerYPosition = 10;
        int scannerXPosition = 5;
        double scannerAngle = Math.toRadians(5);
        int xOffset = 0;
        int yOffset = 0;
        double[] transformedTestValues = testClass.transformErrors(scannerYPosition,scannerXPosition,scannerAngle,xOffset,yOffset);
        double yTest = transformedTestValues[0];
        double xTest = transformedTestValues[1];

        Assertions.assertEquals(yTest, -9.5261682671792 - yOffset, .0001);
        Assertions.assertEquals(xTest, -5.8525309179353 - xOffset, .0001);
    }

    @Test
    public void transformErrorsSmallNegativeAngleTest(){
        int scannerYPosition = 10;
        int scannerXPosition = 5;
        double scannerAngle = Math.toRadians(-5);
        int xOffset = 0;
        int yOffset = 0;
        double[] transformedTestValues = testClass.transformErrors(scannerYPosition,scannerXPosition,scannerAngle,xOffset,yOffset);
        double yTest = transformedTestValues[0];
        double xTest = transformedTestValues[1];

        Assertions.assertEquals(yTest, -10.397725694656, .0001);
        Assertions.assertEquals(xTest, -4.1094160629821, .0001);

        int scannerYPosition2 = -10;
        int scannerXPosition2 = -5;
        double scannerAngle2 = Math.toRadians(-5);
        int xOffset2 = 0;
        int yOffset2 = 0;
        double[] transformedTestValues2 = testClass.transformErrors(scannerYPosition2,scannerXPosition2,scannerAngle2,xOffset2,yOffset2);
        double yTest2 = transformedTestValues2[0];
        double xTest2 = transformedTestValues2[1];

        Assertions.assertEquals(yTest2, 10.397725694656, .0001);
        Assertions.assertEquals(xTest2, 4.1094160629821, .0001);

        int scannerYPosition3 = -10;
        int scannerXPosition3 = 5;
        double scannerAngle3 = Math.toRadians(-5);
        int xOffset3 = 0;
        int yOffset3 = 0;
        double[] transformedTestValues3 = testClass.transformErrors(scannerYPosition3,scannerXPosition3,scannerAngle3,xOffset3,yOffset3);
        double yTest3 = transformedTestValues3[0];
        double xTest3 = transformedTestValues3[1];

        Assertions.assertEquals(yTest3, 9.5261682671792, .0001);
        Assertions.assertEquals(xTest3, -5.8525309179353, .0001);

        int scannerYPosition4 = 10;
        int scannerXPosition4 = -5;
        double scannerAngle4 = Math.toRadians(-5);
        int xOffset4 = 0;
        int yOffset4 = 0;
        double[] transformedTestValues4 = testClass.transformErrors(scannerYPosition4,scannerXPosition4,scannerAngle4,xOffset4,yOffset4);
        double yTest4 = transformedTestValues4[0];
        double xTest4 = transformedTestValues4[1];

        Assertions.assertEquals(yTest4, -9.5261682671792, .0001);
        Assertions.assertEquals(xTest4, 5.8525309179353, .0001);
    }

    @Test
    public void transformErrorsLargeNegativeAngleTest(){
        int scannerYPosition = 10;
        int scannerXPosition = 5;
        double scannerAngle = Math.toRadians(-60);
        int xOffset = 0;
        int yOffset = 0;
        double[] transformedTestValues = testClass.transformErrors(scannerYPosition,scannerXPosition,scannerAngle,xOffset,yOffset);
        double yTest = transformedTestValues[0];
        double xTest = transformedTestValues[1];

        Assertions.assertEquals(yTest, -6.1602540378444, .0001);
        Assertions.assertEquals(xTest, -9.3301270189222, .0001);

        int scannerYPosition2 = -10;
        int scannerXPosition2 = -5;
        double scannerAngle2 = Math.toRadians(-60);
        int xOffset2 = 0;
        int yOffset2 = 0;
        double[] transformedTestValues2 = testClass.transformErrors(scannerYPosition2,scannerXPosition2,scannerAngle2,xOffset2,yOffset2);
        double yTest2 = transformedTestValues2[0];
        double xTest2 = transformedTestValues2[1];

        Assertions.assertEquals(yTest2, 6.1602540378444, .0001);
        Assertions.assertEquals(xTest2, 9.3301270189222, .0001);

        int scannerYPosition3 = -10;
        int scannerXPosition3 = 5;
        double scannerAngle3 = Math.toRadians(-60);
        int xOffset3 = 0;
        int yOffset3 = 0;
        double[] transformedTestValues3 = testClass.transformErrors(scannerYPosition3,scannerXPosition3,scannerAngle3,xOffset3,yOffset3);
        double yTest3 = transformedTestValues3[0];
        double xTest3 = transformedTestValues3[1];

        Assertions.assertEquals(yTest3, 11.160254037844, .0001);
        Assertions.assertEquals(xTest3, 0.66987298107781, .0001);

        int scannerYPosition4 = 10;
        int scannerXPosition4 = -5;
        double scannerAngle4 = Math.toRadians(-60);
        int xOffset4 = 0;
        int yOffset4 = 0;
        double[] transformedTestValues4 = testClass.transformErrors(scannerYPosition4,scannerXPosition4,scannerAngle4,xOffset4,yOffset4);
        double yTest4 = transformedTestValues4[0];
        double xTest4 = transformedTestValues4[1];

        Assertions.assertEquals(yTest4, -11.160254037844, .0001);
        Assertions.assertEquals(xTest4, -0.66987298107781, .0001);
    }

    @Test
    public void transformAnglesFirstRunTest(){
        double result1 = testClass.transformAngle(0);
        testClass.resetTerms();
        double result2 = testClass.transformAngle(1);
        testClass.resetTerms();
        double result3 = testClass.transformAngle(89);
        testClass.resetTerms();
        double result4 = testClass.transformAngle(90);
        testClass.resetTerms();
        double result5 = testClass.transformAngle(91);
        testClass.resetTerms();
        double result6 = testClass.transformAngle(179);
        testClass.resetTerms();
        double result7 = testClass.transformAngle(180);
        testClass.resetTerms();
        double result8 = testClass.transformAngle(181);
        testClass.resetTerms();
        double result9 = testClass.transformAngle(269);
        testClass.resetTerms();
        double result10 = testClass.transformAngle(270);
        testClass.resetTerms();
        double result11 = testClass.transformAngle(271);
        testClass.resetTerms();
        double result12 = testClass.transformAngle(359);
        testClass.resetTerms();
        double result13 = testClass.transformAngle(360);

        Assertions.assertEquals(result1, Math.toRadians(0),.001);
        Assertions.assertEquals(result2, Math.toRadians(-1),.001);
        Assertions.assertEquals(result3, Math.toRadians(1),.001);
        Assertions.assertEquals(result4, Math.toRadians(0),.001);
        Assertions.assertEquals(result5, Math.toRadians(-1),.001);
        Assertions.assertEquals(result6, Math.toRadians(1),.001);
        Assertions.assertEquals(result7, Math.toRadians(0),.001);
        Assertions.assertEquals(result8, Math.toRadians(-1),.001);
        Assertions.assertEquals(result9, Math.toRadians(1),.001);
        Assertions.assertEquals(result10, Math.toRadians(0),.001);
        Assertions.assertEquals(result11, Math.toRadians(-1),.001);
        Assertions.assertEquals(result12, Math.toRadians(1),.001);
        Assertions.assertEquals(result13, Math.toRadians(0),.001);

    }

    @Test
    public void transformAnglesNotFirstRunTest() {
        testClass.setClosestNinety(0);
        double result1 = testClass.transformAngle(359);
        double result2 = testClass.transformAngle(1);
        double result3 = testClass.transformAngle(45);
        double result4 = testClass.transformAngle(315);
        testClass.setClosestNinety(90);
        double result5 = testClass.transformAngle(89);
        double result6 = testClass.transformAngle(91);
        double result7 = testClass.transformAngle(135);
        double result8 = testClass.transformAngle(45);
        testClass.setClosestNinety(180);
        double result9 = testClass.transformAngle(179);
        double result10 = testClass.transformAngle(181);
        double result11 = testClass.transformAngle(225);
        double result12 = testClass.transformAngle(135);
        testClass.setClosestNinety(270);
        double result13 = testClass.transformAngle(269);
        double result14 = testClass.transformAngle(271);
        double result15 = testClass.transformAngle(315);
        double result16 = testClass.transformAngle(225);


        Assertions.assertEquals(result1, Math.toRadians(1), .001);
        Assertions.assertEquals(result2, Math.toRadians(-1), .001);
        Assertions.assertEquals(result3, Math.toRadians(-45), .001);
        Assertions.assertEquals(result4, Math.toRadians(45), .001);
        Assertions.assertEquals(result5, Math.toRadians(1), .001);
        Assertions.assertEquals(result6, Math.toRadians(-1), .001);
        Assertions.assertEquals(result7, Math.toRadians(-45), .001);
        Assertions.assertEquals(result8, Math.toRadians(45), .001);
        Assertions.assertEquals(result9, Math.toRadians(1), .001);
        Assertions.assertEquals(result10, Math.toRadians(-1), .001);
        Assertions.assertEquals(result11, Math.toRadians(-45), .001);
        Assertions.assertEquals(result12, Math.toRadians(45), .001);
        Assertions.assertEquals(result13, Math.toRadians(1), .001);
        Assertions.assertEquals(result14, Math.toRadians(-1), .001);
        Assertions.assertEquals(result15, Math.toRadians(-45), .001);
        Assertions.assertEquals(result16, Math.toRadians(45), .001);
    }
}
