/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fep.bp.model;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.lob.OracleLobHandler;

/**
 *
 * @author THINKPAD
 */
public class UpgradeTaskRowMapper implements RowMapper{
    @Override
    public Object mapRow(ResultSet rs, int index) throws SQLException{
        UpgradeTaskDAO task = new UpgradeTaskDAO();
        task.setTaskId(rs.getInt("TASK_ID"));
        task.setSequenceCode(rs.getInt("SEQUENCE_CODE"));
        task.setLogicAddress(rs.getString("LOGICAL_ADDR"));
        task.setBinFileSize(rs.getInt("BINFILE_SIZE"));
        task.setBinFile(rs.getBinaryStream("BINFILE"));   
        task.setTaskStatus(rs.getString("TASK_STATUS"));
        task.setPostTime(rs.getTimestamp("POST_TIME"));
        task.setSchedule(rs.getFloat("SCHEDULE"));
        task.setFailFrameNo(rs.getInt("failFrameNo"));
        task.setValid(rs.getString("valid"));
        return task;
    }
}