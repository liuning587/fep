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
public class Packet376Event51 extends PmPacket376EventBase{

    private boolean isHappen;//是否发生;置“1”：发生，置“0”：恢复。
    private float apparentPower;//发生时的视在功率
    private float apparentPower_Limit;//发生时的视在功率限值
    @Override
    protected void DecodeEventDetail(BcdDataBuffer eventData, int len) {
        PmPacketData dataBuffer = new PmPacketData(eventData.getRowIoBuffer());
        if (dataBuffer.restBytes() < 7) {
            return;
        }
        this.isHappen = ((byte) (dataBuffer.getByte() & 0x80) >0);
        this.apparentPower = dataBuffer.getA23().getValue();
        this.apparentPower_Limit = dataBuffer.getA23().getValue();
        StringBuilder sb = new StringBuilder();
        if(isHappen)
        {
            sb.append("发生变压器过负荷保护，");
        }
        else{
            sb.append("变压器过负荷保护恢复，");
        }
        sb.append("发生时的视在功率:").append(apparentPower);
        sb.append("发生时的视在功率限值:").append(apparentPower_Limit);
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
     * @return the apparentPower
     */
    public float getApparentPower() {
        return apparentPower;
    }


    /**
     * @return the apparentPower_Limit
     */
    public float getApparentPower_Limit() {
        return apparentPower_Limit;
    }


    
}
