package com.mochilafulfillment.server.api;

import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.controls_logic.dtos.PathRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class PickPathTest {

    PickPath testClass;
    String resourceName;
    InputStreamReader inputStreamReader;

    @BeforeEach
    public void init() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        resourceName = Constants.TEST_FILE_NAME;
        inputStreamReader = new InputStreamReader(classLoader.getResourceAsStream(resourceName));
        testClass = new PickPath(resourceName);
    }

    @Test
    public void fromCsvTest() throws IOException {
        PickPath.PathRecordsUpload innerTestClass = testClass.new PathRecordsUpload();
        LinkedList<PathRecord> testLinkedList = innerTestClass.fromCsv(inputStreamReader);
        PathRecord testPathRecord1 = testLinkedList.getFirst();
        PathRecord testPathRecord2 = testLinkedList.get(1);
        PathRecord testPathRecord3 = testLinkedList.get(2);
        PathRecord testPathRecord4 = testLinkedList.getLast();

        PathRecord comparePathRecord1 = new PathRecord(3880,90, Constants.LIFT_LINE);
        PathRecord comparePathRecord2 = new PathRecord(3878,180,Constants.PICK_LINE);
        PathRecord comparePathRecord3 = new PathRecord(21,270,Constants.LOWER_LINE);
        PathRecord comparePathRecord4 = new PathRecord(21,-90,0);

        Assertions.assertEquals(testPathRecord1, comparePathRecord1);
        Assertions.assertEquals(testPathRecord2, comparePathRecord2);
        Assertions.assertEquals(testPathRecord3, comparePathRecord3);
        Assertions.assertEquals(testPathRecord4, comparePathRecord4);
    }

    @Test
    public void getNextTest(){
        PathRecord testPathRecord1 = testClass.getNext();
        PathRecord testPathRecord2 = testClass.getNext();
        PathRecord testPathRecord3 = testClass.getNext();
        PathRecord testPathRecord4 = testClass.getNext();

        PathRecord comparePathRecord1 = new PathRecord(3880,90, Constants.LIFT_LINE);
        PathRecord comparePathRecord2 = new PathRecord(3878,180,Constants.PICK_LINE);
        PathRecord comparePathRecord3 = new PathRecord(21,270,Constants.LOWER_LINE);
        PathRecord comparePathRecord4 = new PathRecord(21,-90,0);

        Assertions.assertEquals(testPathRecord1, comparePathRecord1);
        Assertions.assertEquals(testPathRecord2, comparePathRecord2);
        Assertions.assertEquals(testPathRecord3, comparePathRecord3);
        Assertions.assertEquals(testPathRecord4, comparePathRecord4);
    }
}
