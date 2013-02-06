/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fep.codec.protocol.gb.gb376;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import fep.codec.utils.BcdDataBuffer;

/**
 *
 * @author luxiaochung
 */
public class PmPacket376EventDecoder {

    public static List<PmPacket376EventBase> decode(BcdDataBuffer data) {
        Date eventTime =new java.util.Date();
        List<PmPacket376EventBase> eventList = new ArrayList<PmPacket376EventBase>();
        if (data.restBytes() >= 8) {
            //data.getBytes(4); //afn
            data.getBytes(4); //event contex
            while (data.restBytes() >= 7) {
                byte erc = (byte) data.getByte();
                int eventlen = data.getByte();
                if (data.restBytes() < eventlen) {
                    break;
                }
                if(erc!=14)
                {
                  eventTime = data.getDate("MIHHDDMMYY");
                  eventlen -= 5;
                }
                
                PmPacket376EventBase event;
                if (erc == 36) {
                    event = new Packet376Event36();
                } 
                else if (erc == 42) {
                    event = new Packet376Event42();
                    event.eventTime = eventTime;//
                }
                else {
                    event = new Packet376EventNormal();
                }
                event.erc = erc;
                event.DecodeEventDetail(data, eventlen);
                event.eventTime = eventTime;
                eventList.add(event);
            }
        }
        return eventList;
    }
}
