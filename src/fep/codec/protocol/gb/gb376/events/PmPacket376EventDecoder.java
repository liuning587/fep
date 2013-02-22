/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fep.codec.protocol.gb.gb376.events;

import fep.codec.utils.BcdDataBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
                switch(erc)
                {
                    case 36:{event = new Packet376Event36();break;}
                    case 42:
                    case 55:{event = new Packet376Event42();break;}
                    case 50:{event = new Packet376Event50();break;}
                    case 51:{event = new Packet376Event51();break;}
                    case 52:{event = new Packet376Event52();break;}
                    case 53:{event = new Packet376Event53();break;}
                    case 54:{event = new Packet376Event54();break;}
                    case 56:{event = new Packet376Event56();break;}
                    default:{event = new Packet376EventNormal();break;}
                }
                event.erc = erc;
                event.eventTime = eventTime;
                event.DecodeEventDetail(data, eventlen);              
                eventList.add(event);
            }
        }
        return eventList;
    }
}
