/*
 * 主动上报报文处理器
 */
package fep.bp.processor;

import fep.bp.dal.DataService;
import fep.bp.model.Dto;
import fep.bp.model.Dto.DtoItem;
import fep.bp.utils.decoder.ClassTwoDataDecoder;
import fep.bp.utils.decoder.Decoder;
import fep.bp.utils.equipMap.EquipMap;
import fep.codec.protocol.gb.PmPacketData;
import fep.codec.protocol.gb.gb376.PmPacket376;
import fep.codec.protocol.gb.gb376.PmPacket376DA;
import fep.codec.protocol.gb.gb376.PmPacket376DT;
import fep.codec.protocol.gb.gb376.events.Packet376Event36;
import fep.codec.protocol.gb.gb376.events.Packet376Event42;
import fep.codec.protocol.gb.gb376.events.PmPacket376EventBase;
import fep.codec.protocol.gb.gb376.events.PmPacket376EventDecoder;
import fep.codec.utils.BcdDataBuffer;
import fep.codec.utils.BcdUtils;
import fep.meter645.Gb645MeterPacket;
import fep.mina.common.PepCommunicatorInterface;
import fep.mina.common.RtuAutoUploadPacketQueue;
import fep.system.SystemConst;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author LiJun
 */
public class UpLoadProcessor extends BaseProcessor {

    private final static Logger log = LoggerFactory.getLogger(UpLoadProcessor.class);
    private DataService dataService;
    private PepCommunicatorInterface pepCommunicator;//通信代理器
    private RtuAutoUploadPacketQueue upLoadQueue;//主动上报报文队列
    private EquipMap equipMap;
    private Decoder  decoder100;
    private Decoder  decoder101;
    public UpLoadProcessor(PepCommunicatorInterface pepCommunicator) {
        super();
        dataService = (DataService) cxt.getBean(SystemConst.DATASERVICE_BEAN);
        upLoadQueue = pepCommunicator.getRtuAutoUploadPacketQueueInstance();
        this.pepCommunicator = pepCommunicator;
        this.equipMap = (EquipMap) cxt.getBean("equipMap");
        this.equipMap.init();
        this.decoder100 = (Decoder)cxt.getBean("decoder100");
        this.decoder101 = (Decoder)cxt.getBean("decoder101");
    }

    @Override
    public void run() {
        while (true) {
            try {
                PmPacket376 packet = (PmPacket376) upLoadQueue.PollPacket();
                if (packet.getAfn() == 0x0C) {
                    decodeAndSaveClassOneData(packet);
                } else if(packet.getAfn() == 0x10){
                    decodeAndSaveClasTransMitData(packet);
                } else if (packet.getAfn() == 0x0D) {
                    decodeAndSaveClassTwoData(packet);
                } else if (packet.getAfn() == 0x0E) {
                    DecodeEventAndSave(packet);
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
    
    private Decoder getDecoder376()
    {
        return this.decoder100;
    }
    
    private Decoder getDecoder(int loubaoProtocol)
    {
        if(loubaoProtocol == 101) {
            return this.decoder101;
        }
        else {
            return this.decoder100;
        }
    }

    private void decodeAndSaveClassOneData(PmPacket376 packet) {
        String logicalAddr = packet.getAddress().getRtua();
        Dto dto = new Dto(logicalAddr, packet.getAfn());
        Decoder decoder = getDecoder376();
        decoder.decode2dto(packet, dto);
        dataService.insertRecvData(dto);
    }

    private void decodeAndSaveClasTransMitData(PmPacket376 packet) throws Exception {
        Gb645MeterPacket packet645= getDecoder376().getGb645MeterPacket(packet);
        String logicalAddr = packet.getAddress().getRtua();
        String loubaoAddr = packet645.getAddress().getAddress();
        Dto dto = new Dto(logicalAddr, packet.getAfn());
        int loubaoProtocol = this.equipMap.loubaoProtocol(logicalAddr,loubaoAddr);
        if(loubaoProtocol != -1)
        {
            Decoder decoder = getDecoder(loubaoProtocol);
            decoder.decode2dto_TransMit(packet, dto);
            int gpSn = this.equipMap.loubaoGpSn(logicalAddr, loubaoAddr);
            for(DtoItem dtoItem : dto.getDataItems())
            {
                dtoItem.gp = gpSn;
            }
            dataService.insertRecvData(dto);
        }    
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
                else if((event.GetEventCode() == 42)||(event.GetEventCode() == 55)) {//漏保事件42
                    Packet376Event42 event42 = (Packet376Event42) event;
                    saveLoubaoEvent(rtua, event42,42);
                }
                saveEvent(rtua, dt.getFn(), da.getPn(), event);
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
