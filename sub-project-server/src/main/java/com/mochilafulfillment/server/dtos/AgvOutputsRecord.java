package com.mochilafulfillment.server.dtos;

import com.mochilafulfillment.server.agv_utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

// Thread safe DTO (data transfer object) with output values updated/shared amongst all Agv logic services
public class AgvOutputsRecord {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private double motor1Velocity;
    private double motor2Velocity;
    private double nominalAccel = Constants.STOPPING_ACCELERATION;
    private int rotationVelocity = Constants.ROTATE_SPEED;
    private int rotationAcceleration = Constants.ROTATE_ACCEL;
    private String direction = Constants.STOP_STRING;
    private int apiVelocityCommand;
    private int lock;
    private boolean tagIsFinished = true; //when starting program all modes are finished so new PathRecord is read
    private boolean forkLiftIsFinished = true;
    private boolean forkPlaceIsFinished = true;
    private boolean rotationIsFinished = true;
    private int liftType;
    private String signMotor1 = Constants.MOTOR_POSITIVE;
    private String signMotor2 = Constants.MOTOR_POSITIVE;
    private double motorVelocityRatio = 1;
    private int safetyScannerMode = Constants.REGULAR_SCAN;
    private boolean pathFinished;
    private boolean bothMotorsNegativeForRotation;
    private boolean agvStopped;



    public AgvOutputsRecord(){ //Default constructor

    }

    public AgvOutputsRecord(AgvOutputsRecord previousAgvOutputsRecord){ //Constructor for copying
        logger.debug("Creating new AGV Output Record with previous velocity, acceleration and direction");
        setMotor1Velocity(previousAgvOutputsRecord.getMotor1Velocity());
        setMotor2Velocity(previousAgvOutputsRecord.getMotor2Velocity());
        setNominalAccel(previousAgvOutputsRecord.getNominalAccel());
        setDirection(previousAgvOutputsRecord.getDirection());
        setLiftType(previousAgvOutputsRecord.getLiftType());
        setSafetyScannerMode(previousAgvOutputsRecord.getSafetyScannerMode());
        setMotor1Sign(previousAgvOutputsRecord.getMotor1Sign());
        setMotor2Sign(previousAgvOutputsRecord.getMotor2Sign());
    }

    public boolean getRotationIsFinished() {
        return rotationIsFinished;
    }

    public void setRotationIsFinished(boolean rotationIsFinished) throws InterruptedException {
        this.rotationIsFinished = rotationIsFinished;
    }

    public int getSafetyScannerMode() {
        return safetyScannerMode;
    }

    public void setSafetyScannerMode(int safetyScannerMode) {
        this.safetyScannerMode = safetyScannerMode;
    }

    public void setMotorVelocityRatio(double motorVelocityRatio){
        this.motorVelocityRatio = motorVelocityRatio;}

    public double getMotorVelocityRatio(){return motorVelocityRatio;}

    public void setMotor1Sign(String signMotor1){this.signMotor1 = signMotor1;}

    public String getMotor1Sign(){return this.signMotor1;}

    public void setMotor2Sign(String signMotor2){this.signMotor2 = signMotor2;}

    public String getMotor2Sign(){return this.signMotor2;}

    public int getLiftType(){return this.liftType;}

    public void setLiftType(int liftType){
        this.liftType = liftType;}

    public boolean getTagIsFinished(){
        return tagIsFinished;
    }

    public void setTagIsFinished(boolean tagIsFinished) throws InterruptedException {
        this.tagIsFinished = tagIsFinished;
    }

    public double getMotor1Velocity() { //get motor 1 velocity
        return motor1Velocity;
    }

    public void setMotor1Velocity(double motor1Velocity) {
        this.motor1Velocity = motor1Velocity;
    }

    public double getMotor2Velocity() { //get motor 2 velocity
        return motor2Velocity;
    }

    public void setMotor2Velocity(double motor2Velocity) { // set motor 2 velocity. Set by laptop. Set to 0 if AGV is off of tape. Scaled down when one tag away.
        this.motor2Velocity = motor2Velocity;
    }

    public double getNominalAccel() { // Get the acceleration of the AGV.
        return nominalAccel;
    }

    public void setNominalAccel(double nominalAccel) { // acceleration of the AGV. Slows down when AGV is one tag away from stop tag.
        this.nominalAccel = nominalAccel;
    }

    public String getDirection() { // called in the straight line implementation
        return direction;
    }

    public void setDirection(String direction) { //Set by laptop inputs. Set automatically to stopString in stoptag mode, set to either cw or ccw in rotate mode.
        this.direction = direction;
    }

    public int getNominalVelocity() { // get the velocity set by the laptop, if not velocity was set by laptop then it is 0
        return apiVelocityCommand;
    }

    public void setNominalVelocity(int apiVelocityCommand) { // Initialized in the AGV output records but can also be set by the laptop
        this.apiVelocityCommand = apiVelocityCommand;
    }

    public int getRotationVelocity() {
        return rotationVelocity;
    }

    public void setRotationVelocity(int rotationVelocity) {
        this.rotationVelocity = rotationVelocity;
    }

    public int getRotationAcceleration() {
        return rotationAcceleration;
    }

    public void setRotationAcceleration(int rotationAcceleration) {
        this.rotationAcceleration = rotationAcceleration;
    }

    public int getLock() { // stopOnTagKey == 1, goButtonKey == 2, rotationKey == 3, otherwise == 0 --> if equals 1, 2 or 3 the two functions that do not correspond to the number that it equals cannot operate
        return lock;
    }

    public void setLock(int lock) { //Used to lock out the other two operations from trying to run while the current operation is running --> if 0 then any operation can grab ontrol and start running
        this.lock = lock;
    }

    public boolean getForkLiftIsFinished() {
        return forkLiftIsFinished;
    }

    public void setForkLiftIsFinished(boolean forkLiftIsFinished) throws InterruptedException {
        this.forkLiftIsFinished = forkLiftIsFinished;
    }

    public void setPathFinished(boolean pathFinished) {
        this.pathFinished = pathFinished;
    }

    public boolean isPathFinished(){
        return pathFinished;
    }

    public void setModeFinished(boolean pathFinished) {
        this.pathFinished = pathFinished;
    }

    public boolean isModeFinished(){
        return pathFinished;
    }

    public boolean isBothMotorsNegativeForRotation() {
        return bothMotorsNegativeForRotation;
    }

    public void setBothMotorsNegativeForRotation(boolean bothMotorsNegativeForRotation) {
        this.bothMotorsNegativeForRotation = bothMotorsNegativeForRotation;
    }

    public void setAgvStopped(boolean agvStopped) {
        this.agvStopped = agvStopped;
    }

    public boolean isAgvStopped(){
        return agvStopped;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgvOutputsRecord)) return false;
        AgvOutputsRecord that = (AgvOutputsRecord) o;
        return Double.compare(that.motor1Velocity, motor1Velocity) == 0 && Double.compare(that.motor2Velocity, motor2Velocity) == 0 && Double.compare(that.nominalAccel, nominalAccel) == 0 && that.rotationVelocity == rotationVelocity && that.rotationAcceleration == rotationAcceleration && apiVelocityCommand == that.apiVelocityCommand && lock == that.lock && tagIsFinished == that.tagIsFinished && forkLiftIsFinished == that.forkLiftIsFinished && rotationIsFinished == that.rotationIsFinished && liftType == that.liftType && Double.compare(that.motorVelocityRatio, motorVelocityRatio) == 0 && safetyScannerMode == that.safetyScannerMode && pathFinished == that.pathFinished && direction.equals(that.direction) && signMotor1.equals(that.signMotor1) && signMotor2.equals(that.signMotor2) && agvStopped == that.agvStopped;
    }

    public boolean equalsMotorController(Object o){
        if(equals(o)) return true;
        if(!(o instanceof AgvOutputsRecord)) return false;
        AgvOutputsRecord that = (AgvOutputsRecord) o;
        return Double.compare(that.motor1Velocity, motor1Velocity) == 0 && Double.compare(that.motor2Velocity, motor2Velocity) == 0 && Double.compare(that.nominalAccel, nominalAccel) == 0 && liftType == that.liftType && safetyScannerMode == that.safetyScannerMode && Objects.equals(direction, that.direction) && Objects.equals(signMotor1, that.signMotor1) && Objects.equals(signMotor2, that.signMotor2);
    }



}
