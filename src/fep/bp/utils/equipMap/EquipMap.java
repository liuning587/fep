/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fep.bp.utils.equipMap;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author THINKPAD
 */
public class EquipMap {
    //漏保组信息
    private class LoubaoGroup
    {
        public HashMap<String,String> loubaoMap;
        public LoubaoGroup()
        {
            loubaoMap = new HashMap<String,String>();
        }
        
    }
    private final static Logger log = LoggerFactory.getLogger(EquipMap.class);
    private HashMap<String,LoubaoGroup> equipMap;
    
    
    private JdbcTemplate jdbcTemplate;
    
    public EquipMap()
    {
        equipMap = new HashMap<String,LoubaoGroup>();
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public DataSource getDataSource() {
        return this.jdbcTemplate.getDataSource();
    }
    
    public void init()
    {
        try {
            StringBuilder sbSQL = new StringBuilder();
            sbSQL.append("select logical_addr,terminalProtocol,loubaoProtocol,loubao_addr");
            sbSQL.append(" from v_term_protocl_lb");
            String SQL = sbSQL.toString();
            List<EqpDAO> results = (List<EqpDAO>) jdbcTemplate.query(SQL, new EqpRowMapper());

            //获取终端 -- 漏保 信息
            
            for (EqpDAO eqp : results) {
                if(equipMap.containsKey(eqp.logicAddress))
                {
                    equipMap.get(eqp.logicAddress).loubaoMap.put(eqp.loubaoAddress, eqp.loubaoProtocol);
                }
                else
                {
                    LoubaoGroup group = new LoubaoGroup();
                    group.loubaoMap.put(eqp.loubaoAddress, eqp.loubaoProtocol);
                    equipMap.put(eqp.getLogicAddress(), group);
                }
            }

        } catch (DataAccessException dataAccessException) {
            log.error(dataAccessException.getMessage());

        }
    }
    
    public int loubaoProtocol(String logicalAddr,String loubaoAddr)
    {
        if(!equipMap.containsKey(logicalAddr)) {
            init();
        }
        if(!equipMap.containsKey(logicalAddr)) {
            return -1;
        }
        else
        {
            LoubaoGroup group = equipMap.get(logicalAddr);
            if(group.loubaoMap.containsKey(loubaoAddr))
            {
                return Integer.valueOf(group.loubaoMap.get(loubaoAddr));
            }
            else {
                return -1;
            }
        }
    }
    
    public class EqpDAO {
        private String logicAddress;
        private String terminalProtocol;
        private String loubaoAddress;
        private String loubaoProtocol;
        
        
        public EqpDAO(){
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
         * @return the terminalProtocol
         */
        public String getTerminalProtocol() {
            return terminalProtocol;
        }

        /**
         * @param terminalProtocol the terminalProtocol to set
         */
        public void setTerminalProtocol(String terminalProtocol) {
            this.terminalProtocol = terminalProtocol;
        }

        /**
         * @return the loubaoProtocol
         */
        public String getLoubaoProtocol() {
            return loubaoProtocol;
        }

        /**
         * @param loubaoProtocol the loubaoProtocol to set
         */
        public void setLoubaoProtocol(String loubaoProtocol) {
            this.loubaoProtocol = loubaoProtocol;
        }

        /**
         * @return the loubaoAddress
         */
        public String getLoubaoAddress() {
            return loubaoAddress;
        }

        /**
         * @param loubaoAddress the loubaoAddress to set
         */
        public void setLoubaoAddress(String loubaoAddress) {
            this.loubaoAddress = loubaoAddress;
        }
    }
    
    public class EqpRowMapper implements RowMapper{
    @Override
    public Object mapRow(ResultSet rs, int index) throws SQLException{
        EqpDAO equip = new EqpDAO();
        equip.setLogicAddress(rs.getString("LOGICAL_ADDR"));
        equip.setTerminalProtocol(rs.getString("terminalProtocol"));
        equip.setLoubaoProtocol(rs.getString("loubaoProtocol"));
        equip.setLoubaoAddress(rs.getString("loubao_addr"));
        
        return equip;
    }
}
}
