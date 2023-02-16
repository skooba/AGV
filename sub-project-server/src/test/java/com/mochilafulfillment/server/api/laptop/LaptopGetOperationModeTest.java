package com.mochilafulfillment.server.api.laptop;

import com.mochilafulfillment.server.agv_utils.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class LaptopGetOperationModeTest {
    LaptopGetOperationMode testClass;

    @BeforeEach
    public void init(){
        testClass = new LaptopGetOperationMode();
    }

    @Test
    public void RemoteControlModeRunTest() throws InterruptedException {
        String compareString = Constants.REMOTE_CONTROL_MODE;
        TerminalReader terminalReader = new TerminalReader();
        TerminalReader spyTerminalReader = spy(terminalReader);
        when(spyTerminalReader.getCurrentValue()).thenReturn("1").thenCallRealMethod();
        String testResponse = testClass.run(spyTerminalReader);
        Assertions.assertEquals(testResponse, compareString);
        Assertions.assertEquals(spyTerminalReader.getCurrentValue(), "none");
    }

    @Test
    public void AutomaticModeRunTest() throws InterruptedException {
        String compareString = Constants.AUTOMATIC_MODE;
        TerminalReader terminalReader = new TerminalReader();
        TerminalReader spyTerminalReader = spy(terminalReader);
        when(spyTerminalReader.getCurrentValue()).thenReturn("2").thenCallRealMethod();
        String testResponse = testClass.run(spyTerminalReader);
        Assertions.assertEquals(testResponse, compareString);
        Assertions.assertEquals(spyTerminalReader.getCurrentValue(), "none");

    }

    @Test
    public void IncorrectModeRunTest() {
        TerminalReader mockedTerminalReader = mock(TerminalReader.class);
        when(mockedTerminalReader.getCurrentValue()).thenReturn("thIs Is for a test9");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            testClass.run(mockedTerminalReader);
        });
    }
}
