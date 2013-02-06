/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fep.bp.processor;

import java.util.HashMap;

/**
 *
 * @author THINKPAD
 */
public class ProcessorStatus {
    private HashMap<String,Integer> rtuStatusMap;
    
    public ProcessorStatus()
    {
        rtuStatusMap = new HashMap<String,Integer>();
    }
    
    public synchronized void updateStatus(String logicalAddr,Integer ProcessLevel)
    {
        rtuStatusMap.put(logicalAddr, ProcessLevel);
    }
    
    public synchronized boolean canProcess(String logicalAddr,Integer ProcessLeve)
    {
        if(rtuStatusMap.containsKey(logicalAddr))
        {
            return (rtuStatusMap.get(logicalAddr) >= ProcessLeve);
        }
        else {
            return true;
        }
    }
}
