/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fep.mina.common.unrespchecker;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author THINKPAD
 */
public class UnrespCheckJobProxy implements Job{
    private UnrespCheckJob job = null;
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        this.job = (UnrespCheckJob) jobDataMap.get("UnrespCheckJob");
        this.job.execute(null);
    }
    
}
