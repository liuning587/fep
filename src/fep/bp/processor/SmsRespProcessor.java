/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fep.bp.processor;

import fep.codec.protocol.gb.PmPacket;
import fep.codec.protocol.gb.gb376.PmPacket376;
import fep.codec.protocol.gb.gb376.PmPacket376Factroy;
import fep.meter645.Gb645MeterPacket;
import fep.meter645.qianlong.QianlongPackageFactroy;
import fep.mina.protocolcodec.gb.PepGbCommunicator;
import fep.mina.protocolcodec.gb.RtuCommunicationInfo;

/**
 *
 * @author luxiaochung
 */
public class SmsRespProcessor {
    private static PepGbCommunicator rtuMap;
    
    public static void setRtuMap(PepGbCommunicator rtumap){
       rtuMap = rtumap; 
    }
    public static void receiveRtuPacket(PmPacket pack){
        //do nothing
    }
    
    public static void receiveLoubaoOperateMsg(long id, String rtua, String lbAddress){
        Gb645MeterPacket lbPacket = QianlongPackageFactroy.makeSetPacket(lbAddress, 0xC036);
        lbPacket.getData().putByte((byte)0x5F);
        
        PmPacket376 pack = PmPacket376Factroy.makeDirectCommunicationPacket(
               RtuCommunicationInfo.LOUBAO_OPRATE_HOSTID, rtua, 
               (byte)1, (byte)0, (byte)0, (byte)0, lbPacket.getValue());
       rtuMap.SendPacket(0, pack,1,(byte)0);
    }
}
