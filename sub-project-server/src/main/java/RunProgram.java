import com.mochilafulfillment.server.Agv;
import com.mochilafulfillment.server.agv_utils.Constants;
import com.mochilafulfillment.server.api.laptop.TerminalReader;
import com.mochilafulfillment.server.motor_controller.MotorController;
import com.mochilafulfillment.server.motor_controller.dtos.MotorControllerRecord;
import com.mochilafulfillment.server.position_scanner.PositionScanner;
import com.mochilafulfillment.server.position_scanner.dtos.PositionScannerResponseRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RunProgram implements Runnable{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private MotorControllerRecord controllerRecord = new MotorControllerRecord(); // record sends input values from motor controller thread to AGV thread
    private PositionScannerResponseRecord responseRecord = new PositionScannerResponseRecord();
    private Lock motorControllerLock = new ReentrantLock();
    private MotorController motorController = new MotorController(Constants.CONTROLLER_PORT, controllerRecord, motorControllerLock);
    private PositionScanner positionScanner = new PositionScanner(Constants.PF_PORT, responseRecord);
    TerminalReader terminalReader = new TerminalReader();
    private Agv agv = new Agv(terminalReader, controllerRecord, responseRecord, motorControllerLock); // Main Thread;
    private ThreadPoolExecutor pool;
    private boolean keepLooping = true;

    private int nThreads = 4;

    public void run() {

        //Inner class creates the equivalent of Executors.newFixedThreadPool(nThreads)
        pool = new ThreadPoolExecutor(nThreads,
                nThreads,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>() ){

            //afterExecute handles uncaught exceptions that propagate to RunProgram
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                if(t!=null) {
                    t.printStackTrace();
                }
                if (t == null && r instanceof Future<?>) {
                    try {
                        Future<?> future = (Future<?>) r;
                        if (future.isDone()) {
                            future.get();
                        }
                    } catch (Exception e) {
                        t = e;
                    }
                }
                if (t != null) {
                    logger.debug("Exception propagated to RunProgram");
                        logger.error("ROLLBAR: Runtime Error Crashes Program");
                        shutdownNow();
                        keepLooping = false;
                        logger.debug("Thread pool shutdown");
                }
            }
        };
        pool.execute(terminalReader);//Thread 1
        pool.execute(motorController);//Thread 2
        pool.execute(positionScanner);//Thread 3
        pool.execute(agv); //Thread 4
        logger.debug("Opened all threads");

        while (keepLooping){
            try {
                Thread.sleep(Constants.LOOP_PAUSE_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.debug("End of runProgram reached");

    }

    public void setMotorController(MotorController motorController) {
        this.motorController = motorController;
    }

    public MotorController getMotorController(){
        return this.motorController;
    }

    public void setControllerRecord(MotorControllerRecord controllerRecord){
        this.controllerRecord = controllerRecord;
    }

    public MotorControllerRecord getControllerRecord(){
        return this.controllerRecord;
    }

    public void setPositionScanner(PositionScanner positionScanner) {
        this.positionScanner = positionScanner;
    }

    public Agv getAgv(){
        return this.agv;
    }

    public void setAgv(Agv agv){
        this.agv = agv;
    }

    public void setTerminalReader(TerminalReader terminalReader){this.terminalReader = terminalReader;}

    public ThreadPoolExecutor getPool(){
        return this.pool;
    }
}
