/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fep.codec.protocol.gb.gb376;

import fep.codec.protocol.gb.PmPacket;

/**
 *
 * @author luxiaochung
 */

public class PmPacket376 extends PmPacket {
    private static byte protocolVersion = 2;

    @Override
    public PmPacket376 clone(){
        PmPacket376 result = new PmPacket376();
        result.setValue(this.getValue(),0);
        result.setCommandRemark(this.getCommandRemark());
        result.setMpSnRemark(this.getMpSnRemark());
        
        return result;
    }

    @Override
    protected byte getProtocolVersion(){
        return PmPacket376.protocolVersion;
    }
    
    public static int getMsgHeadOffset(byte[] msg, int firstIndex){
        return PmPacket.getMsgHeadOffset(msg, PmPacket376.protocolVersion, firstIndex);
    }
}
