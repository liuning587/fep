/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fep.bp.utils.encoder.encoder376;

import fep.bp.realinterface.conf.ProtocolDataItem;
import fep.bp.realinterface.mto.CircleDataItems;
import fep.bp.realinterface.mto.CollectObject;
import fep.bp.realinterface.mto.CollectObject_TransMit;
import fep.bp.realinterface.mto.CommandItem;
import fep.bp.realinterface.mto.DataItem;
import fep.bp.realinterface.mto.DataItemGroup;
import fep.bp.utils.AFNType;
import fep.bp.utils.TermProtocol;
import fep.bp.utils.UtilsBp;
import fep.bp.utils.encoder.Encoder;
import fep.codec.protocol.gb.*;
import fep.codec.protocol.gb.gb376.PmPacket376;
import fep.codec.protocol.gb.gb376.PmPacket376DA;
import fep.codec.protocol.gb.gb376.PmPacket376DT;
import fep.codec.utils.BcdUtils;
import fep.meter645.Gb645MeterPacket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author THINKPAD
 */
public class Encoder376 extends Encoder{
    private final static Logger log = LoggerFactory.getLogger(Encoder376.class);
    private static final byte FUNCODE_DOWM_1 = 1;//PRM =1 功能码：1 （发送/确认）复位
    private static final byte FUNCODE_DOWM_4 = 4;//用户数据 (发送∕无回答)	
    private static final byte FUNCODE_DOWM_10 = 10;//请求1级数据 (请求∕响应帧
    private static final byte FUNCODE_DOWM_11 = 11;//请求1级数据 (请求∕响应帧)
    private final int UPGRADE_FILE_SEGMENT_SIZE = 512;//升级文件每段的最大长度

    private String groupValue = "";
    private int groupBinValue = 0;
    private byte bits = 8;

   /* 
    @Override
    public PmPacket Encode(CollectObject obj, byte AFN) {
        try {
            PmPacket packet = new PmPacket376();
            preSetPacket(packet, AFN, obj.getLogicalAddr());
            int[] MpSn = obj.getMpSn();

            for (int i = 0; i <= MpSn.length - 1; i++) {
                List<CommandItem> CommandItems = obj.getCommandItems();
                for (CommandItem commandItem : CommandItems) {
                    PmPacket376DA da = new PmPacket376DA(MpSn[i]);
                    PmPacket376DT dt = new PmPacket376DT();
                    int fn = Integer.parseInt(commandItem.getIdentifier().substring(4, 8));//10+03+0002(protocolcode+afn+fn)
                    dt.setFn(fn);
                    packet.getDataBuffer().putDA(da);
                    packet.getDataBuffer().putDT(dt);
                    if (AFN == AFNType.AFN_READDATA2) {
                        putDataBuf_withValue((PmPacket376) packet,commandItem);
                    }
                    if ((AFN == AFNType.AFN_SETPARA)) {
                        putDataBuf_withValue((PmPacket376) packet,commandItem);
                    }
                }
                if (AFN == AFNType.AFN_RESET || AFN == AFNType.AFN_SETPARA || AFN == AFNType.AFN_TRANSMIT)//消息认证码字段PW
                {
                    packet.setAuthorize(new Authorize());
                }
            }
            packet.setTpv(new TimeProtectValue());//时间标签
            return packet;
        } catch (NumberFormatException numberFormatException) {
            log.error(numberFormatException.getMessage());
            return null;
        }
    }
    */
   
    @Override
    public List<PmPacket376> Encode(CollectObject obj, byte AFN) {
        try {
            String LogicalAddress = obj.getLogicalAddr();
            List<CommandItem> CommandItems = obj.getCommandItems();
            int[] MpSn = obj.getMpSn();
            switch(AFN)
            {
                case AFNType.AFN_GETPARA:
                    return this.Encode_ReadPara(LogicalAddress, MpSn, CommandItems,obj.getEquipProtocol());
                case AFNType.AFN_SETPARA:
                    return this.Encode_SetPara(LogicalAddress, MpSn, CommandItems,obj.getEquipProtocol());
                case AFNType.AFN_READDATA1:
                    return this.Encode_ReadData1(LogicalAddress, MpSn, CommandItems,obj.getEquipProtocol());
                case AFNType.AFN_READDATA2:
                    return this.Encode_ReadData2(LogicalAddress, MpSn, CommandItems,obj.getEquipProtocol());
                case AFNType.AFN_RESET:
                    return this.Encode_Reset(LogicalAddress, MpSn, CommandItems,obj.getEquipProtocol());
                default:
                    return null;
            }
        } catch (NumberFormatException numberFormatException) {
            log.error(numberFormatException.getMessage());
            return null;
        }
    }

    @Override
    public List<PmPacket376> EncodeList(CollectObject obj, byte AFN) {
        List<PmPacket376> results = new ArrayList<PmPacket376>();
        PmPacket376 packet = new PmPacket376();
        int Index = 1;
        int[] MpSn = obj.getMpSn();
        int DataBuffLen = MAX_PACKET_LEN - 16 - 22;//[68+L+L+68+C+A+AFN+SEQ+TP+PW+CS+16]
        int CmdItemNum = obj.getCommandItems().size();
        StringBuilder gpMark = new StringBuilder();
        StringBuilder commandMark = new StringBuilder();
        for (int i = 0; i <= MpSn.length - 1; i++) {
            gpMark.delete(0, gpMark.length());
            gpMark.append(String.valueOf(MpSn[i])).append("#");
            List<CommandItem> CommandItems = obj.getCommandItems();
            for (CommandItem commandItem : CommandItems) {
                if (NeedSubpackage(commandItem)) //针对类似F10的参数，按每帧最大长度进行自动分包处理
                {
                    cmdItem2PacketList_Subpacket(commandItem, AFN, obj.getLogicalAddr(), MpSn[i], DataBuffLen, results);
                }
                else {
                    if ((Index - 1) % CmdItemNum == 0) {
                        packet = new PmPacket376();
                        preSetPacket(packet, AFN, obj.getLogicalAddr());
                    }
                    commandMark.append(commandItem.getIdentifier()).append("#");
                    PmPacket376DA da = new PmPacket376DA(MpSn[i]);
                    PmPacket376DT dt = new PmPacket376DT();
                    int fn = Integer.parseInt(commandItem.getIdentifier().substring(4, 8));//10+03+0002(protocolcode+afn+fn)
                    dt.setFn(fn);
                    packet.getDataBuffer().putDA(da);
                    packet.getDataBuffer().putDT(dt);
                    if ((AFN == AFNType.AFN_SETPARA)||(AFN == AFNType.AFN_READDATA2)||(AFN == AFNType.AFN_CONTROL)) {
                        putDataBuf_withValue(packet, commandItem);
                    }
                    if (Index % CmdItemNum == 0) {
                        if (AFN == AFNType.AFN_RESET || AFN == AFNType.AFN_SETPARA || AFN == AFNType.AFN_TRANSMIT || AFN == AFNType.AFN_CONTROL)//消息认证码字段PW
                        {
                            packet.setAuthorize(new Authorize());
                        }
                        packet.setTpv(new TimeProtectValue());//时间标签
                        packet.setCommandRemark(commandMark.toString());
                        packet.setMpSnRemark(gpMark.toString());
                        results.add(packet);
                        commandMark.delete(0, commandMark.length());
                    }
                }
                Index++;
            }
        }
        return results;
    }

    @Override
    public List<PmPacket376> EncodeList_TransMit(CollectObject_TransMit obj,StringBuilder commandMark) {
        try {
            List<PmPacket376> results = new ArrayList<PmPacket376>();
            List<CommandItem> CommandItems = obj.getCommandItems();
            StringBuilder gpMark = new StringBuilder();
            for (CommandItem commandItem : CommandItems) {

                PmPacket376 packet = new PmPacket376();
                packet.setAfn(AFNType.AFN_TRANSMIT);//AFN
                packet.getAddress().setRtua(obj.getTerminalAddr()); //逻辑地址
                packet.getControlCode().setIsUpDirect(false);
                packet.getControlCode().setIsOrgniger(true);
                packet.getControlCode().setFunctionKey(getFunCode(AFNType.AFN_TRANSMIT));
                packet.getControlCode().setIsDownDirectFrameCountAvaliable(true);
                packet.getControlCode().setDownDirectFrameCount((byte) 0);
                packet.getSeq().setIsTpvAvalibe(true);
                packet.getSeq().setIsFirstFrame(true);
                packet.getSeq().setIsFinishFrame(true);

                commandMark.append(commandItem.getIdentifier()).append("#");
                PmPacket376DA da = new PmPacket376DA(obj.getMpSn());
                PmPacket376DT dt = new PmPacket376DT(1);

                //376规约组帧
                packet.getDataBuffer().putDA(da);
                packet.getDataBuffer().putDT(dt);
                packet.getDataBuffer().putBin(obj.getPort(), 1);//终端通信端口号
                packet.getDataBuffer().putBS8(obj.getSerialPortPara().toString());//透明转发通信控制字
                packet.getDataBuffer().put((byte) obj.getWaitforPacket());//透明转发接收等待报文超时时间
                packet.getDataBuffer().putBin(obj.getWaitforByte(), 1);//透明转发接收等待字节超时时间

                //645规约组帧
                if (null == obj.getMeterAddr()) {
                    continue;
                }
                Gb645MeterPacket pack = new Gb645MeterPacket(obj.getMeterAddr());
                byte funcode = (byte) obj.getFuncode();
                pack.setControlCode(true, false, false, funcode);
                byte[] DI = BcdUtils.reverseBytes(BcdUtils.stringToByteArray(commandItem.getIdentifier().substring(4, 8)));
                pack.getDataAsPmPacketData().put(DI);
                if((funcode == 0x1B)||(funcode==0x04))//控制、写数据
                {
                    if(obj.getMeterType()==101)//嘉兴规约的漏保需要加密码
                    {
                        byte[] PASS = {0,0,0,0};
                        pack.getDataAsPmPacketData().put(PASS);
                    }
                }   
                Map<String, ProtocolDataItem> DataItemMap_Config = config.getDataItemMap(commandItem.getIdentifier());
                Map<String, String> dataItemMap = commandItem.getDatacellParam();
                if (dataItemMap != null) {
                    Iterator iterator = DataItemMap_Config.keySet().iterator();
                    while (iterator.hasNext()) {
                        String DataItemCode = (String) iterator.next();
                        ProtocolDataItem dataItem = DataItemMap_Config.get(DataItemCode);
                        String DataItemValue = dataItem.getDefaultValue();
                        if ((dataItemMap != null) && (dataItemMap.containsKey(DataItemCode))) {
                            DataItemValue = dataItemMap.get(DataItemCode);
                        }
                        String Format = dataItem.getFormat();
                        String IsGroupEnd = dataItem.getIsGroupEnd();
                        int Length = dataItem.getLength();
                        int bitnumber = dataItem.getBitNumber();
                        this.FillDataBuffer(pack.getDataAsPmPacketData(), Format, DataItemValue, IsGroupEnd, Length, bitnumber);
                    }
                }
                // pack.getDataAsPmPacketData().rewind();
                packet.getDataBuffer().putBin(pack.getValue().length, 2);//透明转发内容字节数k
                packet.getDataBuffer().put(pack.getValue());

                packet.setAuthorize(new Authorize());
                packet.setTpv(new TimeProtectValue());//时间标签
                results.add(packet);
            }
            return results;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
    
     @Override
    public List<PmPacket376> EncodeList_Upgrade(String rtua,byte[] binFile)
    {
        List<PmPacket376> results = new ArrayList<PmPacket376>();
        int segmentNumber =0;
        int fileSize = binFile.length;
        int lastSegBytes = fileSize % UPGRADE_FILE_SEGMENT_SIZE;
        segmentNumber = (fileSize < UPGRADE_FILE_SEGMENT_SIZE)?1:(fileSize / UPGRADE_FILE_SEGMENT_SIZE);
        if(lastSegBytes > 0) {
            segmentNumber +=1;
        } 
        int filePos = 0;
        for(int i=0;i<segmentNumber;i++)
        {
            int thisSegmentLength = (i == segmentNumber-1)?lastSegBytes:UPGRADE_FILE_SEGMENT_SIZE;
            byte[] thisSegment = new byte[UPGRADE_FILE_SEGMENT_SIZE];
            if(i==segmentNumber-1)
            {
                initArray(thisSegment,(byte)0xFF);
            }
            System.arraycopy(binFile, filePos, thisSegment, 0, thisSegmentLength);
            
            PmPacket376 packet = new PmPacket376();
            preSetPacket(packet, AFNType.AFN_UPGRADE, rtua);
            PmPacket376DA da = new PmPacket376DA(0);//p0
            PmPacket376DT dt = new PmPacket376DT();
            dt.setFn(1);//F1,文件传输方式1
            packet.getDataBuffer().putDA(da);
            packet.getDataBuffer().putDT(dt);
            packet.getDataBuffer().putBin(1, 1);//文件标识
            if(i == segmentNumber-1) {
                packet.getDataBuffer().putBin(1, 1);
            }//文件属性
            else {
                packet.getDataBuffer().putBin(0, 1);
            }//文件属性
            packet.getDataBuffer().putBin(0, 1);//文件指令
            packet.getDataBuffer().putBin(segmentNumber, 2);//总段数n
            packet.getDataBuffer().putBin(i, 4);//第i段标识或偏移
            packet.getDataBuffer().putBin(thisSegmentLength, 2);//第i段数据长度Lf
            packet.getDataBuffer().put(thisSegment);//文件数据
            results.add(packet);
            filePos += thisSegmentLength;
        }
        
                        
        return results;
        
    }

    //--------------------private--------------------------------------
    
     private void initArray(byte[] objArray, byte value)
     {
         for(int i=0;i<objArray.length;i++) {
             objArray[i] = value;
         } 
     }

     
     private byte getFunCode(byte afn)
     {
         if((afn == AFNType.AFN_SETPARA)||(afn == AFNType.AFN_UPGRADE)||(afn == AFNType.AFN_CONTROL)) {
             return FUNCODE_DOWM_10;
         }
         else if((afn == AFNType.AFN_RESET)) {
             return FUNCODE_DOWM_1;
         }
         else {
             return FUNCODE_DOWM_11;
         }
     }

    /**
     * 装配报文对象
     * @param packet
     * @param AFN
     * @param Rtua
     */
    private void preSetPacket(PmPacket packet, byte AFN, String Rtua) {
        if (null != packet) {
            byte FunCode = getFunCode(AFN);
            packet.setAfn(AFN);//AFN
            packet.getAddress().setRtua(Rtua); //逻辑地址
            packet.getControlCode().setIsUpDirect(false);
            packet.getControlCode().setIsOrgniger(true);
            packet.getControlCode().setFunctionKey(FunCode);
            packet.getControlCode().setIsDownDirectFrameCountAvaliable(true);
            packet.getControlCode().setDownDirectFrameCount((byte) 0);
            packet.getSeq().setIsTpvAvalibe(true);
            if((AFN == AFNType.AFN_SETPARA)
                    ||(AFN == AFNType.AFN_CONTROL)
                    ||(AFN == AFNType.AFN_RESET)
                    ||(AFN == AFNType.AFN_UPGRADE))
            {
                packet.getSeq().setIsNeedCountersign(true);
            }

        }
    }

    /**
     * 判断是否循环类命令项，如：F10
     * @param commandItem
     * @return 
     */
    private Boolean NeedSubpackage(CommandItem commandItem) {
        Boolean result = (commandItem != null);
        result = result && (commandItem.getDatacellParam() != null);
        result = result && (commandItem.getCircleDataItems() != null);
        result = result && (commandItem.getCircleDataItems().getDataItemGroups() != null);
        result = result && ((commandItem.getDatacellParam().size() > 0)
                && (commandItem.getCircleDataItems().getDataItemGroups().size() > 0));
        return result;
    }

    /**
     * 按规约配置文件填充数据区
     * @param packet
     * @param commandItem
     */
    /*
    private void putDataBuf_readWithConfig(PmPacket376 packet, CommandItem commandItem) {
        String DataItemValue, Format, IsGroupEnd ;
        int Length, bitnumber;
        long TempCode = 0;
        List<ProtocolDataItem> DataItemList_Config = config.getDataItemList(commandItem.getIdentifier());
        Map<String, String> dataItemMap = commandItem.getDatacellParam();
        for (ProtocolDataItem dataItem : DataItemList_Config) {
            String DataItemCode = dataItem.getDataItemCode();
            DataItemValue = dataItem.getDefaultValue();
            if (DataItemValue.equals("YESTERDAY")) //抄上一天
            {
                DataItemValue = UtilsBp.getYeasterday();
            }
            if (dataItemMap != null) {
                if (dataItemMap.containsKey(DataItemCode)) {
                    DataItemValue = dataItemMap.get(DataItemCode);
                }
            }
            Format = dataItem.getFormat();
            Length = dataItem.getLength();
            IsGroupEnd = dataItem.getIsGroupEnd();
            bitnumber = dataItem.getBitNumber();
            FillDataBuffer(packet.getDataBuffer(), Format, DataItemValue, IsGroupEnd, Length, bitnumber);
        }
    }

    public void putDataBuf_withValue(PmPacket376 packet, CommandItem commandItem) {
        String DataItemValue, Format, IsGroupEnd ;
        int Length, bitnumber;
        long TempCode = 0;
        List<ProtocolDataItem> DataItemList_Config = config.getDataItemList(commandItem.getIdentifier());
        Map<String, String> dataItemMap = commandItem.getDatacellParam();
        if (dataItemMap != null) {
            for (ProtocolDataItem dataItem : DataItemList_Config) {
                String DataItemCode = dataItem.getDataItemCode();
                DataItemValue = dataItem.getDefaultValue();
                if (DataItemValue.equals("YESTERDAY")) //抄上一天
                {
                    DataItemValue = UtilsBp.getYeasterday();
                }

                if (dataItemMap.containsKey(DataItemCode)) {
                    DataItemValue = dataItemMap.get(DataItemCode);
                }
                Format = dataItem.getFormat();
                Length = dataItem.getLength();
                IsGroupEnd = dataItem.getIsGroupEnd();
                bitnumber = dataItem.getBitNumber();
                FillDataBuffer(packet.getDataBuffer(), Format, DataItemValue, IsGroupEnd, Length, bitnumber);
            }
        }
    }
*/
    /**
     * 按规约命令/数据项配置文件填充数据包buffer
     * @param packetdata：被填充的数据包
     * @param Format：数据项格式码
     * @param DataItemValue：数据项的值
     * @param IsGroupEnd：该项数据项是否属于BS8格式的最后一个
     * @param Length：数据项长度（字节）
     * @param bitnumber：针对BS8格式的位数
     */
    private void FillDataBuffer(PmPacketData packetdata, String Format, String DataItemValue, String IsGroupEnd, int Length, int bitnumber) {
        if (Format.equals("HEX")) {
            packetdata.putBin(BcdUtils.stringToByte(UtilsBp.lPad(DataItemValue, "0", 2)), Length);
        } else if (Format.equals("BIN")) {
            packetdata.putBin(Integer.parseInt(DataItemValue), Length);
        } else if (Format.equals("IPPORT")) {
            packetdata.putIPPORT(DataItemValue);
        } else if (Format.equals("IP")) {
            packetdata.putIP(DataItemValue);
        } else if (Format.equals("TEL")) {
            packetdata.putTEL(DataItemValue);
        } else if (Format.equals("BS8")) {
            packetdata.putBS8(DataItemValue);
        } else if (Format.equals("GROUP_BS8")) {
            groupValue += DataItemValue;
            // String IsGroupEnd = dataItem.getIsGroupEnd();
            if (IsGroupEnd.equals("1")) {
                packetdata.putBS8(UtilsBp.Reverse(groupValue));
                groupValue = "";
            }
        } else if (Format.equals("GROUP_BIN")) {
            groupBinValue += Integer.parseInt(DataItemValue) << (bits - bitnumber);
            bits -= bitnumber;
            if (IsGroupEnd.equals("1")) {
                packetdata.put((byte) groupBinValue);
                bits = 8;
                groupBinValue = 0;
            }
        } else if (Format.equals("BS16")) {
            packetdata.putBS16(DataItemValue);
        } else if (Format.equals("BS24")) {
            packetdata.putBS24(DataItemValue);
        } else if (Format.equals("BS64")) {
            packetdata.putBS64(DataItemValue);
        } else if (Format.equals("ASCII")) {
            packetdata.putAscii(DataItemValue, Length);
        } else if (Format.equals("A1")) {
            packetdata.putA1(new DataTypeA1(DataItemValue));
        } else if (Format.equals("A2")) {
            packetdata.putA2(new DataTypeA2(Double.parseDouble(DataItemValue)));
        } else if (Format.equals("A3")) {
            packetdata.putA3(new DataTypeA3(Long.parseLong(DataItemValue)));
        } else if (Format.equals("A4")) {
            packetdata.putA4(new DataTypeA4(Byte.parseByte(DataItemValue)));
        } else if (Format.equals("A5")) {
            packetdata.putA5(new DataTypeA5(Float.parseFloat(DataItemValue)));
        } else if (Format.equals("A6")) {
            packetdata.putA6(new DataTypeA6(Float.parseFloat(DataItemValue)));
        } else if (Format.equals("A7")) {
            packetdata.putA7(new DataTypeA7(Float.parseFloat(DataItemValue)));
        } else if (Format.equals("A8")) {
            packetdata.putA8(new DataTypeA8(Integer.parseInt(DataItemValue)));
        } else if (Format.equals("A9")) {
            packetdata.putA9(new DataTypeA9(Double.parseDouble(DataItemValue)));
        } else if (Format.equals("A10")) {
            packetdata.putA10(new DataTypeA10(Long.parseLong(DataItemValue)));
        } else if (Format.equals("A11")) {
            packetdata.putA11(new DataTypeA11(Double.parseDouble(DataItemValue)));
        } else if (Format.equals("A12")) {
            packetdata.putA12(new DataTypeA12(Long.parseLong(DataItemValue)));
        } else if (Format.equals("A13")) {
            packetdata.putA13(new DataTypeA13(Double.parseDouble(DataItemValue)));
        } else if (Format.equals("A14")) {
            packetdata.putA14(new DataTypeA14(Double.parseDouble(DataItemValue)));
        } else if (Format.equals("A15")) {
            packetdata.putA15(new DataTypeA15(DataItemValue, "yyyy-MM-dd HH:mm:ss"));
        } else if (Format.equals("A16")) {
            packetdata.putA16(new DataTypeA16(DataItemValue, "dd HH:mm:ss"));
        } else if (Format.equals("A17")) {
            packetdata.putA17(new DataTypeA17(DataItemValue, "MM-dd HH:mm"));
        } else if (Format.equals("A18")) {
            packetdata.putA18(new DataTypeA18(DataItemValue, "dd HH:mm"));
        } else if (Format.equals("A19")) {
            packetdata.putA19(new DataTypeA19(DataItemValue, "HH:mm"));
        } else if (Format.equals("A20")) {
            packetdata.putA20(new DataTypeA20(DataItemValue, "yyyy-MM-dd"));
        } else if (Format.equals("A21")) {
            packetdata.putA21(new DataTypeA21(DataItemValue, "yyyy-mm"));
        } else if (Format.equals("A22")) {
            packetdata.putA22(new DataTypeA22(Float.parseFloat(DataItemValue)));
        } else if (Format.equals("A23")) {
            packetdata.putA23(new DataTypeA23(Float.parseFloat(DataItemValue)));
        } else if (Format.equals("A24")) {
            packetdata.putA24(new DataTypeA24(DataItemValue, "dd HH"));
        } else if (Format.equals("A25")) {
            packetdata.putA25(new DataTypeA25(Double.parseDouble(DataItemValue)));
        } else if (Format.equals("A26")) {
            packetdata.putA26(new DataTypeA26(Float.parseFloat(DataItemValue)));
        } else if (Format.equals("A27")) {
            packetdata.putA27(new DataTypeA27(Long.parseLong(DataItemValue)));
        } else if (Format.equals("A29")) {
            packetdata.putBcdInt(Long.parseLong(DataItemValue), 1);
        } else if (Format.equals("DATE_LOUBAO")) {
            try {
                packetdata.put(UtilsBp.String2DateArray(DataItemValue, "yyyy-MM-dd HH:mm:ss"));
            } catch (ParseException ex) {
                log.error(ex.getMessage());
            }
        }else if (Format.equals("METER_ADDRESS")) {
            String temp;
            byte[] tempData;
            if (DataItemValue.length() > 12) {
                temp = DataItemValue.substring(DataItemValue.length() - 12);
            } else {
                temp = BcdUtils.dupeString("0", 12 - DataItemValue.length()) + DataItemValue;
            }
            tempData = BcdUtils.reverseBytes(BcdUtils.stringToByteArray(temp));
            packetdata.put(tempData);
        }

    }
    
    private int getDataItemGroupLength(CommandItem commandItem) {
        long TempCode = 0;
        int Len = 0;
        Map<String, ProtocolDataItem> DataItemMap_Config = this.getConfig().getDataItemMap(commandItem.getIdentifier());
        Map<String, String> dataItemMap = commandItem.getDatacellParam();
        Iterator iterator = dataItemMap.keySet().iterator();
        while (iterator.hasNext()) {
            String DataItemCode = (String) iterator.next();
            ProtocolDataItem dataItem = DataItemMap_Config.get(DataItemCode);
            Len += dataItem.getLength();
        } 
                    
        List<DataItemGroup> groups = commandItem.getCircleDataItems().getDataItemGroups();
        if (groups.size() > 0) {
            DataItemGroup group = groups.get(0);
            

            List<DataItem> dataItemList_group = group.getDataItemList();
            for (DataItem dataItem : dataItemList_group) {
                String DataItemCode = dataItem.getDataItemCode();
                if ((Long.valueOf(DataItemCode) - TempCode > 10000) && (TempCode != 0)) {
                    ProtocolDataItem TempdataItem = DataItemMap_Config.get(String.valueOf(TempCode + 10000).substring(0, 10));
                    Len += TempdataItem.getLength();
                }
                ProtocolDataItem protocoldataItem = DataItemMap_Config.get(DataItemCode.substring(0, 10));

                Len += protocoldataItem.getLength();
                TempCode = Long.valueOf(DataItemCode);
            }
            return Len;
        } else {
            return 0;
        }
    }
    

    private void putDataBuf_withValue(PmPacket376 packet, CommandItem commandItem) {
        String DataItemValue, Format, IsGroupEnd;
        int Length, bitnumber;
        long TempCode = 0;
        List<ProtocolDataItem> DataItemList_Config = this.config.getDataItemList(commandItem.getIdentifier());
        Map<String, String> dataItemMap = commandItem.getDatacellParam();

        for (ProtocolDataItem dataItem : DataItemList_Config) {
            String DataItemCode = dataItem.getDataItemCode();
            DataItemValue = dataItem.getDefaultValue();
            if (DataItemValue.equals("YESTERDAY")) //抄上一天
            {
                DataItemValue = UtilsBp.getYeasterday();
            }
            if (dataItemMap != null) {
                if (dataItemMap.containsKey(DataItemCode)) {
                    DataItemValue = dataItemMap.get(DataItemCode);
                }
            }
            Format = dataItem.getFormat();
            Length = dataItem.getLength();
            IsGroupEnd = dataItem.getIsGroupEnd();
            bitnumber = dataItem.getBitNumber();
            FillDataBuffer(packet.getDataBuffer(), Format, DataItemValue, IsGroupEnd, Length, bitnumber);
        }
    }

    /**
     *  带分包处理的转换函数，将命令对象解析成报文包列表
     * @param commandItem
     * @param AFN
     * @param Rtua
     * @param MpSn
     * @param PacketLen
     * @param results 
     */
    private void cmdItem2PacketList_Subpacket(CommandItem commandItem, byte AFN, String Rtua, int MpSn, int PacketLen, List<PmPacket376> results) {
        int Index = 1;
        int packetNo = 1;
        long TempCode = 0;
        boolean CanPacket = false;
        int groupNumber = PacketLen / getDataItemGroupLength(commandItem);  //理论每一帧下发的参数组数
        int ActualgroupNumber;//每次实际下发的参数组数
        String DataItemValue ;
        String Format;
        String IsGroupEnd;
        int Length ;
        int bitnumber;
        ProtocolDataItem dataItem = null;

        Map<String, ProtocolDataItem> DataItemMap_Config = config.getDataItemMap(commandItem.getIdentifier());
        Map<String, String> dataItemMap = commandItem.getDatacellParam();

        PmPacket376 packet = null;
        CircleDataItems circleDIs = commandItem.getCircleDataItems();
        if (circleDIs != null) {
            List<DataItemGroup> groups = circleDIs.getDataItemGroups();
            int WaitForSendPacketNum = groups.size();
            int fn = Integer.parseInt(commandItem.getIdentifier().substring(4, 8));//10+03+0002(protocolcode+afn+fn)
            for (DataItemGroup group : groups) {
                if (WaitForSendPacketNum >= groupNumber) {
                    ActualgroupNumber = groupNumber;
                } else {
                    ActualgroupNumber = WaitForSendPacketNum;
                }

                //生成一个报文
                if ((ActualgroupNumber!=0)&((Index - 1) % ActualgroupNumber == 0) && (!CanPacket)) {
                    packet = new PmPacket376();
                    packet.setCommandRemark(commandItem.getIdentifier());//设置命令项标志
                    packet.setMpSnRemark(String.valueOf(MpSn));//设置测量点标志
                    preSetPacket(packet, AFN, Rtua);
                    PmPacket376DA da = new PmPacket376DA(MpSn);
                    PmPacket376DT dt = new PmPacket376DT(fn);
                    packet.getDataBuffer().putDA(da);
                    packet.getDataBuffer().putDT(dt);
                    Iterator iterator = dataItemMap.keySet().iterator();
                    while (iterator.hasNext()) {
                        String DataItemCode = (String) iterator.next();
                        dataItem = DataItemMap_Config.get(DataItemCode);
                        DataItemValue = dataItemMap.get(DataItemCode);
                        FillDataBuffer(packet.getDataBuffer(), dataItem.getFormat(),String.valueOf(DataItemValue), dataItem.getIsGroupEnd(), dataItem.getLength(), dataItem.getBitNumber());
                    }           
                }

                List<DataItem> dataItemList = group.getDataItemList();
                for (DataItem dataItemTemp : dataItemList) {
                    CanPacket = true;
                    String DataItemCode = dataItemTemp.getDataItemCode();
                    if ((Long.valueOf(DataItemCode) - TempCode > 10000) && (TempCode != 0)) {
                        ProtocolDataItem TempdataItem = DataItemMap_Config.get(String.valueOf(TempCode + 10000).substring(0, 10));
                        DataItemValue = TempdataItem.getDefaultValue();
                        Format = TempdataItem.getFormat();
                        Length = TempdataItem.getLength();
                        IsGroupEnd = TempdataItem.getIsGroupEnd();
                        bitnumber = TempdataItem.getBitNumber();
                        FillDataBuffer(packet.getDataBuffer(), Format, DataItemValue, IsGroupEnd, Length, bitnumber);
                    }
                    ProtocolDataItem protocoldataItem = DataItemMap_Config.get(DataItemCode.substring(0, 10));
                    DataItemValue = dataItemTemp.getDataItemValue();
                    Format = protocoldataItem.getFormat();
                    Length = protocoldataItem.getLength();
                    IsGroupEnd = protocoldataItem.getIsGroupEnd();
                    bitnumber = protocoldataItem.getBitNumber();
                    FillDataBuffer(packet.getDataBuffer(), Format, DataItemValue, IsGroupEnd, Length, bitnumber);
                    TempCode = Long.valueOf(DataItemCode);
                }
                if ((Index % ActualgroupNumber == 0) && (CanPacket)) {
                    Index = 0;
                    if (AFN == AFNType.AFN_RESET || AFN == AFNType.AFN_SETPARA || AFN == AFNType.AFN_TRANSMIT)//消息认证码字段PW
                    {
                        packet.setAuthorize(new Authorize());
                    }
                    packet.setTpv(new TimeProtectValue());//时间标签
                    if (groups.size() == 1) {//单帧
                        packet.getSeq().setIsTpvAvalibe(true);
                        packet.getSeq().setIsFinishFrame(true);
                        packet.getSeq().setIsFirstFrame(true);
                        packet.getSeq().setSeq((byte) 0);
                    } else if (packetNo == 1)//起始帧
                    {
                        packet.getSeq().setIsTpvAvalibe(true);
                        packet.getSeq().setIsFinishFrame(false);
                        packet.getSeq().setIsFirstFrame(true);
                        packet.getSeq().setSeq((byte) packetNo);
                    } else if ((packetNo > 1) && (Index < groups.size()))//中间帧
                    {
                        packet.getSeq().setIsTpvAvalibe(true);
                        packet.getSeq().setIsFinishFrame(false);
                        packet.getSeq().setIsFirstFrame(false);
                        packet.getSeq().setSeq((byte) packetNo);
                    } else if (Index == groups.size())//结束帧
                    {
                        packet.getSeq().setIsTpvAvalibe(true);
                        packet.getSeq().setIsFinishFrame(true);
                        packet.getSeq().setIsFirstFrame(false);
                        packet.getSeq().setSeq((byte) packetNo);
                    }
                    results.add(packet);
                    CanPacket = false;
                    WaitForSendPacketNum -= ActualgroupNumber;
                    packetNo++;
                }
                Index++;
            }
        }
    }
    
    //读一类数据组帧
    private List<PmPacket376> Encode_ReadData1(String LogicalAddr,int[] MpSnList,List<CommandItem> CommandItems,String termProtocol)
    {
        if(termProtocol.equals(TermProtocol.TERM_GW_376_01)) {
            return Encode_Normal_OneCmdItem(LogicalAddr,AFNType.AFN_READDATA1, MpSnList,CommandItems,false,true,false);
        }
        else if (termProtocol.equals(TermProtocol.TERM_GW_376_02)) {
            return Encode_Normal(LogicalAddr,AFNType.AFN_READDATA1, MpSnList,CommandItems,false,true,false);
        }
        else {
            return null;
        }
    }
    
    //读二类数据组帧
    private List<PmPacket376> Encode_ReadData2(String LogicalAddr,int[] MpSnList,List<CommandItem> CommandItems,String termProtocol)
    {
        if(termProtocol.equals(TermProtocol.TERM_GW_376_01)) {
            return Encode_Normal_OneCmdItem(LogicalAddr,AFNType.AFN_READDATA2, MpSnList,CommandItems,false,true,true);
        }
        else if (termProtocol.equals(TermProtocol.TERM_GW_376_02)) {
            return Encode_Normal(LogicalAddr,AFNType.AFN_READDATA2, MpSnList,CommandItems,false,true,true);
        }
        else {
            return null;
        }   
    }
    
    //读参数组帧
    private List<PmPacket376> Encode_ReadPara(String LogicalAddr,int[] MpSnList,List<CommandItem> CommandItems,String termProtocol)
    {
        if(termProtocol.equals(TermProtocol.TERM_GW_376_01)) {
            return Encode_Normal_OneCmdItem(LogicalAddr,AFNType.AFN_GETPARA, MpSnList,CommandItems,false,true,false);
            
        }
        else if (termProtocol.equals(TermProtocol.TERM_GW_376_02)) {
            return Encode_Normal(LogicalAddr,AFNType.AFN_GETPARA, MpSnList,CommandItems,false,true,false);
        }
        else {
            return null;
        }
        
    }
    
    //设置参数组帧
    private List<PmPacket376> Encode_SetPara(String LogicalAddr,int[] MpSnList,List<CommandItem> CommandItems,String termProtocol)
    {
        if(termProtocol.equals(TermProtocol.TERM_GW_376_01)) {
            return Encode_Normal_OneCmdItem(LogicalAddr,AFNType.AFN_SETPARA, MpSnList,CommandItems,true,true,true);        
        }
        else if (termProtocol.equals(TermProtocol.TERM_GW_376_02)) {
            return Encode_Normal(LogicalAddr,AFNType.AFN_SETPARA, MpSnList,CommandItems,true,true,true);
        }
        else {
            return null;
        }      
    }
    
    //设置参数组帧
    private List<PmPacket376> Encode_Transmit(String LogicalAddr,int[] MpSnList,List<CommandItem> CommandItems,String termProtocol)
    { 
        if(termProtocol.equals(TermProtocol.TERM_GW_376_01)) {  
            return Encode_Normal_OneCmdItem(LogicalAddr,AFNType.AFN_TRANSMIT, MpSnList,CommandItems,true,true,true);
        }
        else if (termProtocol.equals(TermProtocol.TERM_GW_376_02)) {
            return Encode_Normal(LogicalAddr,AFNType.AFN_TRANSMIT, MpSnList,CommandItems,true,true,true);
        }
        else {
            return null;
        } 
    }
    
    //复位组帧
    private List<PmPacket376> Encode_Reset(String LogicalAddr,int[] MpSnList,List<CommandItem> CommandItems,String termProtocol)
    { 
        if(termProtocol.equals(TermProtocol.TERM_GW_376_01)) {  
            return Encode_Normal_OneCmdItem(LogicalAddr,AFNType.AFN_TRANSMIT, MpSnList,CommandItems,true,true,false);
        }
        else if (termProtocol.equals(TermProtocol.TERM_GW_376_02)) {
           return Encode_Normal(LogicalAddr,AFNType.AFN_TRANSMIT, MpSnList,CommandItems,true,true,false);
        }
        else {
            return null;
        }
    }


    /**
     * 
     * @param LogicalAddr:终端逻辑地址
     * @param AFN：功能码
     * @param MpSnList ： 测量点列表
     * @param CommandItems：命令像列表
     * @param PW：是否含消息认证码字段PW
     * @param Tp：是否含时间标签
     * @param FillValue：是否填充CommandItem内数据项的值（如：参数设置）
     * @return 
     */
    private List<PmPacket376> Encode_Normal(String LogicalAddr,byte AFN,int[] MpSnList,List<CommandItem> CommandItems,boolean PW,boolean Tp,boolean FillValue)
    {
        try {
            StringBuilder commandMark = new StringBuilder();
            StringBuilder gpMark = new StringBuilder();
            List<PmPacket376> packetList = new ArrayList<PmPacket376>();
            PmPacket376 packet = new PmPacket376();
            preSetPacket(packet, AFN, LogicalAddr);

            for (int i = 0; i <= MpSnList.length - 1; i++) {
                gpMark.append(MpSnList[i]).append("#");
                for (CommandItem commandItem : CommandItems) {
                    commandMark.append(commandItem.getIdentifier()).append("#");
                    PmPacket376DA da = new PmPacket376DA(MpSnList[i]);
                    PmPacket376DT dt = new PmPacket376DT();
                    int fn = Integer.parseInt(commandItem.getIdentifier().substring(4, 8));//10+03+0002(protocolcode+afn+fn)
                    dt.setFn(fn);
                    packet.getDataBuffer().putDA(da);
                    packet.getDataBuffer().putDT(dt);
                    if (FillValue) {
                        putDataBuf_withValue((PmPacket376) packet,commandItem);
                    }
                }
            }
            
            if(PW){
                packet.setAuthorize(new Authorize());
            }
            if(Tp) {
                packet.setTpv(new TimeProtectValue());
            }//时间标签
            packet.setCommandRemark(commandMark.toString());
            packet.setMpSnRemark(gpMark.toString());
            packetList.add(packet);
            return packetList;
        } catch (NumberFormatException numberFormatException) {
            log.error(numberFormatException.getMessage());
            return null;
        }
    }

/**
     *  基于独立命令项的组帧方法
     * @param LogicalAddr:终端逻辑地址
     * @param AFN：功能码
     * @param MpSnList ： 测量点列表
     * @param CommandItems：命令像列表
     * @param PW：是否含消息认证码字段PW
     * @param Tp：是否含时间标签
     * @param FillValue：是否填充CommandItem内数据项的值（如：参数设置）
     * @return 
     */
    private List<PmPacket376> Encode_Normal_OneCmdItem(String LogicalAddr,byte AFN,int[] MpSnList,List<CommandItem> CommandItems,boolean PW,boolean Tp,boolean FillValue)
    {
        try {
            StringBuilder commandMark = new StringBuilder();
            StringBuilder gpMark = new StringBuilder();
            List<PmPacket376> packetList = new ArrayList<PmPacket376>();
            for (int i = 0; i <= MpSnList.length - 1; i++) {
                gpMark.delete(0, gpMark.length());
                gpMark.append(MpSnList[i]).append("#");
                for (CommandItem commandItem : CommandItems) {
                    if (NeedSubpackage(commandItem)) //针对类似F10的参数，按每帧最大长度进行自动分包处理
                    {
                        int DataBuffLen = MAX_PACKET_LEN - 16 - 22;
                        cmdItem2PacketList_Subpacket(commandItem, AFN, LogicalAddr, MpSnList[i], DataBuffLen, packetList);
                    }
                    else
                    {
                        commandMark.delete(0, commandMark.length());
                        commandMark.append(commandItem.getIdentifier()).append("#");
                        PmPacket376 packet = new PmPacket376();
                        preSetPacket(packet, AFN, LogicalAddr);
                        PmPacket376DA da = new PmPacket376DA(MpSnList[i]);
                        PmPacket376DT dt = new PmPacket376DT();
                        int fn = Integer.parseInt(commandItem.getIdentifier().substring(4, 8));//10+03+0002(protocolcode+afn+fn)
                        dt.setFn(fn);
                        packet.getDataBuffer().putDA(da);
                        packet.getDataBuffer().putDT(dt);
                        if (FillValue) {
                            putDataBuf_withValue((PmPacket376) packet,commandItem);
                        }
                        if(PW){
                            packet.setAuthorize(new Authorize());
                        }
                        if(Tp) {
                            packet.setTpv(new TimeProtectValue());
                        }//时间标签
                        packet.setMpSnRemark(gpMark.toString());
                        packet.setCommandRemark(commandMark.toString());
                        packetList.add(packet);
                    }
                }
            }
            return packetList;
        } catch (NumberFormatException numberFormatException) {
            log.error(numberFormatException.getMessage());
            return null;
        }
    }

    
    public List<PmPacket376> EncodeList_Normal(CollectObject obj, byte AFN) {
        List<PmPacket376> results = new ArrayList<PmPacket376>();
        PmPacket376 packet = new PmPacket376();
        int Index = 1;
        int[] MpSn = obj.getMpSn();
        int DataBuffLen = MAX_PACKET_LEN - 16 - 22;//[68+L+L+68+C+A+AFN+SEQ+TP+PW+CS+16]
        int CmdItemNum = obj.getCommandItems().size();
        StringBuilder gpMark = new StringBuilder();
        StringBuilder commandMark = new StringBuilder();
        for (int i = 0; i <= MpSn.length - 1; i++) {
            gpMark.delete(0, gpMark.length());
            gpMark.append(String.valueOf(MpSn[i])).append("#");
            List<CommandItem> CommandItems = obj.getCommandItems();
            for (CommandItem commandItem : CommandItems) {

                if (NeedSubpackage(commandItem)) //针对类似F10的参数，按每帧最大长度进行自动分包处理
                {
                    cmdItem2PacketList_Subpacket(commandItem, AFN, obj.getLogicalAddr(), MpSn[i], DataBuffLen, results);
                }
                else {
                    if ((Index - 1) % CmdItemNum == 0) {
                        packet = new PmPacket376();
                        preSetPacket(packet, AFN, obj.getLogicalAddr());
                    }
                    commandMark.append(commandItem.getIdentifier()).append("#");
                    PmPacket376DA da = new PmPacket376DA(MpSn[i]);
                    PmPacket376DT dt = new PmPacket376DT();
                    int fn = Integer.parseInt(commandItem.getIdentifier().substring(4, 8));//10+03+0002(protocolcode+afn+fn)
                    dt.setFn(fn);
                    packet.getDataBuffer().putDA(da);
                    packet.getDataBuffer().putDT(dt);
                    if ((AFN == AFNType.AFN_SETPARA)||(AFN == AFNType.AFN_READDATA2)) {
                        putDataBuf_withValue(packet, commandItem);
                    }
                    if (Index % CmdItemNum == 0) {
                        if (AFN == AFNType.AFN_RESET || AFN == AFNType.AFN_SETPARA || AFN == AFNType.AFN_TRANSMIT)//消息认证码字段PW
                        {
                            packet.setAuthorize(new Authorize());
                        }
                        packet.setTpv(new TimeProtectValue());//时间标签
                        packet.setCommandRemark(commandMark.toString());
                        packet.setMpSnRemark(gpMark.toString());
                        results.add(packet);
                        commandMark.delete(0, commandMark.length());
                    }
                }
                Index++;
            }
        }
        return results;
    }
}
