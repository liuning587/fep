/*
 * 因为RtuUnrespPacketChecker存在死掉的情况，替换原来的RtuUnrespPacketChecker，
 * 采用quartz替换Timer
 */
package fep.mina.common.unrespchecker;

import fep.bp.processor.polling.PollingProcessor;
import fep.mina.common.PepCommunicatorInterface;
import java.util.Date;
import java.util.logging.Level;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author THINKPAD
 */
public class RtuUnresponseChecker implements Runnable{
    private final static Logger log = LoggerFactory.getLogger(RtuUnresponseChecker.class);
    private PepCommunicatorInterface pepCommunicator;
    private Trigger triggerSecond10;
    
    public RtuUnresponseChecker(PepCommunicatorInterface pepCommunicator)
    {
       this.pepCommunicator = pepCommunicator;
    }
    
    public void run() {
        try {
            SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
            Scheduler sched = schedFact.getScheduler();
            sched.start();

            //10秒检查一次
            JobDetail jobDetailSecond10 = new JobDetail("UnrespCheckJob_10Second", null, UnrespCheckJobProxy.class);
            jobDetailSecond10.getJobDataMap().put("UnrespCheckJob",new UnrespCheckJob(pepCommunicator));
            triggerSecond10 = TriggerUtils.makeSecondlyTrigger(10);

            triggerSecond10.setStartTime(TriggerUtils.getEvenMinuteDate(new Date())); //从下一个分钟开始
            triggerSecond10.setName("triggerSecond10");          

            sched.scheduleJob(jobDetailSecond10, triggerSecond10);
        }
        catch (SchedulerException ex) {
            java.util.logging.Logger.getLogger(RtuUnresponseChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
