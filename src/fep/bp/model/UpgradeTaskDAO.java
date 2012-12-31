/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fep.bp.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 *
 * @author THINKPAD
 */
public class UpgradeTaskDAO {
    private int taskId;
    private int sequenceCode;
    private String logicAddress;
    private int binFileSize;
    private int binFileID;
    private InputStream binFile;
    private Date postTime;
    private String taskStatus = "0"; //默认未处理
    private float schedule;//进度
    private int failFrameNo;
    private String valid;

    /**
     * @return the taskId
     */
    public int getTaskId() {
        return taskId;
    }

    /**
     * @param taskId the taskId to set
     */
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    /**
     * @return the sequenceCode
     */
    public int getSequenceCode() {
        return sequenceCode;
    }

    /**
     * @param sequenceCode the sequenceCode to set
     */
    public void setSequenceCode(int sequenceCode) {
        this.sequenceCode = sequenceCode;
    }

    /**
     * @return the logicAddress
     */
    public String getLogicAddress() {
        return logicAddress;
    }

    /**
     * @param logicAddress the logicAddress to set
     */
    public void setLogicAddress(String logicAddress) {
        this.logicAddress = logicAddress;
    }

    /**
     * @return the binFile
     */
    public InputStream getBinFile() {
        return binFile;
    }
    
    public byte[] getBinFile2ByteArray() throws IOException{
        byte[] result = new byte[this.binFileSize];
        binFile.read(result);
        binFile.close();
        return result;
    }

    /**
     * @param binFile the binFile to set
     */
    public void setBinFile(InputStream binFile) {
        this.binFile = binFile;

    }

    /**
     * @return the postTime
     */
    public Date getPostTime() {
        return postTime;
    }

    /**
     * @param postTime the postTime to set
     */
    public void setPostTime(Date postTime) {
        this.postTime = postTime;
    }

    /**
     * @return the taskStatus
     */
    public String getTaskStatus() {
        return taskStatus;
    }

    /**
     * @param taskStatus the taskStatus to set
     */
    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    /**
     * @return the schedule
     */
    public float getSchedule() {
        return schedule;
    }

    /**
     * @param schedule the schedule to set
     */
    public void setSchedule(float schedule) {
        this.schedule = schedule;
    }

    /**
     * @return the binFileSize
     */
    public int getBinFileSize() {
        return binFileSize;
    }

    /**
     * @param binFileSize the binFileSize to set
     */
    public void setBinFileSize(int binFileSize) {
        this.binFileSize = binFileSize;
    }

    /**
     * @return the binFileID
     */
    public int getBinFileID() {
        return binFileID;
    }

    /**
     * @param binFileID the binFileID to set
     */
    public void setBinFileID(int binFileID) {
        this.binFileID = binFileID;
    }

    /**
     * @return the failFrameNo
     */
    public int getFailFrameNo() {
        return failFrameNo;
    }

    /**
     * @param failFrameNo the failFrameNo to set
     */
    public void setFailFrameNo(int failFrameNo) {
        this.failFrameNo = failFrameNo;
    }

    /**
     * @return the valid
     */
    public String getValid() {
        return valid;
    }

    /**
     * @param valid the valid to set
     */
    public void setValid(String valid) {
        this.valid = valid;
    }
}
