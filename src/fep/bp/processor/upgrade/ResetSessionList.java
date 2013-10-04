/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fep.bp.processor.upgrade;

import java.util.ArrayList;

/**
 *
 * @author THINKPAD
 */
public class ResetSessionList {
    private  ArrayList<String>  sessionList = new ArrayList<String>();
    public void addRtua(String rtua)
    {
        synchronized(this){
            if(!sessionList.contains(rtua))
            {
                sessionList.add(rtua);
            }
        }        
    }
    
    public void removeRtua(String rtua)
    {
        sessionList.remove(rtua);
    }
}
