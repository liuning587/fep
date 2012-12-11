/*
 * 主动上报报文处理器
 */
package fep.bp.processor;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fep.bp.dal.DataService;
import fep.bp.model.Dto;
import fep.bp.utils.Converter;
import fep.bp.utils.decoder.ClassTwoDataDecoder;
import fep.bp.utils.decoder.Decoder;
import fep.codec.protocol.gb.PmPacketData;
import fep.codec.protocol.gb.gb376.Packet376Event36;
import fep.codec.protocol.gb.gb376.Packet376Event42;
import fep.codec.protocol.gb.gb376.PmPacket376;
import fep.codec.protocol.gb.gb376.PmPacket376DA;
import fep.codec.protocol.gb.gb376.PmPacket376DT;
import fep.codec.protocol.gb.gb376.PmPacket376EventBase;
import fep.codec.protocol.gb.gb376.PmPacket376EventDecoder;
import fep.codec.utils.BcdDataBuffer;
import fep.codec.utils.BcdUtils;
import fep.mina.common.PepCommunicatorInterface;
import fep.mina.common.RtuAutoUploadPacketQueue;
import fep.system.SystemConst;

/**
 *
 * @author LiJun
 */
public class UpLoadProcessor extends BaseProcessor {

    private final static Logger log = LoggerFactory.getLogger(ResponseDealer.class);
    private DataService dataService;
    private PepCommunicatorInterface pepCommunicator;//通信代理器
    private RtuAutoUploadPacketQueue upLoadQueue;//主动上报报文队列
    //private Converter converter;
    private Decoder  decoder;
    public UpLoadProcessor(PepCommunicatorInterface pepCommunicator) {
        super();
        dataService = (DataService) cxt.getBean(SystemConst.DATASERVICE_BEAN);
        upLoadQueue = pepCommunicator.getRtuAutoUploadPacketQueueInstance();
        this.pepCommunicator = pepCommunicator;
       // this.converter = (Converter) cxt.getBean("converter");
        this.decoder = (Decoder)cxt.getBean("decoder");
    }

    @Override
    public void run() {
        while (true) {
            try {
                PmPacket376 packet = (PmPacket376) upLoadQueue.PollPacket();
                if (packet.getAfn() == 0x0C) {
                    decodeAndSaveClassOneData(packet);
                    //log.info("对报文： " + BcdUtils.binArrayToString(packet.getValue())+" 做入库处理成功");
                } else if(packet.getAfn() == 0x10){
                    decodeAndSaveClasTransMitData(packet);
                   // log.info("对报文： " + BcdUtils.binArrayToString(packet.getValue())+" 做入库处理成功");
                } else if (packet.getAfn() == 0x0D) {
                    decodeAndSaveClassTwoData(packet);
                   // log.info("对报文： " + BcdUtils.binArrayToString(packet.getValue())+" 做入库处理成功");
                } else if (packet.getAfn() == 0x0E) {
                    DecodeEventAndSave(packet);
                    log.info("对报文： " + BcdUtils.binArrayToString(packet.getValue())+" 做入库处理成功");
                } else {
                    log.error("收到不支持的主动上送报文类" + BcdUtils.binArrayToString(packet.getValue()));
                }
            } catch (InterruptedException ex) {
                break;
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
    }

    private void decodeAndSaveClassOneData(PmPacket376 packet) {
        Dto dto = new Dto(packet.getAddress().getRtua(), packet.getAfn());
        this.decoder.decode2dto(packet, dto);
        dataService.insertRecvData(dto);
    }

    private void decodeAndSaveClasTransMitData(PmPacket376 packet) {
        Dto dto = new Dto(packet.getAddress().getRtua(), packet.getAfn());
        this.decoder.decode2dto_TransMit(packet, dto);
        dataService.insertRecvData(dto);
    }

    private void decodeAndSaveClassTwoData(PmPacket376 packet) {
        Dto classTwoDto = ClassTwoDataDecoder.Decode(packet);
        dataService.insertRecvData(classTwoDto);
    }

    private void DecodeEventAndSave(PmPacket376 packet) {
        String rtua = packet.getAddress().getRtua();
        PmPacketData data = packet.getDataBuffer();
        data.rewind();
        PmPacket376DA da = new PmPacket376DA();
        PmPacket376DT dt = new PmPacket376DT();
        while (data.restBytes() >= 4) {
            data.getDA(da);
            data.getDT(dt);
            List<PmPacket376EventBase> events = PmPacket376EventDecoder.decode(new BcdDataBuffer(data.getRowIoBuffer()));

            for (PmPacket376EventBase event : events) {
                if (event.GetEventCode() == 36) {//漏保事件36
                    Packet376Event36 event36 = (Packet376Event36) event;
                    saveLoubaoEvent(rtua, event36,36);
                }
                else if(event.GetEventCode() == 42) {//漏保事件42
                    Packet376Event42 event42 = (Packet376Event42) event;
                    saveLoubaoEvent(rtua, event42,42);
                }
                else {
                    saveEvent(rtua, dt.getFn(), da.getPn(), event);
                }
            }
        }
    }

    private void saveLoubaoEvent(String rtua, PmPacket376EventBase event,int eventCode) {
        switch(eventCode)
        {
            case 36:{
                this.dataService.insertLBEvent36(rtua, (Packet376Event36)event);break;
            }
            case 42:{
                this.dataService.insertLBEvent42(rtua, (Packet376Event42)event);break;
            }
        }
        
    }

    private void saveEvent(String rtua, int fn, int pn, PmPacket376EventBase event) {
        this.dataService.insertEvent(rtua, event);
    }
}
