/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fep.codec.protocol.gb.gb376.events;

import fep.codec.protocol.gb.PmPacketData;
import fep.codec.utils.BcdDataBuffer;
import fep.codec.utils.BcdUtils;
import fep.meter645.Gb645Address;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class Packet376Event54 extends PmPacket376EventBase{
    public class Meter54
    {
        public String meterAddress;//漏保地址
        public boolean isHappen;//是否发生;置“1”：发生，置“0”：恢复。
        public float leakCurrent;//漏电流值
        public int leakCurrentGear;//漏电流档位植
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("保护器地址=").append(meterAddress).append(" ");
            if (this.isHappen) {
                sb.append("发生漏电告警 ");
            } else {
                sb.append("漏电告警恢复 ");
            }
            sb.append("漏电电流").append(leakCurrent).append("mA");
            sb.append("漏电电流挡位值").append(leakCurrentGear).append("");
            sb.append("发生时间").append(BcdUtils.dateToString(eventTime, "yy-MM-dd HH:mm:ss"));
            return sb.toString();
        }
    }

    public byte Tongxunduankou;
    public List<Meter54> meters = new ArrayList<Meter54>();
    
    

    @Override
    protected void DecodeEventDetail(BcdDataBuffer eventData, int len) {
        if (eventData.restBytes() < 2) {
            return;
        }
        PmPacketData dataBuffer = new PmPacketData(eventData.getRowIoBuffer());
        this.Tongxunduankou = (byte) (dataBuffer.getByte() & 0x3F);
        int count = dataBuffer.getByte();
        len -= 2;

        while ((dataBuffer.restBytes() >= 11) && (len > 0)) {
            try {
                Meter54 event = new Meter54();
                event.meterAddress = Gb645Address.meterAddressToString(dataBuffer.getBytes(6));
                byte s = (byte) dataBuffer.getByte();
                event.isHappen = ((s & 0x80) >0);
                event.leakCurrent = dataBuffer.getA8().getValue();
                event.leakCurrentGear = dataBuffer.getA8().getValue();
                meters.add(event);
            } finally {
                len -= 11;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Comm port=").append(this.Tongxunduankou).append(" ");
        sb.append("meters=(");
        for (Meter54 event : this.meters) {
            sb.append("(").append(event.toString()).append(")");
        }
        sb.append(")");
        this.eventDetail = sb.toString();
    }
    
}
