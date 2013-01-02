/*
 * 记录每个终端的通讯状态,管理终端通讯过程
 */
package fep.mina.protocolcodec.gb;

import fep.bp.utils.DebugUtils;
import fep.codec.protocol.gb.ControlCode;
import fep.codec.protocol.gb.EventCountor;
import fep.codec.protocol.gb.PmPacket;
import fep.codec.protocol.gb.Seq;
import fep.codec.protocol.gb.gb376.PmPacket376;
import fep.codec.protocol.gb.gb376.PmPacket376Factroy;
import fep.mina.common.RtuRespPacketQueue;
import fep.mina.common.SequencedPmPacket;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author luxiaochung
 */
public class RtuCommunicationInfo {

    private String rtua;
    private IoSession session;
    private Queue<SeqPacket> unsendPacket2;   //待发送帧队列，低优先级
    private Queue<SeqPacket> unsendPacket1;   //待发送帧队列，低优先级
    private Queue<SeqPacket> unsendPacket0;  //待发送帧队列，高优先级0
    private byte currentSeq;        //下一个主动发送帧的帧序号
    private boolean idle;           //是否可以发送下行帧
    private byte currentRespSeq;    //当前等待回复的帧序号
    private byte maxRetryTimes;     //当没有收到终端回应帧时最大重复发送次数
    private byte currentSendTimes;
    private Date currentSendTicket;
    private PmPacket currentPacket;
    private int currentSequence;
    private boolean isTcp;
    private byte lastEc1;
    private byte lastEc2;
    private static final byte EC_CALL_HOST_ID = 3;   //读取3类数据时使用的主站ID
    public final static byte AUTO_CALL_TASK_HOSTID = 2;//主动轮召任务返回
    public final static byte LOUBAO_OPRATE_HOSTID = 4;//漏保恢复尝试
    public final static byte UPGRADE = 5;//漏保恢复尝试
    private static final long TIME_OUT = 20 * 1000;
    private final static Logger LOGGER = LoggerFactory.getLogger(RtuCommunicationInfo.class);
    private class SeqPacket {

        private int sequence;
        private PmPacket pack;

        private SeqPacket(int sequence, PmPacket pack) {
            this.sequence = sequence;
            this.pack = pack;
        }
    }

    public RtuCommunicationInfo(String rtua) {
        super();
        this.rtua = rtua;
        currentSeq = 0;
        maxRetryTimes = 3;
        session = null;
        idle = true;
        lastEc1 = 0;
        lastEc2 = 0;
        unsendPacket2 = new ConcurrentLinkedQueue<SeqPacket>();
        unsendPacket1 = new ConcurrentLinkedQueue<SeqPacket>();
        unsendPacket0 = new ConcurrentLinkedQueue<SeqPacket>();
    }

    public RtuCommunicationInfo(String rtua, IoSession session) {
        this(rtua);
        this.session = session;
    }

    public synchronized RtuCommunicationInfo setMaxRetryTimes(byte maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
        return this;
    }

    public synchronized IoSession getSession() {
        return this.session;
    }

    public synchronized RtuCommunicationInfo setTcpSession(IoSession session) {
        this.isTcp = true;
        this.session = session;
        this.sendNextPacket(true);
        return this;
    }

    public synchronized void disconnected() {
        this.session = null;
    }

    public synchronized void receiveRtuUploadPacket(PmPacket packet) {
        ControlCode ctrlCode = packet.getControlCode();
        Seq seq = packet.getSeq();
        if ((!ctrlCode.getIsOrgniger()) && ctrlCode.getIsUpDirect()
                && (seq.getSeq() == this.currentRespSeq)) { //上行响应帧
            //LOGGER.info("向后台发送收到的响应帧"+packet.toString());
            if (seq.getIsFinishFrame()) {
                RtuRespPacketQueue.instance().addPacket(
                        new SequencedPmPacket(this.currentSequence,
                        packet, SequencedPmPacket.Status.SUSSESS));
                this.idle = true;
                sendNextPacket(false);
            } else {
                RtuRespPacketQueue.instance().addPacket(
                        new SequencedPmPacket(this.currentSequence,
                        packet, SequencedPmPacket.Status.TO_BE_CONTINUE));
                this.currentRespSeq++;
                this.currentRespSeq &= 0x0F;
                this.currentSendTicket = new Date();
            }
        }
        else{
           LOGGER.info("非预期的终端响应帧，非响应帧或者非上行帧或者seq不一致"+packet.toString());
        }
    }

    public synchronized void callRtuEventRecord(EventCountor ec) {
        PmPacket376 pack1 = PmPacket376Factroy.makeCallEventRecordPacket(RtuCommunicationInfo.EC_CALL_HOST_ID,
                this.rtua, 1, this.lastEc1, ec.getEc1());
        sendPacket(0, pack1, 0);

        PmPacket376 pack2 = PmPacket376Factroy.makeCallEventRecordPacket(RtuCommunicationInfo.EC_CALL_HOST_ID,
                this.rtua, 2, this.lastEc2, ec.getEc2());
        sendPacket(0, pack2, 0);
    }

    public synchronized void sendPacket(int sequence, PmPacket packet, int priorityLevel) {
        addPacket(sequence, packet, priorityLevel);
        if (this.idle) {
            sendNextPacket(false);
        } else {
            LOGGER.info("Send packet: " + this.rtua + " not idle, sequence=" + sequence
                    + ", pack=" + packet.toString());
        }
    }

    private void addPacket(int sequence, PmPacket packet, int priorityLevel) {
        switch(priorityLevel)
        {
            case 0:{
                this.unsendPacket0.add(new SeqPacket(sequence, packet));
                break;
            }
            case 1:{
                this.unsendPacket1.add(new SeqPacket(sequence, packet));
                break;
            }
            case 2:{
                this.unsendPacket2.add(new SeqPacket(sequence, packet));
                break;
            }
        }
    }

    private void sendNextPacket(boolean forceSend) {
        if (this.idle) {
            SeqPacket seqPacket = pollPacket();
            if (seqPacket != null) {
                this.idle = false;
                this.currentSequence = seqPacket.sequence;
                this.currentSendTimes = 0;
                this.currentPacket = seqPacket.pack;
                this.currentPacket.getSeq().setSeq(this.currentSeq);
                this.currentRespSeq = this.currentSeq;
                this.currentSeq = (byte) ((++this.currentSeq) & 0x0F);
                doSendPacket();
            } else {
                this.idle = true;
            }
        } else {
            boolean firstSent = this.currentSendTimes == 1;
            if ((forceSend) && (!firstSent) && (new Date().getTime() - this.currentSendTicket.getTime() > 1000)) {
                this.doSendPacket();
            }
        }
    }

    private SeqPacket pollPacket() {
        
        
        SeqPacket seqPacket = unsendPacket0.poll();
        if (seqPacket != null) {
            return seqPacket;
        } else {
            seqPacket = unsendPacket1.poll();
            if (seqPacket != null) { 
                return seqPacket;
            }
            else{
                return unsendPacket2.poll();
            }
        }
    }

    private void doSendPacket() {
        if (this.currentPacket == null) {
            this.sendNextPacket(false);
        } else {
            synchronized (this) {
                this.currentSendTicket = new Date();
                this.currentSendTimes++;
            }
            if (this.currentSendTimes <= maxRetryTimes) {
                if (this.session != null) {
                    LOGGER.info("DoSend: " + rtua + " sequence="
                            + this.currentSequence + ", pack=" + this.currentPacket.toString());
                    this.session.write(this.currentPacket);
                } else {
                    LOGGER.info("DoSend: " + rtua + " not online, sequence="
                            + this.currentSequence + ", pack=" + this.currentPacket.toString());
                }

                if (!this.currentPacket.getControlCode().getIsOrgniger()) {
                    this.sendNextPacket(false);
                }
            } else {
                this.sendNextPacket(false);
            }
        }
    }

    /**
     * 到达重复发送检查点，重复发送是由外部定时器发起的，到达一个检查节拍时
     * 向所有Rtu通讯对象发送到达重复检查点消息
     */
    public synchronized void checkNotResponed(Date checkTime) {
        if (this.currentSendTicket == null) {
            return;
        }
        if (this.idle) {
            return;
        }
        if (checkTime.getTime() - this.currentSendTicket.getTime() >= RtuCommunicationInfo.TIME_OUT) {
            if (this.currentSendTimes > this.maxRetryTimes) {
                RtuRespPacketQueue.instance().addPacket(
                        new SequencedPmPacket(this.currentSequence, this.currentPacket,
                        SequencedPmPacket.Status.TIME_OUT));
                this.idle = true;
                LOGGER.info(rtua + " 超时,发送下一包.（发送队列：0级数据包：" + this.unsendPacket0.size() + "个，1级数据包 " + this.unsendPacket1.size() + " 个,2级数据包" + this.unsendPacket2.size() + " 个" +" 检查时间=" + checkTime.toString() + ", 上次发送时间=" + this.currentSendTicket.toString()+")");
                sendNextPacket(false);
            } else {
                LOGGER.info(rtua + " 超时, 重发.（发送队列：0级数据包： " + this.unsendPacket0.size() + "个，1级数据包" + this.unsendPacket1.size() + " 个,2级数据包" + this.unsendPacket2.size() + " 个"+ " 检查时间=" + checkTime.toString() + ", 上次发送时间=" + this.currentSendTicket.toString()+")");
                doSendPacket();
            }
        }
    }
}
