/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fep.codec.protocol.gb.gb376.events;

import fep.codec.protocol.gb.PmPacketData;
import fep.codec.utils.BcdDataBuffer;

/**
 *
 * @author THINKPAD
 */
public class Packet376Event53 extends PmPacket376EventBase{
    private boolean isHappen;//是否发生;置“1”：发生，置“0”：恢复。
    private double oilTemp;//发生时的油温
    private double oilTemp_LIMIT;//发生时的油温限值
    @Override
    protected void DecodeEventDetail(BcdDataBuffer eventData, int len) {
        PmPacketData dataBuffer = new PmPacketData(eventData.getRowIoBuffer());
        if (dataBuffer.restBytes() < 5) {
            return;
        }
        this.isHappen = ((byte) (dataBuffer.getByte() & 0x80) >0);
        this.oilTemp = dataBuffer.getA9().getValue();
        this.oilTemp_LIMIT = dataBuffer.getA9().getValue();
        
        StringBuilder sb = new StringBuilder();
        if(isHappen)
        {
            sb.append("发生变压器过热保护，");
        }
        else{
            sb.append("变压器过热保护恢复，");
        }
        sb.append("发生时的油温:").append(oilTemp).append(" ℃");
        sb.append("发生时的油温限值:").append(oilTemp_LIMIT).append(" ℃");
        sb.append("事件发生时间:").append(this.eventTime);
        this.eventDetail = sb.toString();
    }

    /**
     * @return the isHappen
     */
    public boolean isIsHappen() {
        return isHappen;
    }

    /**
     * @return the oilTemp
     */
    public double getOilTemp() {
        return oilTemp;
    }

    /**
     * @return the oilTemp_LIMIT
     */
    public double getOilTemp_LIMIT() {
        return oilTemp_LIMIT;
    }
    
}
