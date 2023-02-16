import com.mochilafulfillment.host.client_utils.Constants;
import com.mochilafulfillment.host.dtos.ClientRecord;
import com.mochilafulfillment.host.gui.RemoteGui;
import com.mochilafulfillment.host.tcp_socket.TcpClient;
import com.mochilafulfillment.shared.SharedConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.WindowEvent;

//todo: tie in Rollbar (currently all exceptions are runtime exceptions)
public class RunProgram implements Runnable{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private TcpClient tcpClient;
    private RemoteGui remoteGui;
    private ClientRecord clientRecord;

    public RunProgram(){
        this.clientRecord = new ClientRecord();
        tcpClient = new TcpClient(Constants.HOST_IP, SharedConstants.TCP_PORT, clientRecord);

        new Thread( new Runnable() {
            @Override
            public void run() {
                tcpClient.startTcpListener();;
            }
        }).start();
    }

    public void run() {
        while(true) {
            if (clientRecord.isStartGui()) {
                //do not want to start new GUI again
                clientRecord.setStartGui(false);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        remoteGui = new RemoteGui(tcpClient);
                    }
                });
            }
            if (clientRecord.isEndGui() == true){
                //close gui if it isn't closed yet
//                logger.debug("Closing gui");
                WindowEvent event = new WindowEvent(remoteGui.getJFrame() ,WindowEvent.WINDOW_CLOSING);
                remoteGui.getJFrame().dispatchEvent(event);
                break;
            }
            try {
                Thread.sleep(Constants.LOOP_PAUSE_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setTcpClient(TcpClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    public void setClientRecord(ClientRecord clientRecord) {
        this.clientRecord = clientRecord;
    }

    public void setRemoteGui(RemoteGui remoteGui) {
        this.remoteGui = remoteGui;
    }
}
