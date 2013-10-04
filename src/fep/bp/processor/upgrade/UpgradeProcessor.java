/*
 * 远程升级处理器
 */
package fep.bp.processor.upgrade;

import fep.bp.dal.RTTaskService;
import fep.bp.model.UpgradeTaskDAO;
import fep.bp.processor.BaseProcessor;
import fep.bp.processor.ProcessLevel;
import fep.bp.processor.RealTimeSender;
import fep.mina.common.PepCommunicatorInterface;
import fep.mina.common.SequencedPmPacket;
import fep.system.SystemConst;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author THINKPAD
 */
public class UpgradeProcessor extends BaseProcessor{
    private final static Logger log = LoggerFactory.getLogger(UpgradeProcessor.class);
    private RTTaskService taskService;
    private PepCommunicatorInterface pepCommunicator;//通信代理器
    private HashMap<String,UpgradeTask> upgradeTaskMap;
    private ResetSessionList resetSessionList;//针对通信超时的通道进行断开重建
    
    public UpgradeProcessor(PepCommunicatorInterface pepCommunicator) {
        super();
        taskService = (RTTaskService) cxt.getBean(SystemConst.REALTIMETASK_BEAN);
        upgradeTaskMap = new HashMap<String,UpgradeTask>();
        this.pepCommunicator = pepCommunicator;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(RealTimeSender.class.getName()).log(Level.SEVERE, null, ex);
            }
            //加载新的升级任务
            LoadUpgradeTasks(); 
            
            //检查任务执行情况
            CheckUpgradeTask();
        }
    }
    
    public void addUpgradeBackPacket(String Rtua,SequencedPmPacket packet)
    {
        synchronized(this){
            if(upgradeTaskMap.containsKey(Rtua))
            {
                UpgradeTask upgradeTask = upgradeTaskMap.get(Rtua);
                if(upgradeTask!= null)
                {
                    upgradeTask.addUpgradeBackPacket(packet);
                }
            }
        }
    }
    
    private void LoadUpgradeTasks()
    {
        List<UpgradeTaskDAO> tasks = taskService.getUpgradeTasks();
        if (null != tasks) {
            for (UpgradeTaskDAO task : tasks) {
                String rtua = task.getLogicAddress();
                if (task.getBinFileSize() > 0) {
                    int taskId = task.getTaskId();
                    InputStream fin = task.getBinFile();
                    int fileSize = task.getBinFileSize();
                    UpgradeTask upgradeTask = new UpgradeTask();
                    upgradeTask.setHaveUpgradeTask(true);
                    upgradeTask.setStatus(status);
                    upgradeTask.setTaskID(taskId);
                    upgradeTask.setRtua(rtua);
                    upgradeTask.setBinFileStream(fin);
                    upgradeTask.setBinFileSize(fileSize);
                    upgradeTask.setPepCommunicator(pepCommunicator);
                    upgradeTask.setLastFailFrameNo(task.getFailFrameNo());
                    upgradeTask.EncodePacket();
                    this.upgradeTaskMap.put(rtua, upgradeTask);
                    this.status.updateStatus(rtua, ProcessLevel.Level0);
                }
            }
        }
    }
    
    private void CheckUpgradeTask()
    {
        //检查升级响应报文，如果成功，下发下一帧
        Iterator iter = upgradeTaskMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String rtua = (String) entry.getKey();
            UpgradeTask upgradeTask = (UpgradeTask) entry.getValue();
            upgradeTask.CheckBackPacket();
        }
    }  

    /**
     * @param resetSessionList the resetSessionList to set
     */
    public void setResetSessionList(ResetSessionList resetSessionList) {
        this.resetSessionList = resetSessionList;
    }
}
