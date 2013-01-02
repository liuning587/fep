/*
 * 主动下发返回处理器
 */
package fep.bp.processor;

import fep.bp.dal.RTTaskService;
import fep.bp.processor.upgrade.UpgradeProcessor;
import fep.codec.utils.BcdUtils;
import fep.mina.common.PepCommunicatorInterface;
import fep.mina.common.RtuAutoUploadPacketQueue;
import fep.mina.common.RtuRespPacketQueue;
import fep.mina.common.SequencedPmPacket;
import fep.mina.common.SequencedPmPacket.Status;
import fep.mina.protocolcodec.gb.RtuCommunicationInfo;
import fep.system.SystemConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thinkpad
 */
public class ResponseDealer extends BaseProcessor {
    private final static Logger log = LoggerFactory.getLogger(ResponseDealer.class);
    private RTTaskService taskService;
    private PepCommunicatorInterface pepCommunicator;//通信代理器
    private RtuRespPacketQueue respQueue;//返回报文队列
    private RtuAutoUploadPacketQueue upLoadQueue;
    private UpgradeProcessor upgradeProcessor;

    public ResponseDealer(PepCommunicatorInterface pepCommunicator,UpgradeProcessor upgradeProcessor) {
        super();
        taskService = (RTTaskService) cxt.getBean(SystemConst.REALTIMETASK_BEAN);
        respQueue = pepCommunicator.getRtuRespPacketQueueInstance();
        upLoadQueue = pepCommunicator.getRtuAutoUploadPacketQueueInstance();
        this.pepCommunicator = pepCommunicator;
        this.upgradeProcessor = upgradeProcessor;
    }

    @Override
    public void run() {
        while (true) {
            //检查返回
            try {
                SequencedPmPacket packet = respQueue.PollPacket();
                
                if(packet == null)
                    continue;
                if ((packet.status == Status.SUSSESS) || (packet.status == Status.TO_BE_CONTINUE)) {
                    //对主动轮召任务返回，直接插入主动上报任务队列处理，后续统一数据处理
                    if (packet.pack.getAddress().getMastStationId() == RtuCommunicationInfo.AUTO_CALL_TASK_HOSTID)
                    {
                        upLoadQueue.addPacket(packet.pack);
                    } 
                    //对漏保恢复报文，走短信回复处理
                    else if (packet.pack.getAddress().getMastStationId() == RtuCommunicationInfo.LOUBAO_OPRATE_HOSTID)//漏保恢复尝试
                    {
                        SmsRespProcessor.receiveRtuPacket(packet.pack);
                    } 
                    //升级返回报文，走升级处理器
                    else if(packet.pack.getAfn() == 0x0F)
                    {
                        String rtua = packet.pack.getAddress().getRtua();
                        this.upgradeProcessor.addUpgradeBackPacket(rtua, packet);
                    }
                    else //实时召测任务返回处理
                    {
                        taskService.insertRecvMsg(packet.sequence, packet.pack.getAddress().getRtua(), BcdUtils.binArrayToString(packet.pack.getValue()));
                    }
                }
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }

    }

}
