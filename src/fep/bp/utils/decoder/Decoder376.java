/*
 * 376规约解码器
 */
package fep.bp.utils.decoder;

import fep.bp.model.Dto;
import fep.bp.model.Dto.DtoItem;
import fep.bp.realinterface.conf.ProtocolDataItem;
import fep.bp.utils.UtilsBp;
import fep.codec.protocol.gb.PmPacketData;
import fep.codec.protocol.gb.gb376.PmPacket376;
import fep.codec.protocol.gb.gb376.PmPacket376DA;
import fep.codec.protocol.gb.gb376.PmPacket376DT;
import fep.codec.utils.BcdUtils;
import fep.meter645.Gb645MeterPacket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thinkpad
 */
public abstract class Decoder376 extends Decoder {

    private int groupBinValue = 0;
    private byte bits = 8;
    private final static Logger log = LoggerFactory.getLogger(Decoder376.class);

    @Override
    public Map<String, Map<String, String>> decode2Map(Object pack) {
        Map<String, Map<String, String>> results = new TreeMap<String, Map<String, String>>();
        PmPacket376 packet = (PmPacket376) pack;
        PmPacketData dataBuffer = getDataBuffer(packet);
        while (dataBuffer.HaveDate()) {
            PmPacket376DA da = getDA(dataBuffer);
            PmPacket376DT dt = getDt(dataBuffer);
            byte afn = packet.getAfn();
            if (afn > 0) {
                if (afn == 0x0A) {
                    afn = 0x04;
                }
                String commandItemCode = "10" + BcdUtils.byteToString(afn) + String.format("%04d", dt.getFn());
                String key = packet.getAddress().getRtua() + "#" + String.valueOf(da.getPn()) + "#" + commandItemCode;
                Map<String, String> dataItems = this.dataBuffer2Map(commandItemCode, dataBuffer);
                results.put(key, dataItems);
            }
        }
        return results;
    }


    @Override
    public void decode2dto(Object pack, Dto dto) {
        try {
            PmPacket376 packet = (PmPacket376) pack;
            PmPacketData dataBuffer = getDataBuffer(packet);
            dto.setLogicAddress(packet.getAddress().getRtua());
            while (dataBuffer.HaveDate()) {
                PmPacket376DA da = getDA(dataBuffer);
                PmPacket376DT dt = getDt(dataBuffer);
                byte afn = packet.getAfn();
                int gp = da.getPn();
                dto.setAfn(afn);
                String commandItemCode = "10" + BcdUtils.byteToString(afn) + String.format("%04d", dt.getFn());
                DtoItem dtoItem = dto.addDataItem(gp, UtilsBp.getNow(), commandItemCode);
                dtoItem.dataMap = this.dataBuffer2Map(commandItemCode, dataBuffer);
            }
        } catch (Exception e) {
            log.error("错误信息：", e.fillInStackTrace());
        }
    }

      

    public abstract void decode2dto_TransMit(Object pack, Dto dto) ;

    public abstract Map<String, Map<String, String>> decode2Map_TransMit_WriteBack(Object pack);

    public abstract Map<String, Map<String, String>> decode2Map_TransMit(Object pack); 

    /**
     * 根据配置文件数据项格式定义，抽取databuffer内容，填充到dataItem列表中
     * @param DataItemList_Config  配置文件定义对应命令项的数据项列表
     * @param dataItems 返回结果的数据项Map
     * @param dataBuffer 数据抽取的dataBuffer
     */
    private void fillDataItem(List<ProtocolDataItem> DataItemList_Config, Map<String, String> dataItems, PmPacketData dataBuffer) {
         try {
        String GroupValue = "";
        for (ProtocolDataItem dataItem : DataItemList_Config) {
            String DataItemCode = dataItem.getDataItemCode();
            int Len = dataItem.getLength();
            String Format = dataItem.getFormat();
            int bitnumber = dataItem.getBitNumber();
            String IsGroupEnd = dataItem.getIsGroupEnd();
            if (Format.equals("BIN")) {
                dataItems.put(DataItemCode, String.valueOf(dataBuffer.getBin(Len)));
            } else if (Format.equals("IPPORT")) {
                dataItems.put(DataItemCode, dataBuffer.getIPPORT());
            } else if (Format.equals("IP")) {
                dataItems.put(DataItemCode, dataBuffer.getIP());
            } else if (Format.equals("TEL")) {
                dataItems.put(DataItemCode, dataBuffer.getTEL());
            } else if (Format.equals("BS8")) {
                dataItems.put(DataItemCode, dataBuffer.getBS8());
            } else if (Format.equals("BS16")) {
                dataItems.put(DataItemCode, dataBuffer.getBS16());
            } else if (Format.equals("GROUP_BS8")) {
                if (GroupValue.length() == 0) {
                    GroupValue = UtilsBp.Reverse(dataBuffer.getBS8());
                }
                dataItems.put(DataItemCode, GroupValue.substring(0, bitnumber));
                GroupValue = GroupValue.substring(bitnumber, GroupValue.length());
                if (IsGroupEnd.equals("1")) {
                    GroupValue = "";
                }
            } else if (Format.equals("GROUP_BIN")) {
                if (groupBinValue == 0) {
                    groupBinValue = dataBuffer.get();
                }
                if (groupBinValue < 0) {
                    groupBinValue = groupBinValue + 256;
                }
                byte resultValue = (byte) (groupBinValue >> (bits - bitnumber));
                groupBinValue = groupBinValue - (resultValue << (bits - bitnumber));
                bits -= bitnumber;
                dataItems.put(DataItemCode, String.valueOf(resultValue));
                if (IsGroupEnd.equals("1")) {
                    groupBinValue = 0;
                    bits = 8;
                }
            } else if (Format.equals("BS24")) {
                dataItems.put(DataItemCode, String.valueOf(dataBuffer.getBS24()));
            } else if (Format.equals("BS64")) {
                dataItems.put(DataItemCode, String.valueOf(dataBuffer.getBS64()));
            } else if (Format.equals("ASCII")) {
                dataItems.put(DataItemCode, String.valueOf(dataBuffer.getAscii(Len)));
            } else if (Format.equals("A1")) {
                dataItems.put(DataItemCode, dataBuffer.getA1().toString());
            } else if (Format.equals("A2")) {
                dataItems.put(DataItemCode, String.valueOf(dataBuffer.getA2().getValue()));
            } else if (Format.equals("A3")) {
                dataItems.put(DataItemCode, String.valueOf(dataBuffer.getA3().getValue()));
            } else if (Format.equals("A4")) {
                dataItems.put(DataItemCode, String.valueOf(dataBuffer.getA4().getValue()));
            } else if (Format.equals("A5")) {
                dataItems.put(DataItemCode, String.valueOf(dataBuffer.getA5().getValue()));
            } else if (Format.equals("A6")) {
                dataItems.put(DataItemCode, String.valueOf(dataBuffer.getA6().getValue()));
            } else if (Format.equals("A7")) {
                dataItems.put(DataItemCode, String.valueOf(dataBuffer.getA7().getValue()));
            } else if (Format.equals("A8")) {
                dataItems.put(DataItemCode, dataBuffer.getA8().toString());
            } else if (Format.equals("A9")) {
                dataItems.put(DataItemCode, String.format("%.4f", dataBuffer.getA9().getValue()));
            } else if (Format.equals("A10")) {
                dataItems.put(DataItemCode, String.valueOf(dataBuffer.getA10().getValue()));
            } else if (Format.equals("A11")) {
                double value = dataBuffer.getA11().getValue();
                if( value >=0) {
                    dataItems.put(DataItemCode, String.valueOf(value));
                }
            } else if (Format.equals("A12")) {
                long value = dataBuffer.getA12().getValue();
                if(value>=0) {
                    dataItems.put(DataItemCode, String.valueOf(value));
                }
            } else if (Format.equals("A13")) {
                dataItems.put(DataItemCode, String.valueOf(dataBuffer.getA13().getValue()));
            } else if (Format.equals("A14")) {
                double value = dataBuffer.getA14().getValue();
                if( value >= 0) {
                    dataItems.put(DataItemCode, String.valueOf(value));
                }
                else{
                    dataItems.put(DataItemCode, "");
                }
            } else if (Format.equals("A15")) {
                dataItems.put(DataItemCode, dataBuffer.getA15().toString());
            } else if (Format.equals("A16")) {
                dataItems.put(DataItemCode, dataBuffer.getA16().toString());
            } else if (Format.equals("A17")) {
                dataItems.put(DataItemCode, dataBuffer.getA17().toString());
            } else if (Format.equals("A18")) {
                dataItems.put(DataItemCode, dataBuffer.getA18().toString());
            } else if (Format.equals("A19")) {
                dataItems.put(DataItemCode, dataBuffer.getA19().toString());
            } else if (Format.equals("A20")) {
                dataItems.put(DataItemCode, dataBuffer.getA20().toString());
            } else if (Format.equals("A21")) {
                dataItems.put(DataItemCode, dataBuffer.getA21().toString());
            } else if (Format.equals("A22")) {
                float value = dataBuffer.getA22().getValue();
                if(value>=0) {
                    dataItems.put(DataItemCode, String.valueOf(value));
                }
            } else if (Format.equals("A23")) {
                float value = dataBuffer.getA23().getValue();
                if(value>=0) {
                    dataItems.put(DataItemCode, String.valueOf(value));
                }
            } else if (Format.equals("A24")) {
                dataItems.put(DataItemCode, dataBuffer.getA24().toString());
            } else if (Format.equals("A25")) {
                dataItems.put(DataItemCode, String.valueOf(dataBuffer.getA25().getValue()));
            } else if (Format.equals("A26")) {
                float value = dataBuffer.getA26().getValue();
                if(value>=0) {
                    dataItems.put(DataItemCode, String.valueOf(value));
                }
            } else if (Format.equals("A27")) {
                long value = dataBuffer.getA27().getValue();
                if(value >=0) {
                    dataItems.put(DataItemCode, String.valueOf(value));
                }
            } else if (Format.equals("A29")) {
                dataItems.put(DataItemCode, String.valueOf(dataBuffer.getBcdInt(1)));
            } else if (Format.equals("DATE_LOUBAO")) {
                dataItems.put(DataItemCode, UtilsBp.DateArray2String(dataBuffer.getBytes(7)));
            }
        }
        } catch (Exception e) {
            log.error("错误信息：", e.fillInStackTrace());
        }
    }
    
    public  Gb645MeterPacket getGb645MeterPacket(PmPacket376 packet) throws Exception
    {
        PmPacketData dataBuffer = getDataBuffer(packet);
        PmPacket376DA da = getDA(dataBuffer);
        PmPacket376DT dt = getDt(dataBuffer);
        byte afn = packet.getAfn();
        Gb645MeterPacket packet645 = getInnerDataPacket(dataBuffer);//抽取内部包
        return packet645;
    }



    /*=======================================protected =====================================================
    /**
     *根据命令项编码从dataBuffer中将数据项解析到数据项Map中
     * @param commandItemCode:命令项编码
     * @param dataItems：数据项Map
     * @param dataBuffer：接收到数据的buffer
     */
    protected Map<String, String> dataBuffer2Map(String commandItemCode, PmPacketData dataBuffer) {
        try {

            Map<String, String> dataItems = new TreeMap<String, String>();
            List<ProtocolDataItem> DataItemList_Config = config.getDataItemList(commandItemCode);
            fillDataItem(DataItemList_Config, dataItems, dataBuffer);
            return dataItems;
        } catch (Exception e) {
            log.error("错误信息：", e.fillInStackTrace());
            return null;
        }
    }

    protected class InnerDataBuffer{
        private String Key;
        private PmPacketData InnerPacketData;

        public String getKey() {
            return Key;
        }

        public void setKey(String Key) {
            this.Key = Key;
        }

        public PmPacketData getInnerPacketData() {
            return InnerPacketData;
        }

        public void setInnerPacketData(PmPacketData InnerPacketData) {
            this.InnerPacketData = InnerPacketData;
        }
    }

    protected Gb645MeterPacket getInnerDataPacket(PmPacketData dataBuffer) throws Exception {

        try {
            long port = dataBuffer.getBin(1);//终端通信端口号
            long len = dataBuffer.getBin(2);//透明转发内容字节数k
            byte[] databuff_Inner = new byte[(int) len];
            dataBuffer.getRowIoBuffer().get(databuff_Inner);
            int head = Gb645MeterPacket.getMsgHeadOffset(databuff_Inner, 0);
            Gb645MeterPacket packet645 = Gb645MeterPacket.getPacket(databuff_Inner, head);
            return packet645;
        } catch (Exception e) {
            throw e;
        }
    }

    protected InnerDataBuffer getDataBuffer645(PmPacket376 packet) throws Exception {
        Map<String,PmPacketData> results = new TreeMap<String,PmPacketData>();
        PmPacketData dataBuffer = getDataBuffer(packet);
        PmPacket376DA da = getDA(dataBuffer);
        PmPacket376DT dt = getDt(dataBuffer);
        byte afn = packet.getAfn();
        Gb645MeterPacket packet645 = getInnerDataPacket(dataBuffer);//抽取内部包
        String MeterAddress = packet645.getAddress().getAddress();
        if(!packet645.getControlCode().isYichang())
        {
            PmPacketData dataBuffer645 =  packet645.getDataAsPmPacketData();
            String commandItemCode = getCommandItemCode(dataBuffer645);
            String key = packet.getAddress().getRtua() + "#" + MeterAddress + "#" + commandItemCode;
            InnerDataBuffer InnerData = new InnerDataBuffer();
            InnerData.setKey(key);
            InnerData.setInnerPacketData(dataBuffer645);
            return InnerData;
        }
        else {
            return null;
        }
    }

    protected PmPacketData getDataBuffer(PmPacket376 packet) {
        PmPacketData dataBuffer = packet.getDataBuffer();
        dataBuffer.rewind();
        return dataBuffer;
    }

    protected PmPacket376DT getDt(PmPacketData dataBuffer) {
        PmPacket376DT dt = new PmPacket376DT();
        dataBuffer.getDT(dt);
        return dt;
    }

    protected PmPacket376DA getDA(PmPacketData dataBuffer) {
        PmPacket376DA da = new PmPacket376DA();
        dataBuffer.getDA(da);
        return da;
    }

    protected String getCommandItemCode(PmPacketData dataBuffer645) {
        byte[] commandItem = new byte[2];
        dataBuffer645.getRowIoBuffer().rewind();
        dataBuffer645.getRowIoBuffer().get(commandItem);
        String commandItemCode = "8000" + BcdUtils.binArrayToString(BcdUtils.reverseBytes(commandItem));
        return commandItemCode;
    }
    
    public Map<String, String> decodeTransMitBack(Object pack,int successCode,int failedCode,String[] GpArray,String[] CommandArray) {
        Map<String, String> results = new HashMap<String, String>();
        PmPacket376 packet = (PmPacket376)pack;
        String logicAddress = packet.getAddress().getRtua();
        PmPacketData dataBuf = packet.getDataBuffer();
        dataBuf.rewind();
        PmPacket376DA da = new PmPacket376DA();
        dataBuf.getDA(da);
        PmPacket376DT dt = new PmPacket376DT();
        dataBuf.getDT(dt);
        
        byte afn = packet.getAfn();
        if (afn == 0X10) {  //透明转发，特殊处理
            long port = dataBuf.getBin(1);//终端通信端口号
            long len = dataBuf.getBin(2);//透明转发内容字节数k
            if(len == 0) {
                return results;
            }
            byte[] databuff = new byte[(int) len];
            dataBuf.getRowIoBuffer().get(databuff);
            int head = Gb645MeterPacket.getMsgHeadOffset(databuff, 0);
            Gb645MeterPacket packet645 = Gb645MeterPacket.getPacket(databuff, head);
            for (int i = 0; i < GpArray.length; i++) {
                for (int j = 0; j < CommandArray.length; j++) {
                    String key = logicAddress + "#" + String.valueOf(GpArray[i]) + "#" + String.valueOf(CommandArray[i]);
                    String value = "2";
                    if (BcdUtils.byteToUnsigned(packet645.getControlCode().getValue()) == successCode) {
                        value = String.valueOf(1);//确认
                    } else if (BcdUtils.byteToUnsigned(packet645.getControlCode().getValue()) == failedCode) {
                        value = String.valueOf(2);//否认
                    }
                    results.put(key, value);
                }
            }
        }
        return results;
    }
}
