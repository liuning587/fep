/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fep.bp.dal;

import fep.bp.model.Dto;
import fep.codec.protocol.gb.gb376.events.Packet376Event36;
import fep.codec.protocol.gb.gb376.events.Packet376Event42;
import fep.codec.protocol.gb.gb376.events.PmPacket376EventBase;
import java.util.Date;

/**
 *
 * @author Thinkpad
 */
public interface DataService {

    public void insertRecvData(Dto data);

    public void insertLBEvent36(String rtua, Packet376Event36 event);
    public void insertLBEvent42(String rtua, Packet376Event42 event);

    public void insertEvent(String rtua, PmPacket376EventBase event);
    public void insertObjStatus(String rtua,int gpSn,String ObjectType,int statusValue);
    public void insertAccessRecord(String rtua,Date accessDate,String AccessType,String cardCode,String doorMark);
    public void insertCDDL(String logicalAddress,int gpSn,String wgdl_day,String wgdl_month);
}
