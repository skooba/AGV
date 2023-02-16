package com.mochilafulfillment.server.controls_logic;

import com.mochilafulfillment.server.agv_utils.ByteToolbox;
import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.agv_utils.Exceptions.PGVException;
import com.mochilafulfillment.server.controls_logic.dtos.PathRecord;
import com.mochilafulfillment.server.controls_logic.dtos.StopOnTagRecord;
import com.mochilafulfillment.server.dtos.AgvInputsRecord;
import com.mochilafulfillment.server.dtos.AgvOutputsRecord;
import com.mochilafulfillment.server.position_scanner.PositionScannerConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class StopOnTagTest {

    StopOnTag testClass;

    @BeforeEach
    public void init(){
        testClass = new StopOnTag();
    }

    @Test
    public void traversingColumnsTest(){

        int response = testClass.traversingColumns(40, 40, 50, 40);
        int response2 = testClass.traversingColumns(50, 40,50, 50);
        int response3 = testClass.traversingColumns(100, 10, 10, 10);
        int response4 = testClass.traversingColumns(10, 20, 20, 10);


        Assertions.assertEquals(response, 10);
        Assertions.assertEquals(response2, -1);
        Assertions.assertEquals(response3, 90);
        Assertions.assertEquals(response4, -1);

    }

    @Test
    public void traversingRowsTest(){

        int response = testClass.traversingRows(40, 40, 40, 50);
        int response2 = testClass.traversingRows(40, 50,50, 50);
        int response3 = testClass.traversingRows(10, 100, 10, 10);
        int response4 = testClass.traversingRows(20, 10, 10, 20);


        Assertions.assertEquals(response, 10);
        Assertions.assertEquals(response2, -1);
        Assertions.assertEquals(response3, 90);
        Assertions.assertEquals(response4, -1);

    }

    @Test
    public void howManyTagsAwayTest(){

        StopOnTag.OnTag innerTestClass = testClass.new OnTag();

        Assertions.assertEquals(innerTestClass.howManyTagsAway(ByteToolbox.byteArrayToInt(new int[]{0, 0, 50, 40}, PositionScannerConstants.BITS_PER_BYTE), 10,50), 30);
        Assertions.assertEquals(innerTestClass.howManyTagsAway(ByteToolbox.byteArrayToInt(new int[]{0, 0, 50, 100}, PositionScannerConstants.BITS_PER_BYTE), 99,50), 1);
        Assertions.assertEquals(innerTestClass.howManyTagsAway(ByteToolbox.byteArrayToInt(new int[]{0, 0, 10, 40}, PositionScannerConstants.BITS_PER_BYTE), 40,60), 50);
        Assertions.assertEquals(innerTestClass.howManyTagsAway(ByteToolbox.byteArrayToInt(new int[]{0, 0, 70, 40}, PositionScannerConstants.BITS_PER_BYTE), 40,69), 1);
        Assertions.assertEquals(innerTestClass.howManyTagsAway(ByteToolbox.byteArrayToInt(new int[]{0, 0, 70, 40}, PositionScannerConstants.BITS_PER_BYTE), 40,70), -1);
        Assertions.assertEquals(innerTestClass.howManyTagsAway(ByteToolbox.byteArrayToInt(new int[]{0, 0, 70, 40}, PositionScannerConstants.BITS_PER_BYTE), 41,71), -1);

    }

    @Test
    public void howManyTagsAwayOffGridTest(){
        StopOnTag.OnTag innerTestClass = testClass.new OnTag();

        Assertions.assertEquals(innerTestClass.howManyTagsAway(ByteToolbox.byteArrayToInt(new int[]{0, 1, 50, 40}, PositionScannerConstants.BITS_PER_BYTE), 10,50), -1);
    }

    @Test
    public void notCenteredNotCounterLimitOnStopTagTest() throws PGVException, InterruptedException {
        StopOnTag.OnTag innerTestClass = testClass.new OnTag();
        StopOnTagRecord testStopOnTagRecord = new StopOnTagRecord();
        testStopOnTagRecord.setXCounter(Constants.ZERO_X_COUNTS_CUTOFF - 1);
        testClass.setStopOnTagRecord(testStopOnTagRecord);
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setDirection(Constants.FORWARDS_STRING);
        testRecord.setNominalVelocity(Constants.ON_STOP_TAG_VELOCITY);
        testRecord.setNominalAccel(Constants.ON_STOP_TAG_ACCEL);
        testRecord = innerTestClass.onStopTag(testRecord, Constants.X_POSITION_STOP_OFFSET - 30);
        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setNominalVelocity(Constants.ON_STOP_TAG_VELOCITY);
        compareRecord.setNominalAccel(Constants.ON_STOP_TAG_ACCEL);
        compareRecord.setDirection(Constants.FORWARDS_STRING);
        Assertions.assertEquals(testRecord, compareRecord);

        testClass = new StopOnTag();
        StopOnTag.OnTag innerTestClass2 = testClass.new OnTag();
        StopOnTagRecord testStopOnTagRecord2 = new StopOnTagRecord();
        testStopOnTagRecord2.setXCounter(0);
        testClass.setStopOnTagRecord(testStopOnTagRecord2);
        AgvOutputsRecord testRecord2 = new AgvOutputsRecord();
        testRecord2.setDirection(Constants.FORWARDS_STRING);
        testRecord2 = innerTestClass2.onStopTag(testRecord2, Constants.X_POSITION_STOP_OFFSET - 1);
        Assertions.assertEquals(testRecord2, compareRecord);
    }

    @Test
    public void notCenteredCounterLimitsOnStopTagTest() throws PGVException {
        StopOnTag.OnTag innerTestClass = testClass.new OnTag();
        StopOnTagRecord testStopOnTagRecord = new StopOnTagRecord();
        testStopOnTagRecord.setXCounter(Constants.ZERO_X_COUNTS_CUTOFF);
        testClass.setStopOnTagRecord(testStopOnTagRecord);
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setDirection(Constants.FORWARDS_STRING);
        Assertions.assertThrows(PGVException.class, () -> innerTestClass.onStopTag(testRecord, Constants.X_POSITION_STOP_OFFSET - 20));
    }

    @Test
    public void centeredOnStopTagTest() throws PGVException, InterruptedException {
        StopOnTag.OnTag innerTestClass = testClass.new OnTag();

        StopOnTagRecord testStopOnTagRecord = new StopOnTagRecord();
        testClass.setStopOnTagRecord(testStopOnTagRecord);
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setDirection(Constants.FORWARDS_STRING);
        testRecord = innerTestClass.onStopTag(testRecord, Constants.X_POSITION_STOP_OFFSET - 0);
        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setDirection(Constants.STOP_STRING);
        compareRecord.setTagIsFinished(true);
        compareRecord.setNominalVelocity(Constants.ON_STOP_TAG_VELOCITY);
        compareRecord.setNominalAccel(Constants.ON_STOP_TAG_ACCEL);
        Assertions.assertEquals(testRecord, compareRecord);
    }

    @Test
    public void moreThanOneTagAwayOnTagRunTest() throws PGVException, InterruptedException {
        StopOnTag.OnTag innerTestClass = testClass.new OnTag();
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        StopOnTagRecord testStopOnTagRecord = new StopOnTagRecord();
        testStopOnTagRecord.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 50,40},7));
        testClass.setStopOnTagRecord(testStopOnTagRecord);
        testRecord = innerTestClass.run(testRecord,ByteToolbox.byteArrayToInt(new int[] {0, 0, 30, 40},7),40, 30, Constants.X_POSITION_STOP_OFFSET - 20);
        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        StopOnTagRecord stopOnTagCompareRecord = new StopOnTagRecord();
        stopOnTagCompareRecord.setLastId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 30,40},7));
        stopOnTagCompareRecord.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 50,40},7));
        Assertions.assertEquals(testRecord, compareRecord);
        Assertions.assertEquals(stopOnTagCompareRecord, testClass.getStopOnTagRecord());

        testClass = new StopOnTag();
        StopOnTag.OnTag innerTestClass2 = testClass.new OnTag();
        AgvOutputsRecord testRecord2 = new AgvOutputsRecord();
        StopOnTagRecord testStopOnTagRecord2 = new StopOnTagRecord();
        testStopOnTagRecord2.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 50,40},7));
        testClass.setStopOnTagRecord(testStopOnTagRecord2);
        testRecord2 = innerTestClass2.run(testRecord2,ByteToolbox.byteArrayToInt(new int[] {0, 0, 80, 40},7),40, 80, Constants.X_POSITION_STOP_OFFSET - 20);
        AgvOutputsRecord compareRecord2 = new AgvOutputsRecord();
        StopOnTagRecord stopOnTagCompareRecord2 = new StopOnTagRecord();
        stopOnTagCompareRecord2.setLastId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 80,40},7));
        stopOnTagCompareRecord2.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 50,40},7));
        Assertions.assertEquals(testRecord2, compareRecord2);
        Assertions.assertEquals(stopOnTagCompareRecord2, testClass.getStopOnTagRecord());

        StopOnTag.OnTag innerTestClass3 = testClass.new OnTag();
        AgvOutputsRecord testRecord3 = new AgvOutputsRecord();
        StopOnTagRecord testStopOnTagRecord3 = new StopOnTagRecord();
        testStopOnTagRecord3.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 50,70},7));
        testClass.setStopOnTagRecord(testStopOnTagRecord3);
        testRecord3 = innerTestClass3.run(testRecord3,ByteToolbox.byteArrayToInt(new int[] {0, 0, 50, 40},7),40, 50, -Constants.X_POSITION_STOP_OFFSET + 20);
        AgvOutputsRecord compareRecord3 = new AgvOutputsRecord();
        StopOnTagRecord stopOnTagCompareRecord3 = new StopOnTagRecord();
        stopOnTagCompareRecord3.setLastId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 50,40},7));
        stopOnTagCompareRecord3.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 50,70},7));
        Assertions.assertEquals(testRecord3, compareRecord3);
        Assertions.assertEquals(stopOnTagCompareRecord3, testClass.getStopOnTagRecord());

        testClass = new StopOnTag();
        StopOnTag.OnTag innerTestClass4 = testClass.new OnTag();
        AgvOutputsRecord testRecord4 = new AgvOutputsRecord();
        StopOnTagRecord testStopOnTagRecord4 = new StopOnTagRecord();
        testStopOnTagRecord4.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 50,40},7));
        testClass.setStopOnTagRecord(testStopOnTagRecord4);
        testRecord4 = innerTestClass4.run(testRecord4,ByteToolbox.byteArrayToInt(new int[] {0, 0, 50, 70},7),70, 50, Constants.X_POSITION_STOP_OFFSET - 20);
        AgvOutputsRecord compareRecord4 = new AgvOutputsRecord();
        StopOnTagRecord stopOnTagCompareRecord4 = new StopOnTagRecord();
        stopOnTagCompareRecord4.setLastId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 50,70},7));
        stopOnTagCompareRecord4.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 50,40},7));
        Assertions.assertEquals(testRecord4, compareRecord4);
        Assertions.assertEquals(stopOnTagCompareRecord4, testClass.getStopOnTagRecord());
    }

    @Test
    public void oneTagAwayOnTagRunTest() throws PGVException, InterruptedException {
        StopOnTag.OnTag innerTestClass = testClass.new OnTag();
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        StopOnTagRecord testStopOnTagRecord = new StopOnTagRecord();
        testStopOnTagRecord.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 5,4},7));
        testClass.setStopOnTagRecord(testStopOnTagRecord);
        testRecord = innerTestClass.run(testRecord,ByteToolbox.byteArrayToInt(new int[] {0, 0, 4, 4},7),4, 4, Constants.X_POSITION_STOP_OFFSET - 12);
        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setNominalAccel(Constants.ONE_TAG_AWAY_ACCEL);
        compareRecord.setNominalVelocity(Constants.ONE_TAG_AWAY_VELOCITY);
        StopOnTagRecord stopOnTagCompareRecord = new StopOnTagRecord();
        stopOnTagCompareRecord.setLastId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 4,4},7));
        stopOnTagCompareRecord.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 5,4},7));
        stopOnTagCompareRecord.setOneTagAway(true);
        Assertions.assertEquals(testRecord, compareRecord);
        Assertions.assertEquals(stopOnTagCompareRecord, testClass.getStopOnTagRecord());

        testClass = new StopOnTag();
        StopOnTag.OnTag innerTestClass2 = testClass.new OnTag();
        AgvOutputsRecord testRecord2 = new AgvOutputsRecord();
        testRecord2.setNominalAccel(Constants.ONE_TAG_AWAY_ACCEL);
        testRecord2.setNominalVelocity(Constants.ONE_TAG_AWAY_VELOCITY);
        StopOnTagRecord testStopOnTagRecord2 = new StopOnTagRecord();
        testStopOnTagRecord2.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 11,7},7));
        testStopOnTagRecord2.setLastId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 12, 7}, 7));
        testStopOnTagRecord2.setOneTagAway(true);
        testClass.setStopOnTagRecord(testStopOnTagRecord2);
        testRecord2 = innerTestClass2.run(testRecord2,ByteToolbox.byteArrayToInt(new int[] {0, 0, 12, 7},7),7, 12, -Constants.X_POSITION_STOP_OFFSET + 1);
        StopOnTagRecord stopOnTagCompareRecord2 = new StopOnTagRecord();
        stopOnTagCompareRecord2.setLastId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 12,7},7));
        stopOnTagCompareRecord2.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 11,7},7));
        stopOnTagCompareRecord2.setOneTagAway(true);
        Assertions.assertEquals(testRecord2, compareRecord);
        Assertions.assertEquals(stopOnTagCompareRecord2, testClass.getStopOnTagRecord());

        testClass = new StopOnTag();
        StopOnTag.OnTag innerTestClass3 = testClass.new OnTag();
        AgvOutputsRecord testRecord3 = new AgvOutputsRecord();
        StopOnTagRecord testStopOnTagRecord3 = new StopOnTagRecord();
        testStopOnTagRecord3.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 17,2},7));
        testClass.setStopOnTagRecord(testStopOnTagRecord3);
        testRecord3 = innerTestClass3.run(testRecord3,ByteToolbox.byteArrayToInt(new int[] {0, 0, 17, 1},7),1, 17, Constants.X_POSITION_STOP_OFFSET - 1);
        StopOnTagRecord stopOnTagCompareRecord3 = new StopOnTagRecord();
        stopOnTagCompareRecord3.setLastId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 17,1},7));
        stopOnTagCompareRecord3.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 17,2},7));
        stopOnTagCompareRecord3.setOneTagAway(true);
        Assertions.assertEquals(testRecord3, compareRecord);
        Assertions.assertEquals(stopOnTagCompareRecord3, testClass.getStopOnTagRecord());

        testClass = new StopOnTag();
        StopOnTag.OnTag innerTestClass4 = testClass.new OnTag();
        AgvOutputsRecord testRecord4 = new AgvOutputsRecord();
        testRecord2.setNominalAccel(Constants.ONE_TAG_AWAY_ACCEL);
        testRecord2.setNominalVelocity(Constants.ONE_TAG_AWAY_VELOCITY);

        StopOnTagRecord testStopOnTagRecord4 = new StopOnTagRecord();
        testStopOnTagRecord4.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 120,1},7));
        testStopOnTagRecord4.setLastId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 12, 7}, 7));
        testStopOnTagRecord4.setOneTagAway(true);
        testClass.setStopOnTagRecord(testStopOnTagRecord4);
        testRecord4 = innerTestClass4.run(testRecord4,ByteToolbox.byteArrayToInt(new int[] {0, 0, 120, 2},7),2, 120, Constants.X_POSITION_STOP_OFFSET - 85);
        AgvOutputsRecord compareRecord4 = new AgvOutputsRecord();
        StopOnTagRecord stopOnTagCompareRecord4 = new StopOnTagRecord();
        stopOnTagCompareRecord4.setLastId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 120,2},7));
        stopOnTagCompareRecord4.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 0, 120,1},7));
        stopOnTagCompareRecord4.setOneTagAway(true);
        Assertions.assertEquals(testRecord4, compareRecord4);
        Assertions.assertEquals(stopOnTagCompareRecord4, testClass.getStopOnTagRecord());
    }

    @Test
    public void onStopTagCenterNotFinishedTest() throws PGVException, InterruptedException {
        StopOnTag.OnTag innerTestClass = testClass.new OnTag();
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        StopOnTagRecord testStopOnTagRecord = new StopOnTagRecord();
        testStopOnTagRecord.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 1, 5, 4},7));
        testClass.setStopOnTagRecord(testStopOnTagRecord);
        testRecord = innerTestClass.run(testRecord,ByteToolbox.byteArrayToInt(new int[] {0, 1, 5, 4},7),4, 5, Constants.X_POSITION_STOP_OFFSET - 12);
        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setNominalAccel(Constants.ON_STOP_TAG_ACCEL);
        compareRecord.setNominalVelocity(Constants.ON_STOP_TAG_VELOCITY);
        StopOnTagRecord stopOnTagCompareRecord = new StopOnTagRecord();
        stopOnTagCompareRecord.setLastId(ByteToolbox.byteArrayToInt(new int[] {0, 1, 5,4},7));
        stopOnTagCompareRecord.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 1, 5,4},7));
        stopOnTagCompareRecord.setXCounter(1);
        Assertions.assertEquals(testRecord, compareRecord);
        Assertions.assertEquals(stopOnTagCompareRecord, testClass.getStopOnTagRecord());

        testClass = new StopOnTag();
        StopOnTag.OnTag innerTestClass2 = testClass.new OnTag();
        AgvOutputsRecord testRecord2 = new AgvOutputsRecord();

        StopOnTagRecord testStopOnTagRecord2 = new StopOnTagRecord();
        testStopOnTagRecord2.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 1, 17, 2},7));
        testClass.setStopOnTagRecord(testStopOnTagRecord2);
        testRecord2 = innerTestClass2.run(testRecord2,ByteToolbox.byteArrayToInt(new int[] {0, 1, 17, 2},7),2, 17, Constants.X_POSITION_STOP_OFFSET-1);
        StopOnTagRecord stopOnTagCompareRecord2 = new StopOnTagRecord();

        Assertions.assertEquals(testRecord2, compareRecord);

        stopOnTagCompareRecord2.setLastId(ByteToolbox.byteArrayToInt(new int[] {0, 1, 17,2},7));
        stopOnTagCompareRecord2.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 1, 17,2},7));
        stopOnTagCompareRecord2.setXCounter(1);
        Assertions.assertEquals(stopOnTagCompareRecord2, testClass.getStopOnTagRecord());
    }

    @Test
    public void onStopTagCenterFinishedTest() throws PGVException, InterruptedException {
        StopOnTag.OnTag innerTestClass = testClass.new OnTag();
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        testRecord.setNominalVelocity(Constants.ON_STOP_TAG_VELOCITY);
        testRecord.setNominalAccel(Constants.ON_STOP_TAG_ACCEL);

        StopOnTagRecord testStopOnTagRecord = new StopOnTagRecord();
        testStopOnTagRecord.setXCounter(1);
        testStopOnTagRecord.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 1, 5, 4},7));
        testClass.setStopOnTagRecord(testStopOnTagRecord);
        testRecord = innerTestClass.run(testRecord,ByteToolbox.byteArrayToInt(new int[] {0, 1, 5, 4},7),4, 5, Constants.X_POSITION_STOP_OFFSET + 12);
        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        compareRecord.setNominalAccel(Constants.ON_STOP_TAG_ACCEL);
        compareRecord.setNominalVelocity(Constants.ON_STOP_TAG_VELOCITY);
        compareRecord.setDirection(Constants.STOP_STRING);
        compareRecord.setTagIsFinished(true);
        StopOnTagRecord stopOnTagCompareRecord = new StopOnTagRecord();
        Assertions.assertEquals(testRecord, compareRecord);
        Assertions.assertEquals(stopOnTagCompareRecord, testClass.getStopOnTagRecord());

        testClass = new StopOnTag();
        StopOnTag.OnTag innerTestClass2 = testClass.new OnTag();
        AgvOutputsRecord testRecord2 = new AgvOutputsRecord();
        testRecord2.setNominalVelocity(Constants.ON_STOP_TAG_VELOCITY);
        testRecord2.setNominalAccel(Constants.ON_STOP_TAG_ACCEL);
        StopOnTagRecord testStopOnTagRecord2 = new StopOnTagRecord();
        testStopOnTagRecord2.setXCounter(1);
        testStopOnTagRecord2.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 1, 17, 2},7));
        testClass.setStopOnTagRecord(testStopOnTagRecord2);
        testRecord2 = innerTestClass2.run(testRecord2,ByteToolbox.byteArrayToInt(new int[] {0, 1, 17, 2},7),2, 17, Constants.X_POSITION_STOP_OFFSET + 1);
        Assertions.assertEquals(testRecord2, compareRecord);
        Assertions.assertEquals(stopOnTagCompareRecord, testClass.getStopOnTagRecord());

        testClass = new StopOnTag();
        StopOnTag.OnTag innerTestClass3 = testClass.new OnTag();
        AgvOutputsRecord testRecord3 = new AgvOutputsRecord();
        testRecord3.setNominalVelocity(Constants.ON_STOP_TAG_VELOCITY);
        testRecord3.setNominalAccel(Constants.ON_STOP_TAG_ACCEL);
        StopOnTagRecord testStopOnTagRecord3 = new StopOnTagRecord();
        testStopOnTagRecord3.setXCounter(Constants.ZERO_X_COUNTS_CUTOFF -1);
        testStopOnTagRecord3.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 1, 17, 2},7));
        testClass.setStopOnTagRecord(testStopOnTagRecord3);
        testRecord3 = innerTestClass3.run(testRecord3,ByteToolbox.byteArrayToInt(new int[] {0, 1, 17, 2},7),2, 17, Constants.X_POSITION_STOP_OFFSET - 0);
        Assertions.assertEquals(testRecord3, compareRecord);
        Assertions.assertEquals(stopOnTagCompareRecord, testClass.getStopOnTagRecord());

        testClass = new StopOnTag();
        StopOnTag.OnTag innerTestClass4 = testClass.new OnTag();
        AgvOutputsRecord testRecord4 = new AgvOutputsRecord();
        testRecord4.setNominalVelocity(Constants.ON_STOP_TAG_VELOCITY);
        testRecord4.setNominalAccel(Constants.ON_STOP_TAG_ACCEL);

        StopOnTagRecord testStopOnTagRecord4 = new StopOnTagRecord();
        testStopOnTagRecord4.setXCounter(Constants.ZERO_X_COUNTS_CUTOFF -1);
        testStopOnTagRecord4.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 1, 17, 2},7));
        testClass.setStopOnTagRecord(testStopOnTagRecord4);
        testRecord4 = innerTestClass4.run(testRecord4,ByteToolbox.byteArrayToInt(new int[] {0, 1, 17, 2},7),2, 17, Constants.X_POSITION_STOP_OFFSET - 0);
        Assertions.assertEquals(testRecord4, compareRecord);
        Assertions.assertEquals(stopOnTagCompareRecord, testClass.getStopOnTagRecord());
    }


    @Test
    public void tagID0StopOnTagRunTest() throws PGVException, InterruptedException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();
        PathRecord pathTestRecord = new PathRecord(ByteToolbox.byteArrayToInt(new int[] {0, 1, 17, 2},7),0,0);
        testRecord = testClass.run(testRecord,inputTestRecord,pathTestRecord);

        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        StopOnTagRecord stopOnTagCompareRecord = new StopOnTagRecord();
        stopOnTagCompareRecord.setFirstPass(false);
        stopOnTagCompareRecord.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 1, 17, 2},7));
        Assertions.assertEquals(testRecord, compareRecord);
        Assertions.assertEquals(stopOnTagCompareRecord, testClass.getStopOnTagRecord());
    }


    @Test
    public void firstPassOnTagRunTest() throws PGVException, InterruptedException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();
        inputTestRecord.setTagId(ByteToolbox.byteArrayToInt(new int[] {0, 1, 30,40},7));
        inputTestRecord.setColumnNumber(40);
        inputTestRecord.setRowNumber(30);
        PathRecord pathTestRecord = new PathRecord(ByteToolbox.byteArrayToInt(new int[] {0, 1, 50, 40},7),0,0);
        testRecord = testClass.run(testRecord,inputTestRecord,pathTestRecord);
        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        StopOnTagRecord stopOnTagCompareRecord = new StopOnTagRecord();
        stopOnTagCompareRecord.setFirstPass(false);
        stopOnTagCompareRecord.setLastId(ByteToolbox.byteArrayToInt(new int[] {0, 1, 30,40},7));
        stopOnTagCompareRecord.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 1, 50,40},7));
        Assertions.assertEquals(testRecord, compareRecord);
        Assertions.assertEquals(testClass.getStopOnTagRecord(), stopOnTagCompareRecord);
    }

    @Test
    public void notFirstPassOnTagRunTest() throws PGVException, InterruptedException {
        AgvOutputsRecord testRecord = new AgvOutputsRecord();
        AgvInputsRecord inputTestRecord = new AgvInputsRecord();
        inputTestRecord.setTagId(ByteToolbox.byteArrayToInt(new int[] {0, 1, 30,40},7));
        inputTestRecord.setColumnNumber(40);
        inputTestRecord.setRowNumber(30);
        PathRecord pathTestRecord = new PathRecord(ByteToolbox.byteArrayToInt(new int[] {0, 1, 50, 40},7),0,0);
        StopOnTagRecord testStopOnTagRecord = new StopOnTagRecord();
        testStopOnTagRecord.setFirstPass(false);
        testStopOnTagRecord.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 1, 50, 40},7));
        testClass.setStopOnTagRecord(testStopOnTagRecord);
        testRecord = testClass.run(testRecord,inputTestRecord,pathTestRecord);
        AgvOutputsRecord compareRecord = new AgvOutputsRecord();
        StopOnTagRecord stopOnTagCompareRecord = new StopOnTagRecord();
        stopOnTagCompareRecord.setFirstPass(false);
        stopOnTagCompareRecord.setLastId(ByteToolbox.byteArrayToInt(new int[] {0, 1, 30,40},7));
        stopOnTagCompareRecord.setNextId(ByteToolbox.byteArrayToInt(new int[] {0, 1, 50,40},7));
        Assertions.assertEquals(testRecord, compareRecord);
        Assertions.assertEquals(testClass.getStopOnTagRecord(), stopOnTagCompareRecord);
    }

}
