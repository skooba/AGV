package com.mochilafulfillment.server.motor_controller.dtos;

import com.mochilafulfillment.server.agv_utils.Constants;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

// What the motor controller sends back
public class MotorControllerRecord {

    private double accel = 0;
    private String direction = "none"; // Initialize to stopped to avoid future null pointers
    private double motor1Velocity;
    private double motor2Velocity;
    private String motor1Sign;
    private String motor2Sign;
    private boolean topLiftSensor;
    private boolean middleLiftSensor;
    private boolean bottomLiftSensor;
    private boolean warningZone;
    private int liftType;
    private int safetyScannerMode;
    private boolean newMotorControllerRecord;
    private boolean newAgvToMotorControllerRecord;


    public MotorControllerRecord(MotorControllerRecord motorControllerRecord) { //copy constructor
        this.direction = motorControllerRecord.direction;
        this.motor1Velocity = motorControllerRecord.motor1Velocity;
        this.motor2Velocity = motorControllerRecord.motor2Velocity;
        this.motor1Sign = motorControllerRecord.motor1Sign;
        this.motor2Sign = motorControllerRecord.motor2Sign;
        this.accel = motorControllerRecord.accel;
        this.liftType = motorControllerRecord.liftType;
        this.safetyScannerMode = motorControllerRecord.safetyScannerMode;
    }

    public MotorControllerRecord() { //no-args constructor

    }

    public MotorControllerRecord setStopped(){
        this.motor1Velocity = 0;
        this.motor2Velocity = 0;
        this.motor1Sign = Constants.MOTOR_POSITIVE;
        this.motor2Sign = Constants.MOTOR_POSITIVE;
        this.liftType = Constants.STOP_VERTICAL_CONSTANT;
        this.accel = Constants.STOPPING_ACCELERATION;
        this.safetyScannerMode = Constants.REGULAR_SCAN;
        this.newAgvToMotorControllerRecord = true; //flag for MotorController class to update
        return this;
    }

    public int getSafetyScannerMode() {
        return safetyScannerMode;
    }

    public void setSafetyScannerMode(int setSafetyScannerMode) {
        this.safetyScannerMode = setSafetyScannerMode;
    }

    public int getLiftType() {
        return liftType;
    }

    public void setLiftType(int liftType) {
        this.liftType = liftType;
    } //0=stop, 1=lift, 2=lower

    public String getMotor1Sign() {
        return motor1Sign;
    }

    public void setMotor1Sign(String motor1Sign) {
        this.motor1Sign = motor1Sign;
    }

    public String getMotor2Sign() {
        return motor2Sign;
    }

    public void setMotor2Sign(String motor2Sign) {
        this.motor2Sign = motor2Sign;
    }

    public double getNominalAccel() { // Read the accelearion
        return accel;
    }

    public void setNominalAccel(double accel) { // sets the acceleration that will be written directly to the motor

        this.accel = accel;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {  // sets the direction that will be written directly to the motor
        this.direction = direction;
    }

    public double getMotor1Velocity() { // this is a percent of the max RPMs for motor 1 (closed loop control mode)
        return motor1Velocity;
    }

    public void setMotor1Velocity(double motor1Velocity) { // sets the motor1 velocity that will be written directly to the motor
        this.motor1Velocity = motor1Velocity;
    }

    public double getMotor2Velocity() { // this is a perent of the max RPMs for motor 2 (closed loop control mode)
        return motor2Velocity;
    }

    public void setMotor2Velocity(double motor2Velocity) { // sets the motor2 velocity that will be written directly to the motor
        this.motor2Velocity = motor2Velocity;
    }

    public boolean isTopLiftSensor() {
        return topLiftSensor;
    }

    public void setTopLiftSensor(boolean topLiftSensor) {
        this.topLiftSensor = topLiftSensor;
    }

    public boolean isBottomLiftSensor() {
        return bottomLiftSensor;
    }

    public void setBottomLiftSensor(boolean bottomLiftSensor) {
        this.bottomLiftSensor = bottomLiftSensor;
    }

    public boolean isMiddleLiftSensor() {
        return middleLiftSensor;
    }

    public void setMiddleLiftSensor(boolean middleLiftSensor) {
        this.middleLiftSensor = middleLiftSensor;
    }

    public boolean isWarningZone() {
        return warningZone;
    }

    public void setWarningZone(boolean warningZone) {
        this.warningZone = warningZone;
    }

    public void setNewMotorControllerRecord(boolean newMotorControllerRecord){
        this.newMotorControllerRecord = newMotorControllerRecord;
    }

    public boolean isNewMotorControllerRecord(){
        return newMotorControllerRecord;
    }

    
    public boolean isNewAgvToMotorControllerRecord(){
        return newAgvToMotorControllerRecord;
    }
    
    public void setNewAgvToMotorControllerRecord(boolean newAgvToMotorControllerRecord){
        this.newAgvToMotorControllerRecord = newAgvToMotorControllerRecord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MotorControllerRecord)) return false;
        MotorControllerRecord that = (MotorControllerRecord) o;
        return Double.compare(that.accel, accel) == 0 && Double.compare(that.motor1Velocity, motor1Velocity) == 0 && Double.compare(that.motor2Velocity, motor2Velocity) == 0 && topLiftSensor == that.topLiftSensor && middleLiftSensor == that.middleLiftSensor && bottomLiftSensor == that.bottomLiftSensor && warningZone == that.warningZone && liftType == that.liftType && safetyScannerMode == that.safetyScannerMode && newMotorControllerRecord == that.newMotorControllerRecord && newAgvToMotorControllerRecord == that.newAgvToMotorControllerRecord && Objects.equals(direction, that.direction) && Objects.equals(motor1Sign, that.motor1Sign) && Objects.equals(motor2Sign, that.motor2Sign);
    }
}
