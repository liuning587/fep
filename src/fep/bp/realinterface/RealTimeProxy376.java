/*
 * 基于376国网规范的实时交互代理类
 */
package fep.bp.realinterface;

import fep.bp.dal.RTTaskService;
import fep.bp.model.RTTaskRecvDAO;
import fep.bp.model.RealTimeTaskDAO;
import fep.bp.realinterface.conf.ProtocolConfig;
import fep.bp.realinterface.conf.ProtocolDataItem;
import fep.bp.realinterface.mto.*;
import fep.bp.utils.AFNType;
import fep.bp.utils.decoder.Decoder;
import fep.bp.utils.encoder.Encoder;
import fep.codec.protocol.gb.*;
import fep.codec.protocol.gb.gb376.PmPacket376;
import fep.codec.protocol.gb.gb376.PmPacket376DA;
import fep.codec.protocol.gb.gb376.PmPacket376DT;
import fep.codec.utils.BcdUtils;
import fep.meter645.Gb645MeterPacket;
import java.io.IOException;
import java.util.*;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

/**
 *
 * @author Thinkpad
 */
public class RealTimeProxy376 implements RealTimeInterface {

    private static int ID;
    private final int FAILCODE = -1;
    private final int cmdItemNum = 1;
    private final static Logger log = LoggerFactory.getLogger(RealTimeProxy376.class);

    private Decoder decoder;
    private Encoder encoder;

    private RTTaskService taskService;
    public void setTaskService(RTTaskService rtTaskService) {
        this.taskService = rtTaskService;
    }


    private int getID() {
        return taskService.getSequnce();
    }

    protected List<RealTimeTaskDAO> Encode(MessageTranObject MTO, int sequenceCode, byte AFN) {
        MTO_376 mto = (MTO_376) MTO;
        List<RealTimeTaskDAO> tasks = new ArrayList<RealTimeTaskDAO>();
        StringBuilder gpMark = new StringBuilder();
        StringBuilder commandMark = new StringBuilder();

        try {
            for (CollectObject obj : mto.getCollectObjects()) {
                gpMark.delete(0, gpMark.length());
                commandMark.delete(0, commandMark.length());

                List<PmPacket376> packetList = encoder.EncodeList(obj, AFN);
                for (PmPacket376 packet : packetList) {
                    RealTimeTaskDAO task = new RealTimeTaskDAO();
                    task.setSendmsg(BcdUtils.binArrayToString(packet.getValue()));
                    task.setSequencecode(sequenceCode);
                    task.setLogicAddress(obj.getLogicalAddr());
                    task.setGpMark(packet.getMpSnRemark());
                    task.setCommandMark(packet.getCommandRemark());
                    tasks.add(task);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return tasks;
    }

    
    protected List<RealTimeTaskDAO> Encode_TransMit(MessageTranObject MTO, int sequenceCode) throws IOException {
        MTO_376 mto = (MTO_376) MTO;  
        List<RealTimeTaskDAO> tasks = new ArrayList<RealTimeTaskDAO>();
        ProtocolConfig config = ProtocolConfig.getInstance();//获取配置文件对象
        StringBuilder gpMark = new StringBuilder();
        StringBuilder commandMark = new StringBuilder();
        try {
            for (CollectObject_TransMit obj : mto.getCollectObjects_Transmit()) {
                List<PmPacket376> packetList = this.encoder.EncodeList_TransMit(obj);
                for (PmPacket376 packet : packetList) {
                    RealTimeTaskDAO task = new RealTimeTaskDAO();
                    task.setSendmsg(BcdUtils.binArrayToString(packet.getValue()));
                    task.setSequencecode(sequenceCode);
                    task.setLogicAddress(obj.getTerminalAddr());
                    task.setGpMark(gpMark.toString());
                    task.setCommandMark(commandMark.toString());
                    tasks.add(task);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return tasks;
    }
    
    
    /**
     * 参数设置
     * @param MTO 消息传输对象
     * @return 回执码(-1:表示失败)
     * @throws Exception
     */
    @Override
    public long writeParameters(MessageTranObject MTO) throws Exception {
        try {
            if ((null == MTO) || (MTO.getType() != MTOType.GW_376)) {
                return FAILCODE;

            } else {
                int sequenceCode = getID();
                List<RealTimeTaskDAO> tasks = this.Encode(MTO, sequenceCode, AFNType.AFN_SETPARA);
                for (RealTimeTaskDAO task : tasks) {
                    this.taskService.insertTask(task);
                }
                return sequenceCode;
            }
        } catch (NumberFormatException numberFormatException) {
            log.error(numberFormatException.getMessage());
            return FAILCODE;
        } catch (DataAccessException dataAccessException) {
            log.error(dataAccessException.getMessage());
            return FAILCODE;
        }
    }

    /**
     * 参数读取
     * @param MTO 消息传输对象
     * @return 回执码（-1:表示失败）
     * @throws Exception
     */
    @Override
    public long readParameters(MessageTranObject MTO) throws Exception {
        try {
            if ((null == MTO) || (MTO.getType() != MTOType.GW_376)) {
                return FAILCODE;
            } else {
                int sequenceCode = getID();
                List<RealTimeTaskDAO> tasks = this.Encode(MTO, sequenceCode, AFNType.AFN_GETPARA);
                for (RealTimeTaskDAO task : tasks) {
                    this.taskService.insertTask(task);
                }
                return sequenceCode;
            }

        } catch (NumberFormatException numberFormatException) {
            log.error(numberFormatException.getMessage());
            return FAILCODE;
        } catch (DataAccessException dataAccessException) {
            log.error(dataAccessException.getMessage());
            return FAILCODE;
        }
    }

    /**
     * 下发复位命令
     * @param MTO
     * @return 回执码（-1:表示失败）
     * @throws Exception
     */
    @Override
    public long writeResetCommands(MessageTranObject MTO) throws Exception {
        try {
            if ((null == MTO) || (MTO.getType() != MTOType.GW_376)) {
                return FAILCODE;
            } else {
                int sequenceCode = getID();
                List<RealTimeTaskDAO> tasks = this.Encode(MTO, sequenceCode, AFNType.AFN_RESET);
                for (RealTimeTaskDAO task : tasks) {
                    this.taskService.insertTask(task);
                }
                return sequenceCode;
            }

        } catch (NumberFormatException numberFormatException) {
            log.error(numberFormatException.getMessage());
            return FAILCODE;
        } catch (DataAccessException dataAccessException) {
            log.error(dataAccessException.getMessage());
            return FAILCODE;
        }
    }

    /**
     * 实时召测
     * @param MTO
     * @return
     * @throws Exception
     */
    @Override
    public long readData(MessageTranObject MTO) throws Exception {
        try {
            if ((null == MTO) || (MTO.getType() != MTOType.GW_376)) {
                return FAILCODE;
            } else {
                int sequenceCode = getID();
                List<RealTimeTaskDAO> tasks = this.Encode(MTO, sequenceCode, AFNType.AFN_READDATA1);
                for (RealTimeTaskDAO task : tasks) {
                    this.taskService.insertTask(task);
                }
                return sequenceCode;
            }
        } catch (NumberFormatException numberFormatException) {
            log.error(numberFormatException.getMessage());
            return FAILCODE;
        } catch (DataAccessException dataAccessException) {
            log.error(dataAccessException.getMessage());
            return FAILCODE;
        }
    }

    /**
     * 透明转发
     * @param MTO
     * @return
     * @throws Exception
     */
    @Override
    public long transmitMsg(MessageTranObject MTO) throws Exception {
        try {
            if ((null == MTO) || (MTO.getType() != MTOType.GW_376)) {
                return FAILCODE;
            } else {
                int sequenceCode = getID();

                List<RealTimeTaskDAO> tasks = Encode_TransMit(MTO, sequenceCode);
                for (RealTimeTaskDAO task : tasks) {
                    this.taskService.insertTask(task);
                }
                return sequenceCode;
            }
        } catch (NumberFormatException numberFormatException) {
            log.error(numberFormatException.getMessage());
            return FAILCODE;
        } catch (DataAccessException dataAccessException) {
            log.error(dataAccessException.getMessage());
            return FAILCODE;
        }
    }

    /**
     * 获取参数设置结果
     * @param appId 回执码
     * @return 返回结果<"zdljdz#cldxh#commanditem", "result">
     * @throws Exception
     */
    @Override
    public Map<String, String> getReturnByWriteParameter(long appId) throws Exception {
        List<RealTimeTaskDAO> tasks = this.taskService.getTasks(appId);
        StringBuilder sb = new StringBuilder();
        Map<String, String> results = new HashMap<String, String>();
        try {
            for (RealTimeTaskDAO task : tasks) {
                String logicAddress = task.getLogicAddress();
                String gpMark = task.getGpMark();
                String[] GpArray = null;
                if (null != gpMark) {
                    GpArray = task.getGpMark().split("#");
                }

                String[] CommandArray = task.getCommandMark().split("#");
                List<RTTaskRecvDAO> recvs = task.getRecvMsgs();
                PmPacket376 packet = new PmPacket376();
                for (RTTaskRecvDAO recv : recvs) {
                    byte[] msg = BcdUtils.stringToByteArray(recv.getRecvMsg());
                    packet.setValue(msg, 0);
                    PmPacketData dataBuf = packet.getDataBuffer();
                    dataBuf.rewind();
                    dataBuf.getDA(new PmPacket376DA());
                    PmPacket376DT dt = new PmPacket376DT();
                    dataBuf.getDT(dt);
                    int result = dt.getFn();
                    //全部确认或否认
                    if (result < 3) {
                        for (int i = 0; i < GpArray.length; i++) {
                            for (int j = 0; j < CommandArray.length; j++) {
                                String key = logicAddress + "#" + String.valueOf(GpArray[i]) + "#" + String.valueOf(CommandArray[i]);
                                String value = String.valueOf(result);
                                results.put(key, value);
                            }
                        }
                    }
                    //逐项确认
                    if (result == 3) {
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return results;
    }
 
    /**
     * 获取参数读取结果
     * @param appId 回执码
     * @return 返回结果<"zdljdz#cldxh#commanditem", <"dataitem", "datavalue">>
     * @throws Exception
     */
    @Override
    public Map<String, Map<String, String>> getReturnByReadParameter(long appId) throws Exception {
        List<RealTimeTaskDAO> tasks = this.taskService.getTasks(appId);
        StringBuilder sb = new StringBuilder();
        Map<String, Map<String, String>> results = null;
        for (RealTimeTaskDAO task : tasks) {
            String logicAddress = task.getLogicAddress();
            //String[] GpArray = task.getGpMark().split("#");
            //String[] CommandArray = task.getCommandMark().split("#");
            List<RTTaskRecvDAO> recvs = task.getRecvMsgs();
            PmPacket376 packet = new PmPacket376();
            for (RTTaskRecvDAO recv : recvs) {
                byte[] msg = BcdUtils.stringToByteArray(recv.getRecvMsg());
                packet.setValue(msg, 0);
                results = decoder.decode2Map(packet);
            }
        }
        return results;
    }


    /**
     * 获取复位操作结果
     * @param appId
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, String> getReturnByWriteResetCommand(long appId) throws Exception {
        return null;
    }

    /**
     * 获取实时召测返回结果
     * @param appId:任务序列号，调用readData时返回，是前台交互的唯一认证
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Map<String, String>> getReturnByReadData(long appId) throws Exception {
        List<RealTimeTaskDAO> tasks = this.taskService.getTasks(appId);
        StringBuilder sb = new StringBuilder();
        Map<String, Map<String, String>> tempMap = null;
        for (RealTimeTaskDAO task : tasks) {
            String logicAddress = task.getLogicAddress();
            List<RTTaskRecvDAO> recvs = task.getRecvMsgs();
            PmPacket376 packet = new PmPacket376();
            for (RTTaskRecvDAO recv : recvs) {
                byte[] msg = BcdUtils.stringToByteArray(recv.getRecvMsg());
                packet.setValue(msg, 0);
                tempMap = decoder.decode2Map(packet);
            }
        }
        return Deal2DataMap(tempMap);
    }

    /**
     * 透明转发读参数，如：读漏保参数
     * @param appId
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Map<String, String>> readTransmitPara(long appId) throws Exception {
        List<RealTimeTaskDAO> tasks = this.taskService.getTasks(appId);
        StringBuilder sb = new StringBuilder();
        Map<String, Map<String, String>> tempMap = new HashMap<String, Map<String, String>>();
        for (RealTimeTaskDAO task : tasks) {
            String logicAddress = task.getLogicAddress();
            List<RTTaskRecvDAO> recvs = task.getRecvMsgs();
            PmPacket376 packet = new PmPacket376();
            for (RTTaskRecvDAO recv : recvs) {
                byte[] msg = BcdUtils.stringToByteArray(recv.getRecvMsg());
                packet.setValue(msg, 0);
                tempMap = decoder.decode2Map_TransMit(packet);
            }
        }
        return tempMap;
    }

    @Override
    public Map<String, Map<String, String>> readTransmitWriteBack(long appId) throws Exception {
        List<RealTimeTaskDAO> tasks = this.taskService.getTasks(appId);
        StringBuilder sb = new StringBuilder();
        Map<String, Map<String, String>> tempMap = new HashMap<String, Map<String, String>>();
        for (RealTimeTaskDAO task : tasks) {
            String logicAddress = task.getLogicAddress();
            List<RTTaskRecvDAO> recvs = task.getRecvMsgs();
            PmPacket376 packet = new PmPacket376();
            for (RTTaskRecvDAO recv : recvs) {
                byte[] msg = BcdUtils.stringToByteArray(recv.getRecvMsg());
                packet.setValue(msg, 0);
                tempMap = decoder.decode2Map_TransMit_WriteBack(packet);
            }
        }
        return tempMap;
    }



    @Override
    public Map<String, Map<String, String>> readTransmitData(long appId) throws Exception {
        List<RealTimeTaskDAO> tasks = this.taskService.getTasks(appId);
        StringBuilder sb = new StringBuilder();
        Map<String, Map<String, String>> tempMap = new HashMap<String, Map<String, String>>();
        for (RealTimeTaskDAO task : tasks) {
            String logicAddress = task.getLogicAddress();
            List<RTTaskRecvDAO> recvs = task.getRecvMsgs();
            PmPacket376 packet = new PmPacket376();
            for (RTTaskRecvDAO recv : recvs) {
                byte[] msg = BcdUtils.stringToByteArray(recv.getRecvMsg());
                packet.setValue(msg, 0);
                tempMap = decoder.decode2Map(packet);
            }
        }
        return Deal2DataMap(tempMap);
    }
    
    /**
     * 获取保护器写参数返回结果
     * @param appId：任务回执码
     * @return 
     */
    public Map<String, String> getReturnByWriteParameter_TransMit(long appId) throws Exception {
        List<RealTimeTaskDAO> tasks = this.taskService.getTasks(appId);
        StringBuilder sb = new StringBuilder();
        Map<String, String> results = new HashMap<String, String>();
        for (RealTimeTaskDAO task : tasks) {
            String logicAddress = task.getLogicAddress();
            List<RTTaskRecvDAO> recvs = task.getRecvMsgs();
            PmPacket376 packet = new PmPacket376();
            Map<String, String> resultList ;
            for (RTTaskRecvDAO recv : recvs) {               
                byte[] msg = BcdUtils.stringToByteArray(recv.getRecvMsg());
                packet.setValue(msg, 0);
                resultList = decoder.decode2Map_TransMit_WriteParameterBack(packet);
                if(resultList!=null)
                {
                    results.putAll(resultList);
                }
            }
        }
        return results;
    }
    
    public Map<String, String> getReturnByControl_TransMit(long appId) throws Exception {
        List<RealTimeTaskDAO> tasks = this.taskService.getTasks(appId);
        StringBuilder sb = new StringBuilder();
        Map<String, String> results = new HashMap<String, String>();
        for (RealTimeTaskDAO task : tasks) {
            String logicAddress = task.getLogicAddress();
            List<RTTaskRecvDAO> recvs = task.getRecvMsgs();
            PmPacket376 packet = new PmPacket376();
            Map<String, String> resultList ;
            for (RTTaskRecvDAO recv : recvs) {               
                byte[] msg = BcdUtils.stringToByteArray(recv.getRecvMsg());
                packet.setValue(msg, 0);
                resultList = decoder.decode2Map_TransMit_ControlBack(packet);
                if(resultList!=null)
                {
                    results.putAll(resultList);
                }
            }
        }
        return results;
    }

    private Map<String, Map<String, String>> Deal2DataMap(Map<String, Map<String, String>> sourceMap) throws IOException {
        String dataItemCode ;
        Map<String, Map<String, String>> results = new TreeMap<String, Map<String, String>>();
        ProtocolConfig config = ProtocolConfig.getInstance();//获取配置文件对象
        Iterator iterator = sourceMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String keyInner;
            String CommandItemCode = key.split("#")[2];
            Map<String, String> DataMap = sourceMap.get(key);
            Iterator iterator2 = DataMap.keySet().iterator();
            
            while (iterator2.hasNext()) {
                Map<String, String> resultMap = new TreeMap<String, String>();
                dataItemCode = (String) iterator2.next();
                String dataValue = DataMap.get(dataItemCode);
                ProtocolDataItem dataItem = config.getDataItemMap(CommandItemCode).get(dataItemCode);
                String IsTd = dataItem.getIsTd();
                if (IsTd.equals("1")) {
                    keyInner = dataValue;
                } else {
                    keyInner = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
                }
                resultMap.put(keyInner, dataValue);
                results.put(key + "#" + dataItemCode, resultMap);
            }

        }
        return results;
    }

    /**
     * @param decoder the decoder to set
     */
    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    /**
     * @param encoder the encoder to set
     */
    public void setEncoder(Encoder encoder) {
        this.encoder = encoder;
    }
}
