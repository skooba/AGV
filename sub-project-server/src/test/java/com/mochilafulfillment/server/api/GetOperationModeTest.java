package com.mochilafulfillment.server.api;

import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.api.laptop.TerminalReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetOperationModeTest {

    GetOperationMode testClass;

    @BeforeEach
    public void init(){
        testClass = new GetOperationMode();
    }

    @Test
    public void getLaptopOperationMode() throws InterruptedException {
        String compareString = Constants.REMOTE_CONTROL_MODE;
        TerminalReader mockedTerminalReader = mock(TerminalReader.class);
        when(mockedTerminalReader.getCurrentValue()).thenReturn("1");
        String testResponse = testClass.run(mockedTerminalReader);
        Assertions.assertEquals(testResponse, compareString);
    }

}
