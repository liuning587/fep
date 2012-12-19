/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fep.bp.utils.encoder;

import fep.bp.realinterface.conf.ProtocolConfig;
import fep.bp.realinterface.mto.CollectObject;
import fep.bp.realinterface.mto.CollectObject_TransMit;
import fep.codec.protocol.gb.PmPacket;
import fep.codec.protocol.gb.PmPacketData;
import fep.codec.protocol.gb.gb376.PmPacket376;
import java.util.List;

/**
 *
 * @author xiekeli
 */
public abstract class Encoder {
    public ProtocolConfig config;
    protected static final int MAX_PACKET_LEN = 256;

    /**
     *对采集对象编码，生成报文对象
     * @param obj
     * @param AFN
     * @return
     */
    public abstract PmPacket Encode(CollectObject obj, byte AFN);

    /**
     *对采集对象编码，生成报文对象列表，针对一次采集多个对象的情况
     * @param obj
     * @param AFN
     * @return
     */
    public abstract List<PmPacket376> EncodeList(CollectObject obj, byte AFN);

    /**
     * 对透传类型的采集对象进行编码，生成报文对象列表
     * @param obj
     * @return
     */
    public abstract List<PmPacket376> EncodeList_TransMit(CollectObject_TransMit obj);

    /**
     * @return the config
     */
    public ProtocolConfig getConfig() {
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(ProtocolConfig config) {
        this.config = config;
    }

    public abstract void FillDataBuffer(PmPacketData packetdata, String Format, String DataItemValue, String IsGroupEnd, int Length, int bitnumber);

}
