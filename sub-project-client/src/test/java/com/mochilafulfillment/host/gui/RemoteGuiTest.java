package com.mochilafulfillment.host.gui;

import com.mochilafulfillment.host.tcp_socket.TcpClient;
import com.mochilafulfillment.shared.SharedConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class RemoteGuiTest {

    RemoteGui testClass;
    TcpClient mockTcpClient;

    @BeforeEach
    public void init(){
        mockTcpClient = mock(TcpClient.class);
        testClass = new RemoteGui(mockTcpClient);
    }

    @Test
    public void keyPressedLeftTest(){

        KeyEvent key = new KeyEvent(testClass, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,  KeyEvent.VK_LEFT,'Z');
        testClass.getKeyListener().keyPressed(key);

        verify(mockTcpClient).writeToServer(SharedConstants.TCP_LEFT_STRING);

    }

    @Test
    public void keyPressedUpTest(){
        KeyEvent key = new KeyEvent(testClass, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,  KeyEvent.VK_UP,'Z');
        testClass.getKeyListener().keyPressed(key);

        verify(mockTcpClient).writeToServer(SharedConstants.TCP_UP_STRING);

    }

    @Test
    public void keyPressedRightTest(){
        KeyEvent key = new KeyEvent(testClass, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,  KeyEvent.VK_RIGHT,'Z');
        testClass.getKeyListener().keyPressed(key);

        verify(mockTcpClient).writeToServer(SharedConstants.TCP_RIGHT_STRING);

    }

    @Test
    public void keyPressedDownTest(){
        KeyEvent key = new KeyEvent(testClass, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,  KeyEvent.VK_DOWN,'Z');
        testClass.getKeyListener().keyPressed(key);

        verify(mockTcpClient).writeToServer(SharedConstants.TCP_DOWN_STRING);

    }

    @Test
    public void keyPressedUTest(){
        KeyEvent key = new KeyEvent(testClass, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,  KeyEvent.VK_U,'Z');
        testClass.getKeyListener().keyPressed(key);

        verify(mockTcpClient).writeToServer(SharedConstants.TCP_U_STRING);

    }

    @Test
    public void keyPressedDTest(){
        KeyEvent key = new KeyEvent(testClass, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,  KeyEvent.VK_D,'Z');
        testClass.getKeyListener().keyPressed(key);

        verify(mockTcpClient).writeToServer(SharedConstants.TCP_D_STRING);

    }

    @Test
    public void keyReleasedLeftTest(){
        KeyEvent key = new KeyEvent(testClass, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0,  KeyEvent.VK_LEFT,'Z');
        testClass.getKeyListener().keyReleased(key);

        verify(mockTcpClient).writeToServer(SharedConstants.TCP_STOP_STRING);

    }

    @Test
    public void keyReleasedUpTest(){
        KeyEvent key = new KeyEvent(testClass, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0,  KeyEvent.VK_UP,'Z');
        testClass.getKeyListener().keyReleased(key);

        verify(mockTcpClient).writeToServer(SharedConstants.TCP_STOP_STRING);

    }

    @Test
    public void keyReleasedRightTest(){
        KeyEvent key = new KeyEvent(testClass, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0,  KeyEvent.VK_RIGHT,'Z');
        testClass.getKeyListener().keyReleased(key);

        verify(mockTcpClient).writeToServer(SharedConstants.TCP_STOP_STRING);

    }

    @Test
    public void keyReleasedDownTest(){
        KeyEvent key = new KeyEvent(testClass, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0,  KeyEvent.VK_DOWN,'Z');
        testClass.getKeyListener().keyReleased(key);

        verify(mockTcpClient).writeToServer(SharedConstants.TCP_STOP_STRING);

    }

    @Test
    public void keyReleasedUTest(){
        KeyEvent key = new KeyEvent(testClass, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0,  KeyEvent.VK_U,'Z');
        testClass.getKeyListener().keyReleased(key);

        verify(mockTcpClient).writeToServer(SharedConstants.TCP_STOP_STRING);

    }

    @Test
    public void keyReleasedDTest(){
        KeyEvent key = new KeyEvent(testClass, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0,  KeyEvent.VK_D,'Z');
        testClass.getKeyListener().keyReleased(key);

        verify(mockTcpClient).writeToServer(SharedConstants.TCP_STOP_STRING);

    }

    @Test
    public void windowClosedTest() {
        WindowEvent event = new WindowEvent(testClass.getJFrame() ,WindowEvent.WINDOW_CLOSING);
        testClass.getJFrame().dispatchEvent(event);

        verify(mockTcpClient).writeToServer(SharedConstants.TCP_SHUTDOWN_STRING);
    }

    @Test
    public void accelerationEnteredTest(){
        testClass.getAccelerationTextField().setText("50");
        testClass.getAccelerationTextField().postActionEvent();

        verify(mockTcpClient).writeToServer(SharedConstants.TCP_ACCELERATION_STRING);
        verify(mockTcpClient).writeToServer("50");
    }

    @Test
    public void velocityEnteredTest(){
        testClass.getVelocityTextField().setText("50");
        testClass.getVelocityTextField().postActionEvent();

        verify(mockTcpClient).writeToServer(SharedConstants.TCP_VELOCITY_STRING);
        verify(mockTcpClient).writeToServer("50");
    }

    @Test
    public void rotationAccelerationEnteredTest(){
        testClass.getRotAccelerationTextField().setText("50");
        testClass.getRotAccelerationTextField().postActionEvent();

        verify(mockTcpClient).writeToServer(SharedConstants.TCP_ROTATION_ACCELERATION_STRING);
        verify(mockTcpClient).writeToServer("50");
    }

    @Test
    public void rotationVelocityEnteredTest(){
        testClass.getRotVelocityTextField().setText("50");
        testClass.getRotVelocityTextField().postActionEvent();

        verify(mockTcpClient).writeToServer(SharedConstants.TCP_ROTATION_VELOCITY_STRING);
        verify(mockTcpClient).writeToServer("50");
    }

    @Test
    public void keyHeldTest(){
        KeyEvent key = new KeyEvent(testClass, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,  KeyEvent.VK_UP,'Z');
        testClass.getKeyListener().keyPressed(key);

        KeyEvent key1 = new KeyEvent(testClass, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,  KeyEvent.VK_LEFT,'Z');
        testClass.getKeyListener().keyPressed(key1);

        verify(mockTcpClient).writeToServer(SharedConstants.TCP_UP_STRING);

    }

}
