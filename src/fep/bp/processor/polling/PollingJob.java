/*
 * 主站轮召处理器
 */
package fep.bp.processor.polling;

import fep.bp.dal.TaskService;
import fep.bp.model.CommanddItemDAO;
import fep.bp.model.TermTaskDAO;
import fep.bp.processor.ProcessLevel;
import fep.bp.processor.ProcessorStatus;
import fep.bp.realinterface.mto.CollectObject;
import fep.bp.realinterface.mto.CollectObject_TransMit;
import fep.bp.realinterface.mto.CommandItem;
import fep.bp.utils.AFNType;
import fep.bp.utils.BaudRate;
import fep.bp.utils.MeterType;
import fep.bp.utils.SerialPortPara;
import fep.bp.utils.encoder.Encoder;
import fep.bp.utils.encoder.encoder376.Encoder376;
import fep.codec.protocol.gb.PmPacket;
import fep.codec.protocol.gb.gb376.PmPacket376;
import fep.codec.utils.BcdUtils;
import fep.common.exception.BPException;
import fep.mina.common.PepCommunicatorInterface;
import fep.mina.common.RtuRespPacketQueue;
import fep.system.SystemConst;
import java.util.List;
import java.util.logging.Level;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Thinkpad
 */
public class PollingJob implements Job {

    private final static Logger log = LoggerFactory.getLogger(PollingJob.class);
    private TaskService taskService;
    private PepCommunicatorInterface pepCommunicator;//通信代理器
    private RtuRespPacketQueue respQueue;//返回报文队列
    private ApplicationContext cxt;
    private Encoder encoder;
    private int circleUnit;
    private int time_interval;
    private int sequenceCode = 0;
    private ProcessorStatus status;
    private int getsequenceCode() {
        return sequenceCode++;
    }

    public PollingJob(PepCommunicatorInterface pepCommunicator, int circleUnit,int time_interval,ProcessorStatus status) {
        cxt = new ClassPathXmlApplicationContext(SystemConst.SPRING_BEANS);
        taskService = (TaskService) cxt.getBean("taskService");
        //converter = (Converter) cxt.getBean("converter");
        encoder = (Encoder376) cxt.getBean("encoder");
        this.pepCommunicator = pepCommunicator;
        this.circleUnit = circleUnit;
        this.time_interval =time_interval;
        this.status = status;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        List<TermTaskDAO> TermTaskList = taskService.getPollingTask(circleUnit,time_interval);
        if (null != TermTaskList) {
            for (TermTaskDAO task : TermTaskList) {
                try {
                    DoTask(task);
                } catch (BPException ex) {
                    java.util.logging.Logger.getLogger(PollingJob.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private String getBaudRate(byte baudRate)
    {
        switch(baudRate)
        {
            case 0:{return BaudRate.bps_300;}
            case 1:{return BaudRate.bps_600;}
            case 2:{return BaudRate.bps_1200;}
            case 3:{return BaudRate.bps_2400;}
            case 4:{return BaudRate.bps_4800;}
            case 5:{return BaudRate.bps_7200;}
            case 6:{return BaudRate.bps_9600;}
            case 7:{return BaudRate.bps_19200;}
            default: return BaudRate.bps_9600;
        }
    }

    private void DoTask(TermTaskDAO task) throws BPException {
        List<CommanddItemDAO> CommandItemList = task.getCommandItemList();
        for (CommanddItemDAO commandItemDao : CommandItemList) {
            CollectObject object = new CollectObject();
            CommandItem Item = new CommandItem();
            String CommandCode = commandItemDao.getCommandItemCode();
            Item.setIdentifier(CommandCode);
            object.AddCommandItem(Item);

            object.setLogicalAddr(task.getLogicAddress());
            object.setMpSn(new int[]{task.getGp_sn()});
            object.setEquipProtocol(task.getProtocol_No());
            
            if(task.getAFN() == AFNType.AFN_TRANSMIT){  //中继任务
                CollectObject_TransMit object_trans = new CollectObject_TransMit();
                object_trans.setFuncode((byte)1);//读数据
                object_trans.setMeterAddr(task.getGp_addr()); //表地址
                object_trans.setMeterType(MeterType.Meter645);
                object_trans.setPort(task.getTransmitPort());
                object_trans.setMpSn(0);
                SerialPortPara spp = new SerialPortPara();
                spp.setBaudrate(getBaudRate(task.getTransmitBaudrate()));
                spp.setCheckbit(1);
                spp.setStopbit(1);
                spp.setOdd_even_bit(0);//0：不校验 1:偶校验
                spp.setDatabit(8);
                object_trans.setSerialPortPara(spp);
                object_trans.setTerminalAddr(task.getLogicAddress());
                object_trans.setWaitforByte((byte)5);
                object_trans.setWaitforPacket((byte)10);
                object_trans.addCommandItem(Item);
                //List<PmPacket376> packetList =  converter.CollectObject_TransMit2PacketList(object_trans,  new StringBuffer());
                List<PmPacket376> packetList =  encoder.EncodeList_TransMit(object_trans,new StringBuilder());
                if(null != packetList){
                    for(PmPacket376 pack:packetList){
                        pack.getAddress().setMastStationId((byte) 2);
                        if(this.status.canProcess(pack.getAddress().getRtua(), ProcessLevel.Level2))
                        {
                            this.pepCommunicator.SendPacket(this.getsequenceCode(), pack,2,(byte)0);
                            log.info("向终端：["+task.getLogicAddress()+"] 下发轮召报文（命令项;" + Item.getIdentifier() + "）：" + BcdUtils.binArrayToString(pack.getValue()));
                        }
                        else
                        {
                            log.info("终端："+pack.getAddress().getRtua()+"存在更高优先级任务，暂时无法执行该任务");
                        }
                    }
                }
            }
                
            else {
                List<PmPacket376> packList = encoder.Encode(object, task.getAFN());
                for(PmPacket packet : packList)
                {
                    PmPacket376 packet376 = (PmPacket376)packet;
                    packet.getAddress().setMastStationId((byte) 2);
                    pepCommunicator.SendPacket(this.getsequenceCode(), packet,2,(byte)0);
                    log.info("向终端：["+task.getLogicAddress()+"] 下发轮召报文（命令项;" + Item.getIdentifier() + "）：" + BcdUtils.binArrayToString(packet.getValue()));
                }
            }            
        }
    }


}
