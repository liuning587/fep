/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fep.bp.dal;

import java.util.List;
import fep.bp.model.PSDAO;

/**
 *
 * @author Thinkpad
 */
public interface PSService {
    public List<PSDAO> getTestPSList(String testDay,String testHour);

    public int getPsId(String LogicAddr, int GP_SN);

}
