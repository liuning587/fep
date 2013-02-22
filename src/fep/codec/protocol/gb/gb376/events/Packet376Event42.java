/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fep.codec.protocol.gb.gb376.events;

import fep.codec.utils.BcdDataBuffer;
import fep.codec.utils.BcdUtils;
import fep.meter645.Gb645Address;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author THINKPAD
 */
public class Packet376Event42 extends PmPacket376EventBase{
    public class Meter42 {
        public String meterAddress;//漏保地址
        public boolean haveAlarm;//告警状态
        public boolean isClosed;//闸位状态
        public String xiangwei;
        public Date eventTime;
        public byte status;
        public int eventValue;

        public String statusString() {
            switch (this.status) {
                case 0:
                    return "未知";
                case 1:
                    return "无效";
                case 2:
                    return "漏电";
                case 3:
                    return "无效";
                case 4:
                    return "缺零";
                case 5:
                    return "过载";
                case 6:
                    return "短路";
                case 7:
                    return "缺相";
                case 8:
                    return "欠压";
                case 9:
                    return "过压";
                case 10:
                    return "接地";
                case 11:
                    return "停电";
                case 12:
                    return "试验";
                case 13:
                    return "远程";
                case 14:
                    return "模拟";
                case 15:
                    return "闭锁";
                case 16:
                    return "互感器故障";
                case 17:
                    return "合闸失败";
                case 18:
                    return "手动";
                case 19:
                    return "设置更改";
                default:
                    return "未知";
            }
        }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("保护器地址=").append(meterAddress).append(" ");
        if (this.isClosed) {
            sb.append("合闸状态 ");
        } else {
            sb.append("分闸状态 ");
        }
        if (this.haveAlarm) {
            sb.append("有告警 ");
        } else {
            sb.append("无告警 ");
        }
        sb.append("相位").append(xiangwei).append(" ");
        sb.append(this.statusString());
        sb.append("发生时间").append(BcdUtils.dateToString(eventTime, "yy-MM-dd HH:mm:ss"));
        return sb.toString();
    }
}

    public byte Tongxunduankou;
    public List<Meter42> meters = new ArrayList<Meter42>();

    @Override
    protected void DecodeEventDetail(BcdDataBuffer eventData, int len) {
        if (eventData.restBytes() < 2) {
            return;
        }
        this.Tongxunduankou = (byte) (eventData.getByte() & 0x3F);
        int count = eventData.getByte();
        len -= 2;

        while ((eventData.restBytes() >= 15) && (len > 0)) {
            try {
                Meter42 event = new Meter42();
                event.eventTime = this.eventTime;
                event.meterAddress = Gb645Address.meterAddressToString(eventData.getBytes(6));
                byte s = (byte) eventData.getByte();
                event.isClosed = ((s & 0x20) == 0x00);
                event.haveAlarm = ((s & 0x40) == 0x40);
                
                event.status = (byte) (s & 0x1f);
                eventData.getByte();//将保留的状态字读掉
                s = (byte) eventData.getByte();//故障相位
                switch ((s & 0x07))
                {
                    case 0:
                        event.xiangwei = "无效";
                        break;
                    case 1:
                        event.xiangwei = "A相";
                        break;
                    case 2:
                        event.xiangwei = "B相";
                        break;
                    case 4:
                        event.xiangwei = "C相";
                        break;
                }
                try {
                    event.eventValue = (int) eventData.getBcdInt(2);
                } catch (Exception ex) {
                    event.eventValue = 0;
                }
                //4 control 
                eventData.getByte();
                eventData.getByte();
                eventData.getByte();
                eventData.getByte();
                //event.eventTime = eventData.getDate("SSMIHHWWDDMMYY");

                meters.add(event);
            } finally {
                len -= 15;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("端口：").append(this.Tongxunduankou).append(" ");
        sb.append("详细信息：(");
        for (Meter42 event : this.meters) {
            sb.append("(").append(event.toString()).append(")");
        }
        sb.append(")");
        this.eventDetail = sb.toString();
    }

}
