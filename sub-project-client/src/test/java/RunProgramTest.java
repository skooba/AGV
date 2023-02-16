import com.mochilafulfillment.host.dtos.ClientRecord;
import com.mochilafulfillment.host.gui.RemoteGui;
import com.mochilafulfillment.host.tcp_socket.TcpClient;
import com.mochilafulfillment.shared.SharedConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RunProgramTest {
    RunProgram runProgram;

    @Test
    public void startGuiTest() throws IOException, InterruptedException {
        runProgram = mock(RunProgram.class);

        ClientRecord clientRecord = new ClientRecord();
        clientRecord.setStartGui(true);

        ServerSocket serverSocket = new ServerSocket(SharedConstants.TCP_PORT);
        TcpClient testTcpClient = new TcpClient("localhost", SharedConstants.TCP_PORT, clientRecord);

        RunProgram spyRunProgram = Mockito.spy(runProgram);
        doCallRealMethod().when(spyRunProgram).run();
        doCallRealMethod().when(spyRunProgram).setTcpClient(any());
        doCallRealMethod().when(spyRunProgram).setClientRecord(any());

        spyRunProgram.setTcpClient(testTcpClient);
        spyRunProgram.setClientRecord(clientRecord);

        Socket testSocket = testTcpClient.getTcpHandler().getSocket();

        ScheduledExecutorService testExecutor = Executors.newScheduledThreadPool(1);
        Future future = testExecutor.submit(spyRunProgram);
        Thread.sleep(SharedConstants.TEST_RUNTIME);
        future.cancel(true);


        ClientRecord testRecord = new ClientRecord();
        testRecord.setEndGui(false);

        Assertions.assertEquals(clientRecord,testRecord);

        testSocket.close();
        serverSocket.close();
    }

    @Test
    public void endGuiTest() throws IOException, InterruptedException {
        runProgram = mock(RunProgram.class);

        ClientRecord clientRecord = new ClientRecord();
        clientRecord.setEndGui(true);

        ServerSocket serverSocket = new ServerSocket(SharedConstants.TCP_PORT);
        TcpClient testTcpClient = new TcpClient("localhost", SharedConstants.TCP_PORT, clientRecord);

        RunProgram spyRunProgram = Mockito.spy(runProgram);
        doCallRealMethod().when(spyRunProgram).run();
        doCallRealMethod().when(spyRunProgram).setTcpClient(any());
        doCallRealMethod().when(spyRunProgram).setClientRecord(any());
        doCallRealMethod().when(spyRunProgram).setRemoteGui(any());


        spyRunProgram.setTcpClient(testTcpClient);
        spyRunProgram.setClientRecord(clientRecord);

        Socket testSocket = testTcpClient.getTcpHandler().getSocket();


        spyRunProgram.setRemoteGui(new RemoteGui(testTcpClient));
        spyRunProgram.run();

        testSocket.close();
        serverSocket.close();
    }
}
