/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fep.bp.dal;

import fep.bp.model.SMSDAO;
import java.util.List;

/**
 *
 * @author Thinkpad
 */
public interface SmsService {

    public List<SMSDAO> getRecvSMS();

    public void deleteRecvSMS(int smsid);

}
