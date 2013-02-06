/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fep.bp.processor.upgrade;

import fep.bp.dal.RTTaskService;
import fep.bp.model.UpgradeTaskDAO;
import fep.bp.processor.ProcessLevel;
import fep.bp.processor.ProcessorStatus;
import fep.bp.utils.encoder.Encoder;
import fep.bp.utils.encoder.encoder376.Encoder376;
import fep.codec.protocol.gb.PmPacketData;
import fep.codec.protocol.gb.gb376.PmPacket376;
import fep.codec.protocol.gb.gb376.PmPacket376DA;
import fep.codec.protocol.gb.gb376.PmPacket376DT;
import fep.mina.common.PepCommunicatorInterface;
import fep.mina.common.SequencedPmPacket;
import fep.system.SystemConst;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author THINKPAD
 */
public class UpgradeTask {
    private final static Logger log = LoggerFactory.getLogger(UpgradeTask.class);
    private final int TIME_OUT = 350*1000;
    private int taskID;
    private String rtua;
    private InputStream binFileStream;
    private int totalPacketNumber;
    private int currentSendNo;//当前发送的包序号
    private int binFileSize;
    private int lastFailFrameNo;//上一次升级失败是的包序号
    private String taskStatus;
    private ApplicationContext cxt;
    private RTTaskService taskService;
    private Encoder encoder;
    private Queue<SequencedPmPacket> rtuaUpgradeQueue;
    private Queue<SequencedPmPacket> rtuaUpgradeBackQueue;
    private PepCommunicatorInterface pepCommunicator;//通信代理器
    private Date startSendTicket;
    private ProcessorStatus status;
    private boolean haveUpgradeTask;
    /**
     * @return the taskStatus
     */
    public String getTaskStatus() {
        return taskStatus;
    }

    /**
     * @param taskStatus the taskStatus to set
     */
    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(ProcessorStatus status) {
        this.status = status;
    }

    /**
     * @return the haveUpgradeTask
     */
    public boolean isHaveUpgradeTask() {
        return haveUpgradeTask;
    }

    /**
     * @param haveUpgradeTask the haveUpgradeTask to set
     */
    public void setHaveUpgradeTask(boolean haveUpgradeTask) {
        this.haveUpgradeTask = haveUpgradeTask;
    }

    /**
     * @param lastFailFrameNo the lastFailFrameNo to set
     */
    public void setLastFailFrameNo(int lastFailFrameNo) {
        
        if(lastFailFrameNo ==0)
        {
            this.lastFailFrameNo = lastFailFrameNo;
            this.currentSendNo  =lastFailFrameNo;
        }
        else{
            this.lastFailFrameNo = lastFailFrameNo;
            this.currentSendNo  =lastFailFrameNo-1;
        }
    
    }
    
    private interface TaskStatus
    {
        public String Undealed = "0";
        public String Dealing = "1";
        public String Suncess = "2";
        public String Failed = "3";
    }
    
    public UpgradeTask()
    {
        currentSendNo = 0;
        haveUpgradeTask = false;
        this.startSendTicket =null;
        taskStatus = TaskStatus.Dealing;
        cxt = new ClassPathXmlApplicationContext(SystemConst.SPRING_BEANS);
        taskService = (RTTaskService) cxt.getBean(SystemConst.REALTIMETASK_BEAN);
        encoder = (Encoder376)cxt.getBean("encoder");
        rtuaUpgradeQueue = new ConcurrentLinkedQueue<SequencedPmPacket>();
        rtuaUpgradeBackQueue = new ConcurrentLinkedQueue<SequencedPmPacket>();
    }
    
    /**
     * 根据升级文件，进行组帧
     */
    public void EncodePacket() {
        try {
            byte[] binFile = new byte[binFileSize];
            binFileStream.read(binFile);

            List<PmPacket376> packetList = encoder.EncodeList_Upgrade(this.rtua, binFile);
            int frameNo = 0;
            totalPacketNumber = packetList.size();
            for (PmPacket376 packet : packetList) {
                if(frameNo >= this.lastFailFrameNo-1) { //断点续传
                    addUpgradePacket(packet);                
                }   
                frameNo++; 
            }
            binFileStream.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(UpgradeProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 插入返回报文对象
     * @param Rtua
     * @param packet 
     */
    public void addUpgradeBackPacket(SequencedPmPacket packet)
    {
        synchronized(this){
            this.rtuaUpgradeBackQueue.add(packet);
            
        }
    }
    
    public void CheckBackPacket()
    {
        synchronized(this){
            if(isHaveUpgradeTask())
            {
                if(!this.rtuaUpgradeBackQueue.isEmpty())
                {
                    SequencedPmPacket backPacket = rtuaUpgradeBackQueue.peek();
                    if(IsOK(backPacket))
                    {
                        if(currentSendNo<this.totalPacketNumber)
                        {
                            updateTask(TaskStatus.Dealing);
                            taskStatus = TaskStatus.Dealing;
                            sendNextPacket();
                        }
                        else{
                            updateTask(TaskStatus.Suncess);
                            taskStatus = TaskStatus.Suncess;     
                            this.status.updateStatus(rtua, ProcessLevel.Level3);
                            this.setHaveUpgradeTask(false);
                        }
                    }
                    else//否认
                    {                     
                        updateTask(TaskStatus.Failed);
                        taskStatus = TaskStatus.Failed;
                        log.info("收到否认帧失败！！！");
                    }
                }
                else//没有返回
                {
                    if (this.startSendTicket != null) {
                        Date checkTime =new Date();
                        if (checkTime.getTime() - this.startSendTicket.getTime() >= this.TIME_OUT) 
                        {
                            log.info("终端："+this.rtua+"超时失败！！！");
                            updateTask(TaskStatus.Failed);
                            taskStatus = TaskStatus.Failed;
                            emptyPacketQueue();
                            this.status.updateStatus(rtua, ProcessLevel.Level3);
                            this.startSendTicket = null;
                            this.setHaveUpgradeTask(false);
                        }
                    }
                    else
                    {
                        sendNextPacket();
                        taskStatus = TaskStatus.Dealing;
                    }
                }
            }
            
        }
    }
    
    private void sendNextPacket()
    {
        synchronized(this){
            SequencedPmPacket toPacket = this.rtuaUpgradeQueue.peek();
            if(toPacket != null)
            {
                this.pepCommunicator.SendPacket(toPacket.sequence, toPacket.pack, 0);
                currentSendNo++;
                this.startSendTicket = new Date();
                log.info("集中器【"+rtua+"】,下发升级报文第"+currentSendNo+"帧，共有"+totalPacketNumber+"帧");
            }
        }
    }
    
    private void updateTask(String status)
    {
        int failFrameNo = currentSendNo;
        UpgradeTaskDAO dao = new UpgradeTaskDAO();
        dao.setTaskId(taskID);
        dao.setLogicAddress(rtua);
        dao.setFailFrameNo(failFrameNo);
        float rate = ((float)currentSendNo) / (float)this.totalPacketNumber * 100;
        dao.setSchedule(rate);
        dao.setTaskStatus(status);
        taskService.updateUpgradeTask(dao);
    }
    
    private void emptyPacketQueue()
    {
        synchronized(this){
            this.rtuaUpgradeQueue.clear();
            this.rtuaUpgradeBackQueue.clear();
        }
    }
    
    private boolean IsOK(SequencedPmPacket backPacket)
    {
        boolean result = false;
        if(backPacket == null) {
            return result;
        }
        if(rtuaUpgradeQueue!=  null)
        {
            SequencedPmPacket toPacket = rtuaUpgradeQueue.peek();
            if(toPacket.sequence == backPacket.sequence)
            {
                result = (backPacket.pack.getAfn()==0x0F);//文件传输
                PmPacketData dataBuf = backPacket.pack.getDataBuffer();
                dataBuf.rewind();
                dataBuf.getDA(new PmPacket376DA());
                PmPacket376DT dt = new PmPacket376DT();
                dataBuf.getDT(dt);
                int Fn = dt.getFn();
                result = result && (Fn == 1);
                
                int SegNo  = (int) dataBuf.getBin(4);
                result = result &&(SegNo == this.currentSendNo-1);
                
                rtuaUpgradeQueue.poll();
                rtuaUpgradeBackQueue.poll();
            }
        }
        return result;
    }
    
    
    private void addUpgradePacket(PmPacket376 packet)
    {
        synchronized(this){
            SequencedPmPacket seqPacket = new SequencedPmPacket(taskService.getSequnce(),packet,SequencedPmPacket.Status.TO_BE_CONTINUE);
            this.rtuaUpgradeQueue.add(seqPacket);
        }
    }

    /**
     * @param pepCommunicator the pepCommunicator to set
     */
    public void setPepCommunicator(PepCommunicatorInterface pepCommunicator) {
        this.pepCommunicator = pepCommunicator;
    }

    /**
     * @param taskID the taskID to set
     */
    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    /**
     * @param rtua the rtua to set
     */
    public void setRtua(String rtua) {
        this.rtua = rtua;
    }

    /**
     * @param binFileStream the binFileStream to set
     */
    public void setBinFileStream(InputStream binFileStream) {
        this.binFileStream = binFileStream;
    }

    /**
     * @param binFileSize the binFileSize to set
     */
    public void setBinFileSize(int binFileSize) {
        this.binFileSize = binFileSize;
    }
    
    
}
