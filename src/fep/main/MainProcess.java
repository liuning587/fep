/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fep.main;

import fep.bp.processor.*;
import fep.bp.processor.planManager.PlanManager;
import fep.bp.processor.polling.PollingProcessor;
import fep.bp.processor.upgrade.UpgradeProcessor;
import fep.mina.common.PepCommunicatorInterface;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thinkpad
 */
public class MainProcess {

    private static int rtTaskSenderMaxNumber = 1;
    private static int pollingProcessorMaxNumber = 1;
    private ProcessorStatus status;
    private RealTimeSender realtimeSender;
            
    private PollingProcessor pollingProcessor;
    private PlanManager planManager;
    private UpgradeProcessor upgradeProcessor;

    private final static Logger log = LoggerFactory.getLogger(MainProcess.class);
    private PepCommunicatorInterface pepCommunicator;//通信代理器
    private ThreadPoolExecutor threadPool;

    private void runProcessor(int maxCount, String title, Runnable bp) {
        for (int i = 1; i <= maxCount; i++) {
            try {
                String task = title + i;
                log.info(task);
                threadPool.execute(bp);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }



    private void runPollingProcessor() {
        try {
            if (this.pollingProcessor == null) {
                this.pollingProcessor = new PollingProcessor(this.pepCommunicator);
            }
            pollingProcessor.run();
            log.info("启动组件：轮召任务管理器 ");
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void runPlanManager() {
        try {
            if (this.planManager == null) {
                this.planManager = new PlanManager(this.pepCommunicator);
            }
            planManager.run();
            log.info("启动漏保试跳计划调度器 ");
        } catch (Exception e) {
            //e.printStackTrace();
        }

    }

    public MainProcess(PepCommunicatorInterface pepCommunicator) {
        this.threadPool = new ThreadPoolExecutor(10, 10, 3,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        this.pepCommunicator = pepCommunicator;
        status = new ProcessorStatus();
        
        planManager = new PlanManager(this.pepCommunicator);
        planManager.setProcessorStatus(status);
        
        pollingProcessor = new PollingProcessor(this.pepCommunicator);
        pollingProcessor.setProcessorStatus(status);
        
        upgradeProcessor = new UpgradeProcessor(this.pepCommunicator);
        upgradeProcessor.setProcessorStatus(status);
        
        realtimeSender = new RealTimeSender(this.pepCommunicator);
        realtimeSender.setProcessorStatus(status);
   }

    public void run() {
        runPollingProcessor();
        runProcessor(rtTaskSenderMaxNumber, "启动组件：下发报文处理器 ", new ResponseDealer(this.pepCommunicator,upgradeProcessor));
        runProcessor(rtTaskSenderMaxNumber, "启动组件：任务发送器 ", realtimeSender);
        
        runPlanManager();
        runProcessor(rtTaskSenderMaxNumber, "启动短信回复检查处理器 ", new SMSCheckProcessor());
        runProcessor(rtTaskSenderMaxNumber, "启动组件：上报任务处理器 ", new UpLoadProcessor(this.pepCommunicator));
        runProcessor(rtTaskSenderMaxNumber, "启动组件：升级管理器 ", upgradeProcessor);

    }
}
