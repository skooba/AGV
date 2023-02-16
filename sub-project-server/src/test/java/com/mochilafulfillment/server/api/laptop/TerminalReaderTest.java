package com.mochilafulfillment.server.api.laptop;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.mockito.Mockito.*;

public class  TerminalReaderTest {
    TerminalReader testClass;
    TerminalReader spyClass;

    @BeforeEach
    public void init(){
        testClass = new TerminalReader();
        spyClass = spy(testClass);
        doReturn("on", "off").when(spyClass).getStatus();
    }

    @Test
    public void wasTerminateKeyEnteredRunTest(){
        ByteArrayInputStream in = new ByteArrayInputStream(("c" + System.lineSeparator()).getBytes());
        System.setIn(in);
        testClass.run();
        Assertions.assertEquals(testClass.isEndProgram(), true);
    }

    @Test
    public void wasExitAutoModeKeyEnteredRunTest(){
        ByteArrayInputStream in = new ByteArrayInputStream(("1" + System.lineSeparator()).getBytes());
        System.setIn(in);
        spyClass.run();
        Assertions.assertEquals(spyClass.isExitAutoMode(), true);
    }

    @Test
    public void wasExitRemoteModeKeyEnteredRunTest(){
        ByteArrayInputStream in = new ByteArrayInputStream(("2" + System.lineSeparator()).getBytes());
        System.setIn(in);
        spyClass.run();
        Assertions.assertEquals(spyClass.isExitRemoteMode(), true);
    }

    @Test
    public void exitAutoModeResetTest(){
        when(spyClass.getStatus()).thenReturn("on", "on", "off");
        ByteArrayInputStream in = new ByteArrayInputStream(("2" + System.lineSeparator() + "3" + System.lineSeparator()).getBytes());
        System.setIn(in);
        spyClass.run();
        Assertions.assertEquals(spyClass.isExitAutoMode(), false);
    }

    @Test
    public void exitRemoteModeResetTest(){
        when(spyClass.getStatus()).thenReturn("on", "on", "off");
        ByteArrayInputStream in = new ByteArrayInputStream(("1" + System.lineSeparator() + "3" + System.lineSeparator()).getBytes());
        System.setIn(in);
        spyClass.run();
        Assertions.assertEquals(spyClass.isExitRemoteMode(), false);
    }
}
