/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fep.bp.dal;

import java.util.List;
import fep.bp.model.Dto;
import fep.bp.model.SMSDAO;
import fep.codec.protocol.gb.gb376.Packet376Event36;
import fep.codec.protocol.gb.gb376.PmPacket376EventBase;

/**
 *
 * @author Thinkpad
 */
public interface SmsService {

    public List<SMSDAO> getRecvSMS();

    public void deleteRecvSMS(int smsid);

}
