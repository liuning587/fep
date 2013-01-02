/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fep.bp.utils;

import java.util.Calendar;

/**
 *
 * @author THINKPAD
 */
public class DebugUtils {
    private long beginTime;
    private long endTime;
    public void beginTime()
    {
        beginTime =System.currentTimeMillis(); //排序前取得当前时间 
    }
    
    public void printUsedTime()
    {
        endTime = System.currentTimeMillis(); //排序后取得当前时间 
        Calendar c=Calendar.getInstance(); 
        c.setTimeInMillis(endTime-beginTime); 
        System.out.println("耗时: " + c.get(Calendar.MINUTE) + "分 " + c.get(Calendar.SECOND) + "秒 " + c.get(Calendar.MILLISECOND) + " 微秒"); 
    }
}
