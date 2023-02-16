package com.mochilafulfillment.server.api;

import com.mochilafulfillment.server.api.laptop.LaptopGetOperationMode;
import com.mochilafulfillment.server.api.laptop.TerminalReader;

// This class allows other ways to get operation mode in the future, i.e. from api call
public class GetOperationMode {
    public String run(TerminalReader terminalReader) throws InterruptedException {
        LaptopGetOperationMode laptopGetOperationMode = new LaptopGetOperationMode();
        return(laptopGetOperationMode.run(terminalReader));
    }
}
