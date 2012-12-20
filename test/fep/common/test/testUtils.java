/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fep.common.test;

import fep.codec.protocol.gb.gb376.PmPacket376;
import fep.codec.utils.BcdUtils;

/**
 *
 * @author xiekeli
 */
public class testUtils {

    public static PmPacket376 getPacket(String Msg){
        byte[] msg = BcdUtils.stringToByteArray(Msg);
        PmPacket376 packet = new PmPacket376();
        packet.setValue(msg, 0);
        return packet;
    }

}
