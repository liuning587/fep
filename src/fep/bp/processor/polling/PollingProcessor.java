/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fep.bp.processor.polling;

import fep.mina.common.PepCommunicatorInterface;
import java.text.ParseException;
import java.util.logging.Level;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thinkpad
 */
public class PollingProcessor implements Runnable{
    private final static Logger log = LoggerFactory.getLogger(PollingJob.class);
    //任务周期类型
    private final int CIRCLE_UNIT_MINUTE =0;
    private final int CIRCLE_UNIT_HOUR =1;
    private final int CIRCLE_UNIT_DAY =2;
    private final int CIRCLE_UNIT_MONTH =3;

    //日任务启动时间点
    private final int STARTUP_TIME = 1;//凌晨1点启动

    private PepCommunicatorInterface pepCommunicator;

    private static String TRIGGER_GROUP_NAME = "pollingTriggerGroup";
    private static String JOB_NAME_HALFHOUR = "Job_HalfHour";
    private static String JOB_NAME_HOUR = "Job_Hour";
    private Trigger triggerHour;
    private CronTrigger triggerHalfHour;

    public PollingProcessor(PepCommunicatorInterface pepCommunicator){
        this.pepCommunicator = pepCommunicator;
    }

    @Override
    public void run() {
        try {
  
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            String jobName = "pollingJob";
            String groupName = "pollingGroup";

            //删除作业
           if(scheduler.getJobDetail(jobName,groupName) != null){
              scheduler.deleteJob(jobName, groupName);
           }

           //30分钟任务
           JobDetail jobDetailHalfHour = new JobDetail("PollingJobHalfHour", null, PollingJobProxy.class);
           jobDetailHalfHour.getJobDataMap().put("PollingJob",new PollingJob(pepCommunicator,CIRCLE_UNIT_HOUR,30));
           CronTrigger halfHourTrigger = new CronTrigger(JOB_NAME_HALFHOUR,TRIGGER_GROUP_NAME);
           CronExpression cronExpression_halfhour = null;
           try {
              cronExpression_halfhour = new CronExpression("0 0/30 * * * ?");  //从0分开始，每30分钟触发一次
              halfHourTrigger.setCronExpression(cronExpression_halfhour);
              //注册作业
              scheduler.scheduleJob(jobDetailHalfHour, halfHourTrigger);

              if(! scheduler.isShutdown()){
                  scheduler.start();
              }
           } catch (ParseException e) {
           }


           //60分钟任务
           JobDetail jobDetailHour = new JobDetail("PollingJobHour", null, PollingJobProxy.class);
           jobDetailHour.getJobDataMap().put("PollingJob",new PollingJob(pepCommunicator,CIRCLE_UNIT_HOUR,60));
           CronTrigger hourTrigger = new CronTrigger(JOB_NAME_HOUR,TRIGGER_GROUP_NAME);
           CronExpression cronExpression_hour = null;
           try {
              cronExpression_hour = new CronExpression("0 0 0/1 * * ?");  //从0点开始，每1小时触发一次 //格式: [秒] [分] [时] [月中的天] [月] [周中的天] [年]
              hourTrigger.setCronExpression(cronExpression_hour);
              //注册作业
              scheduler.scheduleJob(jobDetailHour, hourTrigger);

              if(! scheduler.isShutdown()){
                  scheduler.start();
              }
           } catch (ParseException e) {
           }

/*            
            SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
            Scheduler sched = schedFact.getScheduler();
            sched.start();


            //1小时任务
            JobDetail jobDetailHour = new JobDetail("PollingJobHour", null, PollingJobProxy.class);
            jobDetailHour.getJobDataMap().put("PollingJob",new PollingJob(pepCommunicator,CIRCLE_UNIT_HOUR,60));
            triggerHour = TriggerUtils.makeMinutelyTrigger(60);

            triggerHour.setStartTime(TriggerUtils.getEvenMinuteDate(new Date())); //从下一个分钟开始
            triggerHour.setName("triggerHour");          

//            JobDetail jobDetailDay = new JobDetail("PollingJobDay", null, PollingJob.class);
//            jobDetailDay.getJobDataMap().put("circleUnit", CIRCLE_UNIT_DAY);
//            triggerDay = TriggerUtils.makeDailyTrigger(STARTUP_TIME, 0); // 每天23：00
//            triggerDay.setStartTime(TriggerUtils.getEvenSecondDate(new Date())); //从下一个秒开始
//            triggerDay.setName("triggerDay");
            sched.scheduleJob(jobDetailHour, triggerHour);
*/
             
        } catch (SchedulerException ex) {
            java.util.logging.Logger.getLogger(PollingProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
         
    }


}
