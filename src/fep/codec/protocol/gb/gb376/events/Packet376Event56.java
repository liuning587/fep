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
 * @author THINKPAD
 */
public class Packet376Event56 extends PmPacket376EventBase{
    public class Meter56 {
        public String meterAddress;//漏保地址
        public boolean isHappen;//是否发生;置“1”：发生，置“0”：恢复。
        public String controlWord;//控制字
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("保护器地址=").append(meterAddress).append(" ");
            if (this.isHappen) {
                sb.append("发生剩余电流动作保护器状态告警 ");
            } else {
                sb.append("剩余电流动作保护器状态告警恢复 ");
            }

            sb.append("保护器控制字：").append(controlWord).append(" ");
            sb.append("发生时间").append(BcdUtils.dateToString(eventTime, "yy-MM-dd HH:mm:ss"));
            return sb.toString();
        }
    }
    @Override
    protected void DecodeEventDetail(BcdDataBuffer eventData, int len) {
        PmPacketData dataBuffer = new PmPacketData(eventData.getRowIoBuffer());
        if (dataBuffer.restBytes() < 2) {
            return;
        }
        this.Tongxunduankou = (byte) (dataBuffer.getByte() & 0x3F);
        int count = dataBuffer.getByte();
        len -= 2;

        while ((dataBuffer.restBytes() >= 11) && (len > 0)) {
            try {
                Meter56 event = new Meter56();
                event.meterAddress = Gb645Address.meterAddressToString(dataBuffer.getBytes(6));
                byte s = (byte) dataBuffer.getByte();
                event.isHappen = ((s & 0x80) >0);
                event.controlWord = dataBuffer.getBS(4);
                meters.add(event);
            } finally {
                len -= 15;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Comm port=").append(this.Tongxunduankou).append(" ");
        sb.append("meters=(");
        for (Meter56 event : this.meters) {
            sb.append("(").append(event.toString()).append(")");
        }
        sb.append(")");
        this.eventDetail = sb.toString();
    }
    
    public byte Tongxunduankou;
    public List<Meter56> meters = new ArrayList<Meter56>();
}
