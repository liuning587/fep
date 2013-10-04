/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fep.bp.dal;

import fep.bp.model.OnlineStatusDAO;

/**
 *
 * @author THINKPAD
 */
public interface StatusService {
    public void insertStatus(OnlineStatusDAO status);
    public void initStatus_offLine();
}
