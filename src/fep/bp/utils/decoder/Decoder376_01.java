/*
 * 376 Decoder Q
 */

package fep.bp.utils.decoder;

import fep.bp.model.Dto;
import fep.bp.model.Dto.DtoItem;
import fep.bp.utils.UtilsBp;
import fep.codec.protocol.gb.PmPacketData;
import fep.codec.protocol.gb.gb376.PmPacket376;
import fep.codec.protocol.gb.gb376.PmPacket376DA;
import fep.codec.protocol.gb.gb376.PmPacket376DT;
import fep.codec.utils.BcdUtils;
import fep.meter645.Gb645MeterPacket;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author THINKPAD
 */
public class Decoder376_01 extends Decoder376{

    private final static Logger log = LoggerFactory.getLogger(Decoder376_01.class);
    @Override
    public void decode2dto_TransMit(Object pack, Dto dto) {
        try {
            PmPacket376 packet = (PmPacket376) pack;
            String logicAddress = packet.getAddress().getRtua();
            dto.setLogicAddress(logicAddress);
            PmPacketData dataBuffer = getDataBuffer(packet);
 
            PmPacket376DA da = getDA(dataBuffer);
            PmPacket376DT dt = getDt(dataBuffer);
            byte afn = packet.getAfn();
            int gp = da.getPn();
            dto.setAfn(afn);

            Gb645MeterPacket packet645 = getInnerDataPacket(dataBuffer);//抽取内部包
            PmPacketData dataBuffer645 = packet645.getDataAsPmPacketData();
            String commandItemCode = getCommandItemCode(dataBuffer645);
            readSwitchStatus(commandItemCode, dataBuffer645);
            DtoItem dtoItem = dto.addDataItem(gp, UtilsBp.getNow(), commandItemCode);
            dtoItem.dataMap = this.dataBuffer2Map(commandItemCode, dataBuffer645);
        } catch (Exception e) {
            log.error("错误信息：", e.fillInStackTrace());
        }
    }
    
    @Override
    public Map<String, Map<String, String>> decode2Map_TransMit_WriteBack(Object pack) {
        try {
            Map<String, Map<String, String>> results = new TreeMap<String, Map<String, String>>();
            PmPacket376 packet = (PmPacket376) pack;
            InnerDataBuffer InnerData =  getDataBuffer645(packet);
            PmPacketData dataBuffer645 = InnerData.getInnerPacketData();
            String commandItemCode = "8000C040";
            readSwitchStatus(commandItemCode, dataBuffer645);//针对8000C040、8000C04F、8000B66F将附带的开关信息状态读掉
            Map<String, String> dataItems = this.dataBuffer2Map(commandItemCode, dataBuffer645);
            results.put(InnerData.getKey(), dataItems);
            return results;
        } catch (Exception e) {
            log.error("错误信息：", e.fillInStackTrace());
            return null;
        }

    }

    /**
     * 针对透明转发类报文的规约解析
     * @param pack：报文对象
     * @param IsWriteBack：针对漏保跳合闸设置，会返回8000C040的内容，用这个参数标志是否是解析设置返回信息
     * @return：返回嵌套map的数据结构
     */
    @Override
    public Map<String, Map<String, String>> decode2Map_TransMit(Object pack) {

        try {
            Map<String, Map<String, String>> results = new TreeMap<String, Map<String, String>>();
            PmPacket376 packet = (PmPacket376) pack;

            InnerDataBuffer InnerData =  getDataBuffer645(packet);
            PmPacketData dataBuffer645 = InnerData.getInnerPacketData();
            String commandItemCode = getCommandItemCode(dataBuffer645);
            readSwitchStatus(commandItemCode, dataBuffer645);//针对8000C040、8000C04F、8000B66F将附带的开关信息状态读掉
            Map<String, String> dataItems = this.dataBuffer2Map(commandItemCode, dataBuffer645);
            results.put(InnerData.getKey(), dataItems);
            return results;
        } catch (Exception e) {
            log.error("错误信息：", e.fillInStackTrace());
            return null;
        }

    }
    
    /**
     * 针对透明转发类设置参数报文返回的规约解析
     * @param pack：报文对象
     * @return：返回嵌套map的数据结构：key = logicAddress + "#" + Mpsn + "#" + CommandCode;  value = 1(确认) 2（否认）
     */
    @Override
    public Map<String, String> decode2Map_TransMit_WriteParameterBack(Object pack,String[] GpArray,String[] CommandArray) {
        return super.decodeTransMitBack(pack, 0x84, 0xc1,GpArray,CommandArray);
    }
    
    /**
     * 针对透明转发类控制操作报文返回的规约解析
     * @param pack：报文对象
     * @return 返回嵌套map的数据结构：key = logicAddress + "#" + Mpsn + "#" + CommandCode;  value = 1(确认) 2（否认）
     */
    @Override
    public Map<String, String> decode2Map_TransMit_ControlBack(Object pack,String[] GpArray,String[] CommandArray)
    {
        return super.decodeTransMitBack(pack, 0x84, 0xc1,GpArray,CommandArray);
    }

    private void readSwitchStatus(String commandItemCode, PmPacketData dataBuffer645) {
        if ((!commandItemCode.equals("8000C040")) && (!commandItemCode.equals("8000C04F")) && (!commandItemCode.equals("8000B66F"))) {
            dataBuffer645.get();
        }
    }
}
