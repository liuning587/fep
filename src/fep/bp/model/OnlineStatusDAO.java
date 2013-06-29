/**
* @Description: 
* @author lijun 
* @date 2011-6-30 0:17:20
*
* Expression tags is undefined on line 6, column 5 in Templates/Classes/Class.java.
*/

package fep.bp.model;

import java.sql.Time;
import java.util.Date;



public class OnlineStatusDAO {
    private String logicalAddress;
    private boolean iscurrent;
    private Date eventTime;
    private Date recordTime;
    private boolean isConnect;

    public OnlineStatusDAO(String logicalAddress,boolean iscurrent,Date eventTime,boolean isConnect){
        super();
        this.logicalAddress = logicalAddress;
        this.iscurrent = iscurrent;
        this.eventTime = eventTime;
        this.isConnect = isConnect;
    }

    /**
     * @return the logicalAddress
     */
    public String getLogicalAddress() {
        return logicalAddress;
    }

    /**
     * @param logicalAddress the logicalAddress to set
     */
    public void setLogicalAddress(String logicalAddress) {
        this.logicalAddress = logicalAddress;
    }

    
    /**
     * @return the recordTime
     */
    public Date getRecordTime() {
        return recordTime;
    }

    /**
     * @param recordTime the recordTime to set
     */
    public void setRecordTime(Time recordTime) {
        this.recordTime = recordTime;
    }

    /**
     * @return the iscurrent
     */
    public boolean isIscurrent() {
        return iscurrent;
    }

    /**
     * @param iscurrent the iscurrent to set
     */
    public void setIscurrent(boolean iscurrent) {
        this.iscurrent = iscurrent;
    }

    /**
     * @return the eventTime
     */
    public Date getEventTime() {
        return eventTime;
    }

    /**
     * @param eventTime the eventTime to set
     */
    public void setEventTime(Time eventTime) {
        this.eventTime = eventTime;
    }

    /**
     * @return the isConnect
     */
    public boolean isIsConnect() {
        return isConnect;
    }

    /**
     * @param isConnect the isConnect to set
     */
    public void setIsConnect(boolean isConnect) {
        this.isConnect = isConnect;
    }

}
