package com.mochilafulfillment.host.gui;

import com.mochilafulfillment.host.tcp_socket.TcpClient;
import com.mochilafulfillment.shared.SharedConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RemoteGui extends JFrame {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private boolean keyReady = true;
    JFrame frame = new JFrame();
    JTextField keyTextField;
    JTextField velocityTextField;
    JTextField accelerationTextField;
    JTextField rotVelocityTextField;
    JTextField rotAccelerationTextField;
    TcpClient tcpClient;
    KeyListener keyListener;


    public RemoteGui(TcpClient tcpClient) {
        this.tcpClient = tcpClient;
        JPanel panel = new JPanel();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1200, 250);
        panel.setLayout(null);

        frame.add(panel);

        Font font = new Font("Verdana", Font.BOLD, 28);
        JLabel label = new JLabel("Enter Velocity");
        label.setFont(font);
        JLabel label2 = new JLabel("Enter Acceleration");
        label2.setFont(font);
        JLabel label3 = new JLabel("Enter Rot Vel");
        label3.setFont(font);
        JLabel label4 = new JLabel("Enter Rot Accel");
        label4.setFont(font);
        label.setBounds(10,20,300,50);
        label2.setBounds(10,80,300,50);
        label3.setBounds(550,20,300,50);
        label4.setBounds(550,80,300,50);
        panel.add(label);
        panel.add(label2);
        panel.add(label3);
        panel.add(label4);

        keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                //nothing
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if(keyReady == true) {
                    switch (keyEvent.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            logger.debug("Left key pressed");
                            tcpClient.writeToServer(SharedConstants.TCP_LEFT_STRING);
                            break;
                        case KeyEvent.VK_UP:
                            logger.debug("Up key pressed");
                            tcpClient.writeToServer(SharedConstants.TCP_UP_STRING);
                            break;
                        case KeyEvent.VK_RIGHT:
                            logger.debug("Right key pressed");
                            tcpClient.writeToServer(SharedConstants.TCP_RIGHT_STRING);
                            break;
                        case KeyEvent.VK_DOWN:
                            logger.debug("Down key pressed");
                            tcpClient.writeToServer(SharedConstants.TCP_DOWN_STRING);
                            break;
                        case KeyEvent.VK_U:
                            logger.debug("U key pressed");
                            tcpClient.writeToServer(SharedConstants.TCP_U_STRING);
                            break;
                        case KeyEvent.VK_D:
                            logger.debug("D key pressed");
                            tcpClient.writeToServer(SharedConstants.TCP_D_STRING);
                            break;
                    }
                    keyReady = false;
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_U:
                    case KeyEvent.VK_D:
                        logger.debug("Motion key released");
                        tcpClient.writeToServer(SharedConstants.TCP_STOP_STRING);
                        break;
//                        logger.debug("Lift key released");
//                        tcpClient.writeToServer(SharedConstants.TCP_STOP_LIFT_STRING);
//                        break;
                }
                keyReady = true;
            }
        };

        keyTextField = new JTextField("");
        keyTextField.addKeyListener(keyListener);

        accelerationTextField = new JTextField("");
        accelerationTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String acceleration = accelerationTextField.getText();
                logger.debug("Acceleration " + acceleration + " entered into GUI");
                tcpClient.writeToServer(SharedConstants.TCP_ACCELERATION_STRING);
                tcpClient.writeToServer(acceleration);
            }
        });

        velocityTextField = new JTextField("");
        velocityTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String velocity = velocityTextField.getText();
                logger.debug("Velocity " + velocity + " entered into GUI");
                tcpClient.writeToServer(SharedConstants.TCP_VELOCITY_STRING);
                tcpClient.writeToServer(velocity);
            }
        });

        rotVelocityTextField = new JTextField("");
        rotVelocityTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String rotVelocity = rotVelocityTextField.getText();
                logger.debug("Rotation velocity " + rotVelocity + " entered into GUI");
                tcpClient.writeToServer(SharedConstants.TCP_ROTATION_VELOCITY_STRING);
                tcpClient.writeToServer(rotVelocity);
            }
        });

        rotAccelerationTextField = new JTextField("");
        rotAccelerationTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String rotAcceleration = rotAccelerationTextField.getText();
                logger.debug("Rotation velocity " + rotAcceleration + " entered into GUI");
                tcpClient.writeToServer(SharedConstants.TCP_ROTATION_ACCELERATION_STRING);
                tcpClient.writeToServer(rotAcceleration);
            }
        });

        velocityTextField.setFont(font);
        accelerationTextField.setFont(font);
        rotVelocityTextField.setFont(font);
        rotAccelerationTextField.setFont(font);
        keyTextField.setFont(font);

        velocityTextField.setBounds(320,20,200,50);
        accelerationTextField.setBounds(320,80,200,50);
        rotVelocityTextField.setBounds(860,20,200,50);
        rotAccelerationTextField.setBounds(860,80,200,50);

        keyTextField.setBounds(1090,20,200,110);

        panel.add(velocityTextField);
        panel.add(accelerationTextField);
        panel.add(rotVelocityTextField);
        panel.add(rotAccelerationTextField);
        panel.add(keyTextField);


        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                logger.debug("Window closed");
                tcpClient.writeToServer(SharedConstants.TCP_SHUTDOWN_STRING);
                e.getWindow().dispose();
            }
        });

        frame.setVisible(true);
    }

    public JFrame getJFrame(){
        return this.frame;
    }

    public JTextField getVelocityTextField(){
        return this.velocityTextField;
    }

    public JTextField getAccelerationTextField(){
        return this.accelerationTextField;
    }

    public JTextField getRotVelocityTextField(){
        return this.rotVelocityTextField;
    }

    public JTextField getRotAccelerationTextField(){
        return this.rotAccelerationTextField;
    }

    public KeyListener getKeyListener() {
        return keyListener;
    }

}

