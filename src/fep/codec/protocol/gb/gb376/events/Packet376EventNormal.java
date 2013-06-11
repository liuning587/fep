/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fep.codec.protocol.gb.gb376.events;

import fep.codec.protocol.gb.DataTypeA12;
import fep.codec.utils.BcdDataBuffer;
import fep.codec.utils.BcdUtils;

/**
 *
 * @author luxiaochung
 */
public class Packet376EventNormal extends PmPacket376EventBase{

    @Override
    protected void DecodeEventDetail(BcdDataBuffer eventData, int len) {
        switch(erc)
        {
            case 4:{  //开关量状态变位
                this.eventDetail = BcdUtils.bytesToBitSetString(eventData.getBytes(len));
                break;
            }
            case 14:{//停上电事件
                this.eventDetail = BcdUtils.binArrayToString(eventData.getBytes(len));
                break;
            }
            case 57:{//台区门禁（循查事件）记录
                if(len == 6)
                {
                    DataTypeA12 data = new DataTypeA12(eventData.getBytes(len));
                    this.eventDetail = data.toString();
                }
                break;
            }
        }
        
    }
}
