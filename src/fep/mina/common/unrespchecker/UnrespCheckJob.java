/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fep.mina.common.unrespchecker;

import fep.mina.common.PepCommunicatorInterface;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author THINKPAD
 */
public class UnrespCheckJob implements Job{
    private PepCommunicatorInterface communicator;
    public UnrespCheckJob(PepCommunicatorInterface pepCommunicator) {
        communicator = pepCommunicator;
    }
    
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        communicator.checkUndespPackets();
    }
    
}
