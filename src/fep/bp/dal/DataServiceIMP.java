/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fep.bp.dal;

import fep.bp.dal.storedProc.DAY_ECUR_STATIS_StoredProcedure;
import fep.bp.dal.storedProc.DAY_IMB_STATIS_StoredProcedure;
import fep.bp.dal.storedProc.DAY_VOLT_STATIS_StoredProcedure;
import fep.bp.dal.storedProc.ECCURV_StoredProcedure;
import fep.bp.dal.storedProc.ECCURV_StoredProcedure2;
import fep.bp.dal.storedProc.EventStoredProcedure;
import fep.bp.dal.storedProc.Humiture_StoredProcedure;
import fep.bp.dal.storedProc.I_ACT_StoredProcedure;
import fep.bp.dal.storedProc.I_REACT_StoredProcedure;
import fep.bp.dal.storedProc.LouBaoEvent36_StoredProcedure;
import fep.bp.dal.storedProc.LouBaoEvent42_StoredProcedure;
import fep.bp.dal.storedProc.PFCURV_StoredProcedure;
import fep.bp.dal.storedProc.PSCtrlPara_StoredProcedure;
import fep.bp.dal.storedProc.PSCustomPara_StoredProcedure;
import fep.bp.dal.storedProc.PSStatus_StoredProcedure;
import fep.bp.dal.storedProc.P_ACT_StoredProcedure;
import fep.bp.dal.storedProc.P_REACT_StoredProcedure;
import fep.bp.dal.storedProc.PowerCurv_StoredProcedure;
import fep.bp.dal.storedProc.PowerCurv_StoredProcedure2;
import fep.bp.model.Dto;
import fep.bp.model.Dto.DtoItem;
import fep.bp.utils.UtilsBp;
import fep.codec.protocol.gb.gb376.events.Packet376Event36;
import fep.codec.protocol.gb.gb376.events.Packet376Event36.Meter36;
import fep.codec.protocol.gb.gb376.events.Packet376Event42;
import fep.codec.protocol.gb.gb376.events.Packet376Event42.Meter42;
import fep.codec.protocol.gb.gb376.events.PmPacket376EventBase;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Thinkpad
 */
@Transactional
public class DataServiceIMP implements DataService {

    private final static Logger log = LoggerFactory.getLogger(DataServiceIMP.class);
    private JdbcTemplate jdbcTemplate;
    private P_ACT_StoredProcedure p_actStoredProcedure;
    private P_REACT_StoredProcedure p_reactStoredProcedure;
    private I_REACT_StoredProcedure i_reactStoredProcedure;
    private I_ACT_StoredProcedure i_actStoredProcedure;
    private ECCURV_StoredProcedure eccurvStoredProcedure;
    private ECCURV_StoredProcedure2 eccurvStoredProcedure2;
    private PowerCurv_StoredProcedure powerCurvStoredProcedure;
    private PowerCurv_StoredProcedure2 powerCurvStoredProcedure2;
    private Humiture_StoredProcedure humitureStoredProcedure;
    private EventStoredProcedure eventStoredProcedure;
    private LouBaoEvent36_StoredProcedure loubaoEvent36_StoredProcedure;
    private LouBaoEvent42_StoredProcedure loubaoEvent42_StoredProcedure;
    private DAY_ECUR_STATIS_StoredProcedure ecurStatisStoredProcedure;
    private DAY_VOLT_STATIS_StoredProcedure voltStatisStoredProcedure;
    private DAY_IMB_STATIS_StoredProcedure imbStatisStoredProcedure;
    private PFCURV_StoredProcedure pfcurvStoredProcedure;
    private PSStatus_StoredProcedure psStatusStoredProcedure;
    //add on 2013-02-24  by lijun 
    private PSCtrlPara_StoredProcedure psCtrlParaStoredProcedure;
    private PSCustomPara_StoredProcedure psCustomParaStoreProcedure;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public DataSource getDataSource() {
        return this.jdbcTemplate.getDataSource();
    }

    @Override
    public void insertRecvData(Dto dto) {
        byte AFN = dto.getAfn();

        List dtoItems = dto.getDataItems();
        for (int i = 0; i <= dtoItems.size() - 1; i++)
        {
            DtoItem dtoItem = (DtoItem) dtoItems.get(i);
            //一类数据
            if (AFN == (byte) 0X0C) {                             
                insertData_0C(dto.getLogicAddress(), dtoItem);
                continue;
            }
            //二类数据
            if (AFN == (byte) 0X0D) {
                insertData_0D(dto.getLogicAddress(), dtoItem);
                continue;
            }
            //漏保数据
            if (AFN == (byte) 0X10) {                         //透明转发数据
                insertData_10(dto.getLogicAddress(), dtoItem);
                continue;
            }
        }
    }

    @Override
    public void insertLBEvent36(String rtua, Packet376Event36 event) {
        List<Meter36> Meters = event.meters;
        for (Meter36 meter : Meters) {
            try {
                this.loubaoEvent36_StoredProcedure.execute(rtua, meter.meterAddress,
                        meter.status, meter.eventTime, new Date(), (meter.isClosed ? 1 : 0), (meter.isLocked ? 1 : 0), meter.xiangwei, meter.eventValue);
            } catch (Exception e) {
                log.error("错误信息：", e.fillInStackTrace());
            }
        }
    }

    @Override
    public void insertLBEvent42(String rtua, Packet376Event42 event) {
        List<Meter42> Meters = event.meters;
        for (Meter42 meter : Meters) {
            try {
                this.loubaoEvent42_StoredProcedure.execute(rtua, meter.meterAddress,
                        meter.status, meter.eventTime, new Date(), (meter.isClosed ? 1 : 0), (meter.isLocked ? 1 : 0), meter.xiangwei, meter.eventValue);
            } catch (Exception e) {
                log.error("错误信息：", e.fillInStackTrace());
            }
        }
    }

    @Override
    public void insertEvent(String rtua, PmPacket376EventBase event) {
        try {
            this.eventStoredProcedure.execute(rtua, 0, String.valueOf(event.GetEventCode()), UtilsBp.Date2String(event.getEventTime()), event.getEventDetail());
        } catch (Exception e) {
            log.error("错误信息：", e.fillInStackTrace());
        }
    }


    /*===============================================内部函数，闲人莫入===============================*/

    //一类数据入库
    private void insertData_0C(String logicalAddress, DtoItem dtoItem) {
        String commandItemCode = dtoItem.commandItemCode;
        Map<String, String> dataItemMap = dtoItem.dataMap;
        //当前正向有功电能示值（总、费率1～M）
        if (commandItemCode.equals("100C0129")) {
            insertData_P_ACT(logicalAddress, dtoItem.gp, dtoItem.dataTime, dtoItem);
        }
        //当前正向无功（组合无功1）电能示值（总、费率1～M）
        if (commandItemCode.equals("100C0130")) {
            insertData_P_REACT(logicalAddress, dtoItem.gp, dtoItem.dataTime, dtoItem);
        }
        //当前反向有功电能示值（总、费率1～M）
        if (commandItemCode.equals("100C0131")) {
            insertData_I_ACT(logicalAddress, dtoItem.gp, dtoItem.dataTime, dtoItem);
        }
         //当前反向无功（组合无功1）电能示值（总、费率1～M）
        if (commandItemCode.equals("100C0132")) {
            insertData_I_REACT(logicalAddress, dtoItem.gp, dtoItem.dataTime, dtoItem);
        }

         //当前三相及总有/无功功率、功率因数，三相电压、电流、零序电流、视在功率
        if (commandItemCode.equals("100C0025"))
        {
            insertData_EC_CURV(logicalAddress, dtoItem.gp, dtoItem.dataTime, dtoItem);//電壓/電流曲線
            insert_POWER_CRUV(logicalAddress, dtoItem.gp, dtoItem.dataTime, dtoItem);//功率曲線
        }
        //模拟量实时数据（温度）
        if (commandItemCode.equals("100C0073")) {
            insertData_HUMITURE(logicalAddress, dtoItem.gp, "0", dataItemMap.get("100C007301"));
        }
        
        //新增一类数据（漏保数据F200、F201）
        if (commandItemCode.equals("100C0200")) {
            //電壓/電流曲線
            this.insertData_EC_CURV_LouBao(logicalAddress, dtoItem.gp, dtoItem.dataTime,commandItemCode, dtoItem);
            this.insertData_PsCtrlPara(logicalAddress, dtoItem.gp, commandItemCode, dtoItem);
        }
        //用户自定义参数
        if (commandItemCode.equals("100C0201")){
            this.insertData_PsCustomPara(logicalAddress, dtoItem.gp, commandItemCode, dtoItem);
        }
    }

    //二类数据入库
    private void insertData_0D(String logicalAddress,DtoItem dtoItem) {
        String commandItemCode = dtoItem.commandItemCode;
        Map<String, String> dataItemMap = dtoItem.dataMap;
        //电压统计数据
        if (commandItemCode.equals("100D0027")) {
            insertData_voltStatis(logicalAddress, dtoItem.gp, dtoItem.dataTime, dtoItem);
        }
        //不平衡度的数据
        if (commandItemCode.equals("100D0028")) {
            insertData_imbStatis(logicalAddress, dtoItem.gp, dtoItem.dataTime, dtoItem);
        }
        //日冻结日电流越限数据
        if (commandItemCode.equals("100D0029")) {
            insertData_dayEcurStatis(logicalAddress, dtoItem.gp, dtoItem.dataTime, dtoItem);
        }
        //测量点有功功率曲线
        if (commandItemCode.equals("100D0081")) {
            powerCurvStoredProcedure2.insert_act_power_total(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("2300"));
        }
        //测量点A相有功功率曲线
        if (commandItemCode.equals("100D0082")) {
            powerCurvStoredProcedure2.insert_act_power_a(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("2301"));
        }

        //测量点B相有功功率曲线
        if (commandItemCode.equals("100D0083")) {
            powerCurvStoredProcedure2.insert_act_power_b(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("2302"));
        }
        //测量点C相有功功率曲线
        if (commandItemCode.equals("100D0084")) {
            powerCurvStoredProcedure2.insert_act_power_c(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("2303"));
        }

        //测量点无功功率曲线
        if (commandItemCode.equals("100D0085")) {
            powerCurvStoredProcedure2.insert_react_power_total(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("2400"));
        }

        //测量点A相无功功率曲线
        if (commandItemCode.equals("100D0086")) {
            powerCurvStoredProcedure2.insert_react_power_a(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("2401"));
        }
        //测量点B相无功功率曲线
        if (commandItemCode.equals("100D0087")) {
            powerCurvStoredProcedure2.insert_react_power_b(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("2402"));
        }
        //测量点C相无功功率曲线
        if (commandItemCode.equals("100D0088")) {
            powerCurvStoredProcedure2.insert_react_power_c(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("2403"));
        }
        //测量点A相电压曲线
        if (commandItemCode.equals("100D0089")) {
            eccurvStoredProcedure2.insertVoltA(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("2101"));
        }
        //测量点B相电压曲线
        if (commandItemCode.equals("100D0090")) {
            eccurvStoredProcedure2.insertVoltB(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("2102"));
        }
        //测量点C相电压曲线
        if (commandItemCode.equals("100D0091")) {
            eccurvStoredProcedure2.insertVoltC(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("2103"));
        }
        //测量点A相电流曲线
        if (commandItemCode.equals("100D0092")) {
            eccurvStoredProcedure2.insertEcurA(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("2201"));
        }
        //测量点B相电流曲线
        if (commandItemCode.equals("100D0093")) {
            eccurvStoredProcedure2.insertEcurB(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("2202"));
        }
        //测量点C相电流曲线
        if (commandItemCode.equals("100D0094")) {
            eccurvStoredProcedure2.insertEcurC(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("2203"));
        }
        //测量点零序电流曲线
        if (commandItemCode.equals("100D0095")) {
            eccurvStoredProcedure2.insertEcurC(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("2204"));
        }
        //测量点正向有功总电能示值曲线
        if (commandItemCode.equals("100D0101")) {
            this.p_actStoredProcedure.execute(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("0100"), "", "", "", "");
        }
        //测量点正向无功总电能示值曲线
        if (commandItemCode.equals("100D0102")) {
            this.i_actStoredProcedure.execute(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("A000"), "", "", "", "");
        }
        //测量点反向有功总电能示值曲线
        if (commandItemCode.equals("100D0103")) {
            this.p_reactStoredProcedure.execute(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("0200"), "", "", "", "");
        }
        //测量点反向无功总电能示值曲线
        if (commandItemCode.equals("100D0104")) {
            this.i_reactStoredProcedure.execute(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("A000"), "", "", "", "");
        }
        //测量点功率因数曲线
        if (commandItemCode.equals("100D0105")) {
            this.getPfcurvStoredProcedur().insert_power_factor(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("2600"));
        }
        //测量点A相功率因数曲线
        if (commandItemCode.equals("100D0106")) {
            this.getPfcurvStoredProcedur().insert_power_factor_a(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("2601"));
        }
        //测量点B相功率因数曲线
        if (commandItemCode.equals("100D0107")) {
            this.getPfcurvStoredProcedur().insert_power_factor_b(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("2602"));
        }
        //测量点C相功率因数曲线
        if (commandItemCode.equals("100D0108")) {
            this.getPfcurvStoredProcedur().insert_power_factor_c(logicalAddress, dtoItem.gp, dtoItem.dataTime, dataItemMap.get("2603"));
        }
}


    //漏保数据入库
    private void insertData_10(String logicalAddress, DtoItem dtoItem) {
        String commandItemCode = dtoItem.commandItemCode;
        Map<String, String> dataItemMap = dtoItem.dataMap;

        if (commandItemCode.equals("8000B66F")) {
            //電壓/電流曲線
            this.insertData_EC_CURV_LouBao(logicalAddress, dtoItem.gp, dtoItem.dataTime,commandItemCode, dtoItem);
            //漏保状态
            this.psStatusStoredProcedure.execute(logicalAddress, dtoItem.gp, dtoItem.dataTime,
                    dataItemMap.get("8000B66F01"), dataItemMap.get("8000B66F02"),
                    dataItemMap.get("8000B66F03"), dataItemMap.get("8000B66F04"));
        }

        if (commandItemCode.equals("800001FF")) {
            //電壓/電流曲線
            this.insertData_EC_CURV_LouBao(logicalAddress, dtoItem.gp, dtoItem.dataTime,commandItemCode, dtoItem);
        }
    }
    //当前正向有功电能示值
    private void insertData_P_ACT(String logicalAddress,
            int gpSn,
            String dataDate,
            DtoItem dtoItem) {
        try {
            Map<String, String> dataItemMap = dtoItem.dataMap;
            this.p_actStoredProcedure.execute(
                    logicalAddress,
                    gpSn,
                    dataDate,
                    dataItemMap.get("0100"),
                    dataItemMap.get("0101"),
                    dataItemMap.get("0102"),
                    dataItemMap.get("0103"),
                    dataItemMap.get("0104"));
        } catch (Exception e) {
            log.error("错误信息：", e.fillInStackTrace());
        }
    }

    //当前正向無功电能示值
    private void insertData_P_REACT(String logicalAddress,
            int gpSn,
            String dataDate,
            DtoItem dtoItem)
    {
        try {
            Map<String, String> dataItemMap = dtoItem.dataMap;
            this.p_reactStoredProcedure.execute(logicalAddress,
                    gpSn,
                    dataDate,
                    dataItemMap.get("A000"),
                    dataItemMap.get("A001"),
                    dataItemMap.get("A002"),
                    dataItemMap.get("A003"),
                    dataItemMap.get("A004"));

        } catch (Exception e) {
            log.error("错误信息：", e.fillInStackTrace());
        }
    }


    //当前反向有功电能示值
    private void insertData_I_ACT(String logicalAddress, 
            int gpSn,
            String dataDate,
            DtoItem dtoItem)
    {
        try {
            Map<String, String> dataItemMap = dtoItem.dataMap;
            this.i_actStoredProcedure.execute(
                    logicalAddress,
                    gpSn,
                    dataDate,
                    dataItemMap.get("0200"),
                    dataItemMap.get("0201"),
                    dataItemMap.get("0202"),
                    dataItemMap.get("0203"),
                    dataItemMap.get("0204"));
        } catch (Exception e) {
            log.error("错误信息：", e.fillInStackTrace());
        }
    }


    //当前反向無功电能示值
    private void insertData_I_REACT(String logicalAddress, 
            int gpSn,
            String dataDate,
            DtoItem dtoItem)
    {
        try {
            Map<String, String> dataItemMap = dtoItem.dataMap;
            this.i_reactStoredProcedure.execute(
                    logicalAddress,
                    gpSn,
                    dataDate,
                    dataItemMap.get("A100"),
                    dataItemMap.get("A101"),
                    dataItemMap.get("A102"),
                    dataItemMap.get("A103"),
                    dataItemMap.get("A104"));
        } catch (Exception e) {
            log.error("错误信息：", e.fillInStackTrace());
        }
    }



    //电压电流曲线
    private void insertData_EC_CURV(String logicalAddress,
            int gpSn,
            String dataDate,
            DtoItem dtoItem)
    {
        try {
            Map<String, String> dataItemMap = dtoItem.dataMap;
            this.eccurvStoredProcedure.execute(
                    logicalAddress,
                    gpSn,
                    dataDate,
                    dataItemMap.get("2201"),
                    dataItemMap.get("2202"),
                    dataItemMap.get("2203"),
                    dataItemMap.get("2204"),
                    dataItemMap.get("B660"),
                    dataItemMap.get("2101"),
                    dataItemMap.get("2102"),
                    dataItemMap.get("2103"),
                    "00");
        } catch (Exception e) {
            log.error("错误信息：", e.fillInStackTrace());
        }
    }


    //针对保护器电压电流
    private void insertData_EC_CURV_LouBao(String logicalAddress,
            int gpSn,
            String dataDate,
            String commandItemCode,
            DtoItem dtoItem)
    {
        try {
            Map<String, String> dataItemMap = dtoItem.dataMap;
            String phase = "01";
            if(commandItemCode.equals("8000B66F"))
            {
                phase = dataItemMap.get("8000B66F03");
                if (phase == null ? "00" == null : phase.equals("00"))
                {
                    phase = "01"; //针对接口数据，00按A相处理
                }
                this.eccurvStoredProcedure.execute(
                    logicalAddress,
                    gpSn,
                    dataDate,
                    dataItemMap.get("B621"),
                    dataItemMap.get("B622"),
                    dataItemMap.get("B623"),
                    "",//最大漏电相位对应的漏电电流
                    dataItemMap.get("B660"),
                    dataItemMap.get("B611"),
                    dataItemMap.get("B612"),
                    dataItemMap.get("B613"),
                    phase);
            }
            else if(commandItemCode.equals("800001FF"))
            {
                if("1".equals(dataItemMap.get("B661")))
                {
                    phase = "01";
                }
                else if("2".equals(dataItemMap.get("B661")))
                {
                    phase = "02";
                }
                else if("4".equals(dataItemMap.get("B661")))
                {
                    phase = "03";
                }
                this.eccurvStoredProcedure.execute(
                    logicalAddress,
                    gpSn,
                    dataDate,
                    dataItemMap.get("B621"),
                    dataItemMap.get("B622"),
                    dataItemMap.get("B623"),
                    "",
                    dataItemMap.get("B660"),
                    dataItemMap.get("B611"),
                    dataItemMap.get("B612"),
                    dataItemMap.get("B613"),
                    phase);//最大漏电相位
            }
            else if(commandItemCode.equals("100C0200"))
            {
                this.eccurvStoredProcedure.execute(
                    logicalAddress,
                    gpSn,
                    dataDate,
                    dataItemMap.get("100C020001"),
                    dataItemMap.get("100C020002"),
                    dataItemMap.get("100C020003"),
                    "",
                    dataItemMap.get("100C020008"),
                    dataItemMap.get("100C020004"),
                    dataItemMap.get("100C020005"),
                    dataItemMap.get("100C020006"),
                    dataItemMap.get("100C020007"));
            }
        } catch (Exception e) {
            log.error("错误信息：", e.fillInStackTrace());
        }
    }


    //功率曲线
    private void insert_POWER_CRUV(String logicalAddress, 
            int gpSn,
            String dataDate,
            DtoItem dtoItem)
    {
        try {
            Map<String, String> dataItemMap = dtoItem.dataMap;
            powerCurvStoredProcedure.execute(
                    logicalAddress,
                    gpSn,
                    dataDate,
                    dataItemMap.get("2300"),
                    dataItemMap.get("2301"),
                    dataItemMap.get("2302"),
                    dataItemMap.get("2303"),
                    dataItemMap.get("2400"),
                    dataItemMap.get("2401"),
                    dataItemMap.get("2402"),
                    dataItemMap.get("2403"));
        } catch (DataAccessException dataAccessException) {
            log.error(dataAccessException.getMessage());
        }
    }

    //电压统计数据
    private void insertData_voltStatis(
            String logicalAddress, 
            int gpSn,
            String dataDate,
            DtoItem dtoItem)
    {
        try {
            Map<String, String> dataItemMap = dtoItem.dataMap;
            voltStatisStoredProcedure.execute(
                    logicalAddress,
                    gpSn,
                    dataDate,
                    dataItemMap.get("BE40"),dataItemMap.get("BE41"),
                    dataItemMap.get("BE42"),dataItemMap.get("BE43"),
                    dataItemMap.get("BE44"),dataItemMap.get("BE45"),
                    dataItemMap.get("BE46"),dataItemMap.get("BE47"),
                    dataItemMap.get("BE48"),dataItemMap.get("BE49"),
                    dataItemMap.get("BE4A"),dataItemMap.get("BE4B"),
                    dataItemMap.get("BE4C"),dataItemMap.get("BE4D"),
                    dataItemMap.get("BE4E"),dataItemMap.get("BE4F"),
                    dataItemMap.get("BE50"),dataItemMap.get("BE51"),
                    dataItemMap.get("BE52"),dataItemMap.get("BE53"),
                    dataItemMap.get("BE54"),dataItemMap.get("BE55"),
                    dataItemMap.get("BE56"),dataItemMap.get("BE57"),
                    dataItemMap.get("BE58"),dataItemMap.get("BE59"),
                    dataItemMap.get("BE5A"),dataItemMap.get("BE5B"),
                    dataItemMap.get("BE5C"),dataItemMap.get("BE5D")
                    );
        } catch (DataAccessException dataAccessException) {
            log.error(dataAccessException.getMessage());
        }
    }

    //不平衡度数据
    private void insertData_imbStatis(
            String logicalAddress,
            int gpSn,
            String dataDate,
            DtoItem dtoItem)
    {
        try {
            Map<String, String> dataItemMap = dtoItem.dataMap;
            imbStatisStoredProcedure.execute(
                    logicalAddress,
                    gpSn,
                    dataDate,
                    dataItemMap.get("BE5E"),
                    dataItemMap.get("BE5F"),
                    dataItemMap.get("2D01"),
                    dataItemMap.get("2D02"),
                    dataItemMap.get("2D03"),
                    dataItemMap.get("2D04")
                    );
        } catch (DataAccessException dataAccessException) {
            log.error(dataAccessException.getMessage());
        }
    }

    //日冻结越限数据
    private void insertData_dayEcurStatis(
            String logicalAddress,
            int gpSn,
            String dataDate,
            DtoItem dtoItem)
    {
        try {
            Map<String, String> dataItemMap = dtoItem.dataMap;
            this.ecurStatisStoredProcedure.execute(
                    logicalAddress,
                    gpSn,
                    dataDate,
                    dataItemMap.get("BE60"),dataItemMap.get("BE61"),
                    dataItemMap.get("BE62"),dataItemMap.get("BE63"),
                    dataItemMap.get("BE64"),dataItemMap.get("BE65"),
                    dataItemMap.get("BE66"),dataItemMap.get("BE67"),
                    dataItemMap.get("BE68"),dataItemMap.get("BE69"),
                    dataItemMap.get("BE6A"),dataItemMap.get("BE6B"),
                    dataItemMap.get("BE6C"),dataItemMap.get("BE6D"),
                    dataItemMap.get("BE6E")
                    );
        } catch (DataAccessException dataAccessException) {
            log.error(dataAccessException.getMessage());
        }
    }


    //温度和湿度
    private void insertData_HUMITURE(String logicalAddress, int gpSn, String humiture, String temperature) {
        try {
            humitureStoredProcedure.execute(logicalAddress, gpSn, humiture, temperature);
        } catch (Exception e) {
            log.error("错误信息：", e.fillInStackTrace());
        }
    }

    //设备事件
    private void insertData_Event_Data(String logicalAddress, byte gpSn, String dataDate,
            String p_act_total, String p_act_sharp, String p_act_peak, String p_act_level, String p_act_valley) {
        try {
        } catch (DataAccessException dataAccessException) {
            log.error(dataAccessException.getMessage());
        }
    }
    
    //漏保控制字
    private void insertData_PsCtrlPara(String logicalAddress,
            int gpSn,
            String dataDate,
            DtoItem dtoItem)
    {
        try {
            Map<String, String> dataItemMap = dtoItem.dataMap;
            this.psCtrlParaStoredProcedure.execute(
                    logicalAddress,
                    gpSn,
                    dataItemMap.get("100C020001"),//时间
                    dataItemMap.get("100C020017"),//现场/远程
                    dataItemMap.get("100C020018"),//数据告警
                    dataItemMap.get("100C020019"),//报警灯光
                    dataItemMap.get("100C020020"),//报警声音
                    dataItemMap.get("100C020021"),//定时试跳
                    dataItemMap.get("100C020022"),//档位返回
                    dataItemMap.get("100C020023"),//重合闸
                    dataItemMap.get("100C020025"),//欠压保护-数据告警
                    dataItemMap.get("100C020026"),//欠压保护-跳闸控制
                    dataItemMap.get("100C020027"),//过压保护-数据告警
                    dataItemMap.get("100C020028"),//过压保护-跳闸控制
                    dataItemMap.get("100C020029"),//缺相保护-数据告警
                    dataItemMap.get("100C020030"),//缺相保护-跳闸控制
                    dataItemMap.get("100C020031"),//过流保护-数据告警
                    dataItemMap.get("100C020032"),//过流保护-跳闸控制
                    dataItemMap.get("100C020034"),//试跳源
                    dataItemMap.get("100C020036"),//缺零保护-数据告警
                    dataItemMap.get("100C020038"),//缺零保护-跳闸控制
                    dataItemMap.get("100C020039"),//II档额定剩余电流
                    dataItemMap.get("100C020040"),//I档额定剩余电流
                    dataItemMap.get("100C020041"),//极限不驱动时间
                    dataItemMap.get("100C020042")//漏电报警时间

                    );

        } catch (Exception e) {
            log.error("错误信息：", e.fillInStackTrace());
        }
    }
    
    private void insertData_PsCustomPara(String logicalAddress,
            int gpSn,
            String dataDate,
            DtoItem dtoItem)
    {
        try {
            Map<String, String> dataItemMap = dtoItem.dataMap;
            this.psCustomParaStoreProcedure.execute(
                    logicalAddress,
                    gpSn,
                    dataItemMap.get("100C020101"),//过压超限值
                    dataItemMap.get("100C020102"),//欠压超限值
                    dataItemMap.get("100C020103"),//缺相超限值
                    dataItemMap.get("100C020104"),//额定电流值
                    dataItemMap.get("100C020105"),//定时试跳
                    dataItemMap.get("100C020106"),//档位返回
                    dataItemMap.get("100C020107"),//重合闸
                    dataItemMap.get("100C020108"),//欠压保护-数据告警
                    dataItemMap.get("100C020109"),//欠压保护-跳闸控制
                    dataItemMap.get("100C020110"),//过压保护-数据告警
                    dataItemMap.get("100C020111"),//过压保护-跳闸控制
                    dataItemMap.get("100C020112")//缺相保护-数据告警
                    
                    );

        } catch (Exception e) {
            log.error("错误信息：", e.fillInStackTrace());
        }
    }

    /**
     * @return the eccurvStoredProcedure
     */
    public ECCURV_StoredProcedure getEccurvStoredProcedure() {
        return eccurvStoredProcedure;
    }

    /**
     * @param eccurvStoredProcedure the eccurvStoredProcedure to set
     */
    public void setEccurvStoredProcedure(ECCURV_StoredProcedure eccurvStoredProcedure) {
        this.eccurvStoredProcedure = eccurvStoredProcedure;
    }

    /**
     * @return the eventStoredProcedure
     */
    public EventStoredProcedure getEventStoredProcedure() {
        return eventStoredProcedure;
    }

    /**
     * @param eventStoredProcedure the eventStoredProcedure to set
     */
    public void setEventStoredProcedure(EventStoredProcedure eventStoredProcedure) {
        this.eventStoredProcedure = eventStoredProcedure;
    }

    /**
     * @return the loubaoEventStoredProcedure
     */
    public LouBaoEvent36_StoredProcedure getLoubaoEvent36_StoredProcedure() {
        return loubaoEvent36_StoredProcedure;
    }

    /**
     * @param loubaoEventStoredProcedure the loubaoEventStoredProcedure to set
     */
    public void setLoubaoEvent36_StoredProcedure(LouBaoEvent36_StoredProcedure loubaoEventStoredProcedure) {
        this.loubaoEvent36_StoredProcedure = loubaoEventStoredProcedure;
    }

    /**
     * @return the loubaoEventStoredProcedure
     */
    public LouBaoEvent42_StoredProcedure getLoubaoEvent42_StoredProcedure() {
        return loubaoEvent42_StoredProcedure;
    }

    /**
     * @param loubaoEventStoredProcedure the loubaoEventStoredProcedure to set
     */
    public void setLoubaoEvent42_StoredProcedure(LouBaoEvent42_StoredProcedure loubaoEventStoredProcedure) {
        this.loubaoEvent42_StoredProcedure = loubaoEventStoredProcedure;
    }

    /**
     * @return the eccurvStoredProcedure2
     */
    public ECCURV_StoredProcedure2 getEccurvStoredProcedure2() {
        return eccurvStoredProcedure2;
    }

    /**
     * @param eccurvStoredProcedure2 the eccurvStoredProcedure2 to set
     */
    public void setEccurvStoredProcedure2(ECCURV_StoredProcedure2 eccurvStoredProcedure2) {
        this.eccurvStoredProcedure2 = eccurvStoredProcedure2;
    }

    /**
     * @return the powerCurvStoredProcedure2
     */
    public PowerCurv_StoredProcedure2 getPowerCurvStoredProcedure2() {
        return powerCurvStoredProcedure2;
    }

    /**
     * @param powerCurvStoredProcedure2 the powerCurvStoredProcedure2 to set
     */
    public void setPowerCurvStoredProcedure2(PowerCurv_StoredProcedure2 powerCurvStoredProcedure2) {
        this.powerCurvStoredProcedure2 = powerCurvStoredProcedure2;
    }

    /**
     * @return the powerCurvStoredProcedure
     */
    public PowerCurv_StoredProcedure getPowerCurvStoredProcedure() {
        return powerCurvStoredProcedure;
    }

    /**
     * @param powerCurvStoredProcedure the powerCurvStoredProcedure to set
     */
    public void setPowerCurvStoredProcedure(PowerCurv_StoredProcedure powerCurvStoredProcedure) {
        this.powerCurvStoredProcedure = powerCurvStoredProcedure;
    }

    /**
     * @return the humitureStoredProcedure
     */
    public Humiture_StoredProcedure getHumitureStoredProcedure() {
        return humitureStoredProcedure;
    }

    /**
     * @param humitureStoredProcedure the humitureStoredProcedure to set
     */
    public void setHumitureStoredProcedure(Humiture_StoredProcedure humitureStoredProcedure) {
        this.humitureStoredProcedure = humitureStoredProcedure;
    }

    /**
     * @return the p_actStoredProcedure
     */
    public P_ACT_StoredProcedure getP_actStoredProcedure() {
        return p_actStoredProcedure;
    }

    /**
     * @param p_actStoredProcedure the p_actStoredProcedure to set
     */
    public void setP_actStoredProcedure(P_ACT_StoredProcedure p_actStoredProcedure) {
        this.p_actStoredProcedure = p_actStoredProcedure;
    }

    /**
     * @return the p_reactStoredProcedure
     */
    public P_REACT_StoredProcedure getP_reactStoredProcedure() {
        return p_reactStoredProcedure;
    }

    /**
     * @param p_reactStoredProcedure the p_reactStoredProcedure to set
     */
    public void setP_reactStoredProcedure(P_REACT_StoredProcedure p_reactStoredProcedure) {
        this.p_reactStoredProcedure = p_reactStoredProcedure;
    }

    /**
     * @return the i_reactStoredProcedure
     */
    public I_REACT_StoredProcedure getI_reactStoredProcedure() {
        return i_reactStoredProcedure;
    }

    /**
     * @param i_reactStoredProcedure the i_reactStoredProcedure to set
     */
    public void setI_reactStoredProcedure(I_REACT_StoredProcedure i_reactStoredProcedure) {
        this.i_reactStoredProcedure = i_reactStoredProcedure;
    }

    /**
     * @return the i_actStoredProcedure
     */
    public I_ACT_StoredProcedure getI_actStoredProcedure() {
        return i_actStoredProcedure;
    }

    /**
     * @param i_actStoredProcedure the i_actStoredProcedure to set
     */
    public void setI_actStoredProcedure(I_ACT_StoredProcedure i_actStoredProcedure) {
        this.i_actStoredProcedure = i_actStoredProcedure;
    }

    /**
     * @return the ecurStatisStoredProcedur
     */
    public DAY_ECUR_STATIS_StoredProcedure getEcurStatisStoredProcedur() {
        return ecurStatisStoredProcedure;
    }

    /**
     * @param ecurStatisStoredProcedur the ecurStatisStoredProcedur to set
     */
    public void setEcurStatisStoredProcedur(DAY_ECUR_STATIS_StoredProcedure ecurStatisStoredProcedur) {
        this.ecurStatisStoredProcedure = ecurStatisStoredProcedur;
    }

    /**
     * @return the voltStatisStoredProcedur
     */
    public DAY_VOLT_STATIS_StoredProcedure getVoltStatisStoredProcedur() {
        return voltStatisStoredProcedure;
    }

    /**
     * @param voltStatisStoredProcedur the voltStatisStoredProcedur to set
     */
    public void setVoltStatisStoredProcedur(DAY_VOLT_STATIS_StoredProcedure voltStatisStoredProcedur) {
        this.voltStatisStoredProcedure = voltStatisStoredProcedur;
    }

    /**
     * @return the imbStatisStoredProcedur
     */
    public DAY_IMB_STATIS_StoredProcedure getImbStatisStoredProcedur() {
        return imbStatisStoredProcedure;
    }

    /**
     * @param imbStatisStoredProcedur the imbStatisStoredProcedur to set
     */
    public void setImbStatisStoredProcedur(DAY_IMB_STATIS_StoredProcedure imbStatisStoredProcedur) {
        this.imbStatisStoredProcedure = imbStatisStoredProcedur;
    }

    /**
     * @return the pfcurvStoredProcedur
     */
    public PFCURV_StoredProcedure getPfcurvStoredProcedur() {
        return pfcurvStoredProcedure;
    }

    /**
     * @param pfcurvStoredProcedur the pfcurvStoredProcedur to set
     */
    public void setPfcurvStoredProcedur(PFCURV_StoredProcedure pfcurvStoredProcedur) {
        this.pfcurvStoredProcedure = pfcurvStoredProcedur;
    }

    /**
     * @return the psStatusStoredProcedur
     */
    public PSStatus_StoredProcedure getPsStatusStoredProcedur() {
        return psStatusStoredProcedure;
    }

    /**
     * @param psStatusStoredProcedur the psStatusStoredProcedur to set
     */
    public void setPsStatusStoredProcedur(PSStatus_StoredProcedure psStatusStoredProcedur) {
        this.psStatusStoredProcedure = psStatusStoredProcedur;
    }

    /**
     * @return the psCtrlParaStoredProcedure
     */
    public PSCtrlPara_StoredProcedure getPsCtrlParaStoredProcedure() {
        return psCtrlParaStoredProcedure;
    }

    /**
     * @param psCtrlParaStoredProcedure the psCtrlParaStoredProcedure to set
     */
    public void setPsCtrlParaStoredProcedure(PSCtrlPara_StoredProcedure psCtrlParaStoredProcedure) {
        this.psCtrlParaStoredProcedure = psCtrlParaStoredProcedure;
    }

    /**
     * @return the psCustomParaStoreProcedure
     */
    public PSCustomPara_StoredProcedure getPsCustomParaStoreProcedure() {
        return psCustomParaStoreProcedure;
    }

    /**
     * @param psCustomParaStoreProcedure the psCustomParaStoreProcedure to set
     */
    public void setPsCustomParaStoreProcedure(PSCustomPara_StoredProcedure psCustomParaStoreProcedure) {
        this.psCustomParaStoreProcedure = psCustomParaStoreProcedure;
    }


}
