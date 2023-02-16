package com.mochilafulfillment.server.controls_logic.utils;

import com.mochilafulfillment.server.agv_utils.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

public class PidTest {

    Pid testClass;
    @BeforeEach
    public void init(){
        testClass = new Pid(Constants.KP, Constants.KI, Constants.KD);
    }

    @Test
    public void modifyYPositionTest(){
        Assertions.assertEquals(testClass.modifyYPosition(5,Math.toRadians(0), 0, 0),5,.01);
        Assertions.assertEquals(testClass.modifyYPosition(-5,Math.toRadians(30), 0,0),-4.33, .01);
        Assertions.assertEquals(testClass.modifyYPosition(-5,Math.toRadians(330), 0, 0), -4.33, .01);
        Assertions.assertEquals(testClass.modifyYPosition(6,Math.toRadians(30), -12, 0), 11.196, .01);
        Assertions.assertEquals(testClass.modifyYPosition(-4,Math.toRadians(-30), 11, -5),-2.29,.01);
        Assertions.assertEquals(testClass.modifyYPosition(-2,Math.toRadians(30), 5,12),6.160,.01);

    }

    @Test
    public void calculateProportionalTest(){
        Assertions.assertEquals(testClass.calculateProportional(10), -10);
        Assertions.assertEquals(testClass.calculateProportional(-10), 10);

    }

    @Test
    public void calculateDerivativeTest(){
        testClass.setLastProportionalError(10);
        testClass.setDt(.1);
        Assertions.assertEquals(testClass.calculateDerivative(8), 0);
        testClass.setLastTime(0);
        Assertions.assertEquals(testClass.calculateDerivative(8), 20);
        testClass.setDt(.01);
        Assertions.assertEquals(testClass.calculateDerivative(-8), 1800);
        testClass.setLastProportionalError(-5.1);
        Assertions.assertEquals(testClass.calculateDerivative(-1.1), -400,.0001);

    }

    @Test
    public void calculateStandardIntegralTest(){
        testClass.setLastIntegralError(11.5);
        Assertions.assertEquals(testClass.calculateStandardIntegral(-2.2), 9.3);
    }

    @Test
    public void calculateTruncatedIntegralTest(){
        testClass.setLastIntegralError(11.5);
        Assertions.assertEquals(testClass.calculateTruncatedIntegral(-2.2), 9.3);
        testClass.setLastIntegralError(Constants.MAX_INTEGRAL_ALLOWED);
        Assertions.assertEquals(testClass.calculateTruncatedIntegral(1), Constants.MAX_INTEGRAL_ALLOWED);
        testClass.setLastIntegralError(Constants.MIN_INTEGRAL_ALLOWED);
        Assertions.assertEquals(testClass.calculateTruncatedIntegral(-1), Constants.MIN_INTEGRAL_ALLOWED);

    }

    @Test
    public void calculateScaledIntegralTest(){
        List<Double> testList = new ArrayList<>(
                List.of(5., 10., 6., -4.5, -2., 0.5));
        testClass.setErrorArray(testList);
        testClass.setKi_loops(10);
        Assertions.assertEquals(testClass.calculateScaledIntegral(-.7), (-.7 + 5.+10.+6.+-4.5+-2.+0.5)/(10 * Constants.INTEGRAL_WEIGHT_FACTOR),.0001);
        List<Double> testList2 = new ArrayList<>(
                List.of(5., 10., 6., -4.5, -2., 0.5));
        testClass.setErrorArray(testList2);
        testClass.setKi_loops(5);
        Assertions.assertEquals(testClass.calculateScaledIntegral(-.7), (-.7 + 5.+10.+6.+-4.5)/(5 * Constants.INTEGRAL_WEIGHT_FACTOR),.0001);
    }

    @Test
    public void iterationStandardIntegralTypeTest(){
        Pid mockedTestClass = Mockito.spy(testClass);

        Mockito.when(mockedTestClass.calculateProportional(0)).thenReturn(10000.00001/Constants.KP);
        Mockito.when(mockedTestClass.calculateDerivative(10000.00001/Constants.KP)).thenReturn(1000.0001/Constants.KD);
        Mockito.when(mockedTestClass.calculateStandardIntegral(10000.00001/Constants.KP)).thenReturn(100.001/Constants.KI);

        testClass.setLastProportionalError(0);
        testClass.setLastIntegralError(0);
        testClass.setIntegralType(0);
        double[] testResult = mockedTestClass.iteration(0,0,0, 1);
        Assertions.assertEquals(testResult[0], 11100.00111 + 1);//+1 to account for the nominalVelocity
    }

    @Test
    public void iterationTruncatedIntegralTypeTest(){
        Pid mockedTestClass = Mockito.spy(testClass);
        Mockito.when(mockedTestClass.calculateProportional(0)).thenReturn(10000.00001/Constants.KP);
        Mockito.when(mockedTestClass.calculateDerivative(10000.00001/Constants.KP)).thenReturn(1000.0001/Constants.KD);
        Mockito.when(mockedTestClass.calculateTruncatedIntegral(10000.00001/Constants.KP)).thenReturn(10.01/Constants.KI);

        mockedTestClass.setLastProportionalError(10);
        mockedTestClass.setLastIntegralError(10);
        mockedTestClass.setIntegralType(1);
        double[] testResult = mockedTestClass.iteration(0,0,0, 1);
        Assertions.assertEquals(testResult[0], 11010.01011 + 1);//+1 to account for the nominalVelocity
    }

    @Test
    public void iterationScaledIntegralTypeTest(){
        Pid mockedTestClass = Mockito.spy(testClass);
        Mockito.when(mockedTestClass.calculateProportional(0)).thenReturn(10000.00001/Constants.KP);
        Mockito.when(mockedTestClass.calculateDerivative(10000.00001/Constants.KP)).thenReturn(1000.0001/Constants.KD);
        Mockito.when(mockedTestClass.calculateScaledIntegral(10000.00001/Constants.KP)).thenReturn(1.1/Constants.KI);

        mockedTestClass.setLastProportionalError(-50);
        mockedTestClass.setLastIntegralError(50);
        mockedTestClass.setIntegralType(2);
        Assertions.assertEquals(mockedTestClass.iteration(0,0,0, 1)[0], 11001.10011 + 1);//+1 to account for the nominalVelocity

        mockedTestClass.setLastProportionalError(1);
        mockedTestClass.setLastIntegralError(-30);
        mockedTestClass.setIntegralType(2);
        double[] testResult = mockedTestClass.iteration(0,0,0, 1);
        Assertions.assertEquals(testResult[0], 11001.10011 + 1); //+1 to account for the nominalVelocity
    }

    @Test
    public void resetPidTest(){
        testClass.setLastTime(100);
        testClass.setLastIntegralError(789);
        testClass.setLastProportionalError(123);
        testClass.resetTerms();
        Assertions.assertEquals(0, testClass.getLastIntegralError());
        Assertions.assertEquals(0, testClass.getLastIntegralError());
        Assertions.assertEquals(0, testClass.getLastTime());

    }

    @Test
    public void transformErrorTest(){
        int scannerYPosition = 1;
        int scannerXPosition = 2;
        double scannerAngle = 3;
        int xOffset = 4;
        int yOffset = 5;
        Pid spyTestClass = Mockito.spy(testClass);
        spyTestClass.transformErrors(scannerYPosition, scannerXPosition, scannerAngle, xOffset, yOffset);

        Mockito.verify(spyTestClass).transformErrors(scannerYPosition, scannerXPosition, scannerAngle, xOffset, yOffset);
    }
}
