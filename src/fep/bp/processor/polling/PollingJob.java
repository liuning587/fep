/*
 * 主站轮召处理器
 */
package fep.bp.processor.polling;

import java.util.List;
import java.util.logging.Level;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import fep.codec.protocol.gb.gb376.PmPacket376;
import fep.common.exception.BPException;
import fep.mina.common.PepCommunicatorInterface;
import fep.mina.common.RtuRespPacketQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import fep.bp.dal.TaskService;
import fep.bp.model.CommanddItemDAO;

import fep.bp.model.TermTaskDAO;
import fep.bp.realinterface.mto.CollectObject;
import fep.bp.realinterface.mto.CollectObject_TransMit;
import fep.bp.realinterface.mto.CommandItem;
import fep.bp.utils.AFNType;
import fep.bp.utils.BaudRate;
import fep.bp.utils.MeterType;
import fep.bp.utils.SerialPortPara;
import fep.bp.utils.encoder.Encoder;
import fep.bp.utils.encoder.encoder376.Encoder376;

import fep.codec.utils.BcdUtils;
import fep.system.SystemConst;

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
    //private Converter converter;
    private Encoder encoder;
    private int circleUnit;
    private int time_interval;
    private int sequenceCode = 0;

    private int getsequenceCode() {
        return sequenceCode++;
    }

    public PollingJob(PepCommunicatorInterface pepCommunicator, int circleUnit,int time_interval) {
        cxt = new ClassPathXmlApplicationContext(SystemConst.SPRING_BEANS);
        taskService = (TaskService) cxt.getBean("taskService");
        //converter = (Converter) cxt.getBean("converter");
        encoder = (Encoder376) cxt.getBean("encoder");
        this.pepCommunicator = pepCommunicator;
        this.circleUnit = circleUnit;
        this.time_interval =time_interval;
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
            
            if(task.getAFN() == AFNType.AFN_TRANSMIT){  //中继任务
                CollectObject_TransMit object_trans = new CollectObject_TransMit();
                object_trans.setFuncode((byte)1);//读数据
                object_trans.setMeterAddr(task.getGp_addr()); //表地址
                object_trans.setMeterType(MeterType.Meter645);
                object_trans.setPort((byte)1);
                object_trans.setMpSn(task.getGp_sn());
                SerialPortPara spp = new SerialPortPara();
                spp.setBaudrate(BaudRate.bps_9600);
                spp.setCheckbit(0);
                spp.setStopbit(1);
                spp.setOdd_even_bit(1);
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
                        this.pepCommunicator.SendPacket(this.getsequenceCode(), pack,2);
                        log.info("向终端：["+task.getLogicAddress()+"] 下发轮召报文（命令项;" + Item.getIdentifier() + "）：" + BcdUtils.binArrayToString(pack.getValue()));
                    }
                }
            }
                
            else {
                PmPacket376 packet =  (PmPacket376)encoder.Encode(object, task.getAFN());
                packet.getAddress().setMastStationId((byte) 2);
                //converter.CollectObject2Packet(object, packet, task.getAFN(), new StringBuffer(), new StringBuffer());

                pepCommunicator.SendPacket(this.getsequenceCode(), packet,2);
                log.info("向终端：["+task.getLogicAddress()+"] 下发轮召报文（命令项;" + Item.getIdentifier() + "）：" + BcdUtils.binArrayToString(packet.getValue()));
            }            
        }
    }
}
