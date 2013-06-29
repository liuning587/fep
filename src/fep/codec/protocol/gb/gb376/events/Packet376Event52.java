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
public class Packet376Event52 extends PmPacket376EventBase{
    private boolean isHappen;//是否发生;置“1”：发生，置“0”：恢复。
    private boolean A_outLimit;//A相越限
    private boolean B_outLimit;//B相越限
    private boolean C_outLimit;//C相越限
    private float Volt_A;
    private float Volt_B;
    private float Volt_C;
    @Override
    protected void DecodeEventDetail(BcdDataBuffer eventData, int len) {
        PmPacketData dataBuffer = new PmPacketData(eventData.getRowIoBuffer());
        if (dataBuffer.restBytes() < 8) {
            return;
        }
        this.isHappen = ((byte) (dataBuffer.getByte() & 0x80) >0);
        byte tempValue = (byte)dataBuffer.getByte();
        this.A_outLimit =((tempValue & 0x01) == 1);
        this.B_outLimit =((tempValue & 0x02) == 2);
        this.C_outLimit =((tempValue & 0x04) == 4);
        this.Volt_A = dataBuffer.getA7().getValue();
        this.Volt_B = dataBuffer.getA7().getValue();
        this.Volt_C = dataBuffer.getA7().getValue();
        
        StringBuilder sb = new StringBuilder();
        if(isHappen)
        {
            sb.append("发生变压器欠压保护，");
        }
        else{
            sb.append("变压器欠压保护恢复，");
        }
        sb.append("发生时的A相电压:").append(Volt_A).append(" V");
        sb.append("发生时的B相电压:").append(Volt_B).append(" V");
        sb.append("发生时的C相电压:").append(Volt_C).append(" V");
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
     * @param isHappen the isHappen to set
     */
    public void setIsHappen(boolean isHappen) {
        this.isHappen = isHappen;
    }

    /**
     * @return the A_outLimit
     */
    public boolean isA_outLimit() {
        return A_outLimit;
    }

    /**
     * @param A_outLimit the A_outLimit to set
     */
    public void setA_outLimit(boolean A_outLimit) {
        this.A_outLimit = A_outLimit;
    }

    /**
     * @return the B_outLimit
     */
    public boolean isB_outLimit() {
        return B_outLimit;
    }

    /**
     * @return the C_outLimit
     */
    public boolean isC_outLimit() {
        return C_outLimit;
    }

    /**
     * @return the Volt_A
     */
    public float getVolt_A() {
        return Volt_A;
    }

    /**
     * @return the Volt_B
     */
    public float getVolt_B() {
        return Volt_B;
    }

    /**
     * @return the Volt_C
     */
    public float getVolt_C() {
        return Volt_C;
    }
    
}
