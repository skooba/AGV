package com.mochilafulfillment.server.controls_logic;

import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.controls_logic.dtos.StraightLineRecord;
import com.mochilafulfillment.server.controls_logic.utils.Pid;
import com.mochilafulfillment.server.dtos.AgvInputsRecord;
import com.mochilafulfillment.server.dtos.AgvOutputsRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class StraightLineTest {
    StraightLine testClassWithMockedPid;
    Pid testPidCalculator;
    Pid mockedPidTestClass;

    @BeforeEach
    public void init(){
        testPidCalculator = new Pid(Constants.KP,Constants.KI,Constants.KD);
        mockedPidTestClass = Mockito.mock(Pid.class);
        testClassWithMockedPid = new StraightLine(mockedPidTestClass);
    }


    @Test
    public void notCorrectDirectionRunTest(){
        Pid mockedPidTestClass = Mockito.mock(Pid.class);

        StraightLine testClassWithMockedPid = new StraightLine(mockedPidTestClass);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setDirection(Constants.STOP_STRING);
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();
        testRecord = testClassWithMockedPid.run(testRecord, inputTestRecord);
        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.STOP_STRING);
        Assertions.assertEquals(testRecord, compareRecord);

        AgvOutputsRecord testRecord2 = new AgvOutputsRecord();
        testRecord2.setDirection(Constants.CCW_STRING);
        testRecord2 = testClassWithMockedPid.run(testRecord2, inputTestRecord);
        AgvOutputsRecord compareRecord2 = new AgvOutputsRecord();
        compareRecord2.setDirection(Constants.CCW_STRING);
        Assertions.assertEquals(testRecord2, compareRecord2);

        AgvOutputsRecord testRecord3 = new AgvOutputsRecord();
        testRecord3.setDirection(Constants.CW_STRING);
        testRecord3 = testClassWithMockedPid.run(testRecord3, inputTestRecord);
        AgvOutputsRecord compareRecord3 = new AgvOutputsRecord();
        compareRecord3.setDirection(Constants.CW_STRING);
        Assertions.assertEquals(testRecord3, compareRecord3);
        Mockito.verify(mockedPidTestClass,Mockito.never()).iteration(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt());

    }

    @Test
    public void tagIsFinishedRunTest() throws InterruptedException {
        Pid mockedPidTestClass = Mockito.mock(Pid.class);

        StraightLine testClassWithMockedPid = new StraightLine(mockedPidTestClass);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setDirection(Constants.FORWARDS_STRING);
        testRecord.setTagIsFinished(true);
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();
        testRecord = testClassWithMockedPid.run(testRecord, inputTestRecord);
        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.FORWARDS_STRING);
        compareRecord.setTagIsFinished(true);
        Assertions.assertEquals(testRecord, compareRecord);

        AgvOutputsRecord testRecord2 = new AgvOutputsRecord();
        testRecord2.setDirection(Constants.BACKWARDS_STRING);
        testRecord2.setTagIsFinished(true);
        testRecord2 = testClassWithMockedPid.run(testRecord2, inputTestRecord);
        AgvOutputsRecord compareRecord2 = new AgvOutputsRecord();
        compareRecord2.setDirection(Constants.BACKWARDS_STRING);
        compareRecord2.setTagIsFinished(true);
        Assertions.assertEquals(testRecord2, compareRecord2);


        Mockito.verify(mockedPidTestClass,Mockito.never()).iteration(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt());

    }

    @Test
    public void notOnTagOrTapeUnderCounterLimitRunTest() throws InterruptedException {
        Pid mockedPidTestClass = Mockito.mock(Pid.class);

        StraightLine testClassWithMockedPid = new StraightLine(mockedPidTestClass);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setDirection(Constants.FORWARDS_STRING);
        testRecord.setNominalVelocity(50);
        testRecord.setTagIsFinished(false);
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();
        inputTestRecord.setOnTape(false);
        inputTestRecord.setTagId(0);
        testClassWithMockedPid.straightLineRecord.setZeroYCounts(Constants.ZERO_Y_COUNTS_CUTOFF - 1);
        testRecord = testClassWithMockedPid.run(testRecord, inputTestRecord);
        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.FORWARDS_STRING);
        compareRecord.setNominalVelocity(50);
        compareRecord.setTagIsFinished(false);
        StraightLineRecord straightLineCompareRecord = new StraightLineRecord();
        straightLineCompareRecord.setZeroYCounts(Constants.ZERO_Y_COUNTS_CUTOFF);
        Assertions.assertEquals(straightLineCompareRecord,testClassWithMockedPid.straightLineRecord);
        Assertions.assertEquals(testRecord, compareRecord);

        StraightLine testClass = new StraightLine(testPidCalculator);
        AgvOutputsRecord testRecord2 = new AgvOutputsRecord();
        testRecord2.setDirection(Constants.BACKWARDS_STRING);
        testRecord2.setNominalVelocity(0);
        testRecord2.setTagIsFinished(false);
        AgvInputsRecord inputTestRecord2 = new AgvInputsRecord();
        inputTestRecord2.setOnTape(false);
        inputTestRecord2.setTagId(0);
        testClass.straightLineRecord.setZeroYCounts(0);
        testRecord2 = testClass.run(testRecord2, inputTestRecord2);
        AgvOutputsRecord compareRecord2 = new AgvOutputsRecord();
        compareRecord2.setDirection(Constants.BACKWARDS_STRING);
        compareRecord2.setNominalVelocity(0);
        compareRecord2.setTagIsFinished(false);
        StraightLineRecord straightLineCompareRecord2 = new StraightLineRecord();
        straightLineCompareRecord2.setZeroYCounts(1);
        Assertions.assertEquals(straightLineCompareRecord2,testClass.straightLineRecord);
        Assertions.assertEquals(testRecord2, compareRecord2);
        Mockito.verify(mockedPidTestClass,Mockito.never()).iteration(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt());

    }

    @Test
    public void notOnTagOrTapeOverCounterLimitRunTest() throws InterruptedException {
        Pid mockedPidTestClass = Mockito.mock(Pid.class);

        StraightLine testClassWithMockedPid = new StraightLine(mockedPidTestClass);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setDirection(Constants.FORWARDS_STRING);
        testRecord.setNominalVelocity(50);
        testRecord.setTagIsFinished(false);
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();
        inputTestRecord.setOnTape(false);
        inputTestRecord.setTagId(0);
        testClassWithMockedPid.straightLineRecord.setZeroYCounts((int)(Constants.ZERO_Y_COUNTS_CUTOFF / (Constants.MOTOR_CONTROLLER_TIMEOUT / 1000.0)));
        testRecord = testClassWithMockedPid.run(testRecord, inputTestRecord);
        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.STOP_STRING);
        compareRecord.setNominalVelocity(0);
        compareRecord.setTagIsFinished(false);
        StraightLineRecord straightLineCompareRecord = new StraightLineRecord();
        straightLineCompareRecord.setZeroYCounts((int)(Constants.ZERO_Y_COUNTS_CUTOFF / (Constants.MOTOR_CONTROLLER_TIMEOUT / 1000.0)));
        Assertions.assertEquals(straightLineCompareRecord,testClassWithMockedPid.straightLineRecord);
        Assertions.assertEquals(testRecord, compareRecord);
        Mockito.verify(mockedPidTestClass,Mockito.never()).iteration(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt());

    }


    @Test
    public void agvFoundTapeRunTest() throws InterruptedException {
        Pid mockedPidTestClass = Mockito.mock(Pid.class);

        StraightLine testClassWithMockedPid = new StraightLine(mockedPidTestClass);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setTagIsFinished(false);
        testRecord.setDirection(Constants.FORWARDS_STRING);
        testRecord.setNominalVelocity(50);
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();
        inputTestRecord.setOnTape(true);
        inputTestRecord.setTagId(0);
        inputTestRecord.setY(Constants.BACK_ON_TAPE_Y_POSITION_MINIMUM+1);
        testClassWithMockedPid.straightLineRecord.setZeroYCounts(Constants.ZERO_Y_COUNTS_CUTOFF -1 );
        testRecord = testClassWithMockedPid.run(testRecord, inputTestRecord);
        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.FORWARDS_STRING);
        compareRecord.setNominalVelocity(50);
        compareRecord.setTagIsFinished(false);
        StraightLineRecord straightLineCompareRecord = new StraightLineRecord();
        straightLineCompareRecord.setZeroYCounts(0);
        Assertions.assertEquals(straightLineCompareRecord,testClassWithMockedPid.straightLineRecord);
        Assertions.assertEquals(testRecord, compareRecord);
        Mockito.verify(mockedPidTestClass,Mockito.never()).iteration(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt());

    }


    @Test
    public void nominalVelocityIs0OnTagOrTapeRun() throws InterruptedException {
        Pid mockedPidTestClass = Mockito.mock(Pid.class);

        StraightLine testClassWithMockedPid = new StraightLine(mockedPidTestClass);

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setDirection(Constants.FORWARDS_STRING);
        testRecord.setNominalVelocity(0);
        testRecord.setTagIsFinished(false);
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();
        inputTestRecord.setOnTape(true);
        inputTestRecord.setTagId(0);
        testClassWithMockedPid.straightLineRecord.setZeroYCounts((int)(Constants.ZERO_Y_COUNTS_CUTOFF / (Constants.MOTOR_CONTROLLER_TIMEOUT / 1000.0)) - 1);
        testRecord = testClassWithMockedPid.run(testRecord, inputTestRecord);
        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.FORWARDS_STRING);
        compareRecord.setNominalVelocity(0);
        compareRecord.setTagIsFinished(false);
        StraightLineRecord straightLineCompareRecord = new StraightLineRecord();
        straightLineCompareRecord.setZeroYCounts((int)(Constants.ZERO_Y_COUNTS_CUTOFF / (Constants.MOTOR_CONTROLLER_TIMEOUT / 1000.0)) - 1);
        Assertions.assertEquals(straightLineCompareRecord,testClassWithMockedPid.straightLineRecord);
        Assertions.assertEquals(testRecord, compareRecord);

        StraightLine testClass = new StraightLine(testPidCalculator);
        AgvOutputsRecord testRecord2 = new AgvOutputsRecord();
        testRecord2.setDirection(Constants.BACKWARDS_STRING);
        testRecord2.setNominalVelocity(0);
        testRecord2.setTagIsFinished(false);
        AgvInputsRecord inputTestRecord2 = new AgvInputsRecord();
        inputTestRecord2.setOnTape(false);
        inputTestRecord2.setTagId(20);
        testClass.straightLineRecord.setZeroYCounts(0);
        testRecord2 = testClass.run(testRecord2, inputTestRecord2);
        AgvOutputsRecord compareRecord2 = new AgvOutputsRecord();
        compareRecord2.setDirection(Constants.BACKWARDS_STRING);
        compareRecord2.setNominalVelocity(0);
        compareRecord2.setTagIsFinished(false);
        StraightLineRecord straightLineCompareRecord2 = new StraightLineRecord();
        straightLineCompareRecord2.setZeroYCounts(0);
        Assertions.assertEquals(straightLineCompareRecord2,testClass.straightLineRecord);
        Assertions.assertEquals(testRecord2, compareRecord2);
        Mockito.verify(mockedPidTestClass,Mockito.never()).iteration(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt());

    }

    @Test
    public void nominalVelocityNot0ZeroYCountsNot0OnTagOrTapeRun(){
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setDirection(Constants.FORWARDS_STRING);
        testRecord.setNominalVelocity(Constants.ON_STOP_TAG_VELOCITY);
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();
        inputTestRecord.setOnTape(true);
        inputTestRecord.setTagId(0);
        inputTestRecord.setTapeAngle(0);
        testClassWithMockedPid.straightLineRecord.setZeroYCounts(Constants.ZERO_Y_COUNTS_CUTOFF-1);
        testRecord = testClassWithMockedPid.run(testRecord, inputTestRecord);


        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.FORWARDS_STRING);
        compareRecord.setNominalVelocity(Constants.ON_STOP_TAG_VELOCITY);
        StraightLineRecord straightLineCompareRecord = new StraightLineRecord();
        straightLineCompareRecord.setZeroYCounts(Constants.ZERO_Y_COUNTS_CUTOFF-1);
        Assertions.assertEquals(straightLineCompareRecord,testClassWithMockedPid.straightLineRecord);
        Assertions.assertEquals(testRecord, compareRecord);
        Mockito.verify(mockedPidTestClass,Mockito.never()).iteration(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt());

    }

    @Test
    public void nominalVelocityNot0ZeroYCounts0OnTagOrTapeRun() throws InterruptedException {

        Mockito.when(mockedPidTestClass.iteration(Mockito.anyDouble(),Mockito.anyDouble(),Mockito.anyDouble(),Mockito.anyInt())).thenReturn(new double[] {1,0,0});

        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setDirection(Constants.FORWARDS_STRING);
        testRecord.setNominalVelocity(Constants.ON_STOP_TAG_VELOCITY);
        testRecord.setTagIsFinished(false);
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();
        inputTestRecord.setOnTape(true);
        inputTestRecord.setTagId(0);
        inputTestRecord.setY(Constants.BACK_ON_TAPE_Y_POSITION_MINIMUM + 1);
        testClassWithMockedPid.straightLineRecord.setZeroYCounts(0);
        testRecord = testClassWithMockedPid.run(testRecord, inputTestRecord);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.FORWARDS_STRING);
        compareRecord.setNominalVelocity(Constants.ON_STOP_TAG_VELOCITY);
        compareRecord.setMotorVelocityRatio(1);
        compareRecord.setTagIsFinished(false);
        StraightLineRecord straightLineCompareRecord = new StraightLineRecord();
        straightLineCompareRecord.setZeroYCounts(0);
        Assertions.assertEquals(straightLineCompareRecord,testClassWithMockedPid.straightLineRecord);
        Assertions.assertEquals(testRecord, compareRecord);
        Mockito.verify(mockedPidTestClass).iteration(Mockito.anyDouble(),Mockito.anyDouble(),Mockito.anyDouble(),Mockito.anyInt());
    }
}
