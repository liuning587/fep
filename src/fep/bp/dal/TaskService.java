/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fep.bp.dal;


import java.sql.Date;
import java.util.List;
import fep.bp.model.TermTaskDAO;

/**
 *
 * @author Thinkpad
 */
public interface TaskService {
    public List<TermTaskDAO> getPollingTask(int CircleUnit,int interval);
    public void updateTask(int TaskId,String ProtocolNo,String Sys_Object,Date StartTime,Date EndTime,int PollingNum);

}
