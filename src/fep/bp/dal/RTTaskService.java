/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fep.bp.dal;
import fep.bp.model.RealTimeTaskDAO;
import fep.bp.model.UpgradeTaskDAO;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
/**
 *
 * @author Thinkpad
 */
public interface RTTaskService {
    /**
     * 插入一条任务记录
     * @param task
     */
    public void insertTask(RealTimeTaskDAO task);

    /**
     *插入多条任务记录
     * @param Tasks
     */
    public void insertTasks(List<RealTimeTaskDAO> Tasks);

    /**
     * 获取未处理的任务记录
     * @return
     */
    public List<RealTimeTaskDAO> getTasks();

    /**
     * 
     * @param sequnceCode
     * @return
     */
    public RealTimeTaskDAO getTask(long sequnceCode);

    public List<RealTimeTaskDAO> getTasks(long sequnceCode);
    /**
     * 更新接收报文
     * @param sequnceCode
     * @param recvMsg
     */
    public void insertRecvMsg(long taskid,String logicAddress ,String recvMsg);


    /**
     * 获取回执码
     * @return
     */
    public int getSequnce();

    /**
     * 获取未同步的试跳任务记录
     * @return
     */
    public List<RealTimeTaskDAO> getTripTasks();

    /**
     * 同步的试跳任务记录
     * @return
     */
    public boolean InsertTripTaskInfo(int ps_id,String date,Date postTime,Date acceptTime,String tripResult,int task_Id);
    
    /**
     * 获取升级任务列表
     * @return 
     */
    public List<UpgradeTaskDAO> getUpgradeTasks();
    
    /**
     * 插入一条远程升级任务
     * @param task 
     */
    public void insertUpgradeTask(UpgradeTaskDAO task);
    
    /**
     * 插入终端升级文件
     * @param fileVersion
     * @param fileName
     * @param binFile 
     */
    public void insertUpgradeFile(String fileVersion,String fileName,InputStream binFile);
    
    /**
     * 更新升级任务信息
     * @param task 
     */
    public void updateUpgradeTask(UpgradeTaskDAO task);
    
}
