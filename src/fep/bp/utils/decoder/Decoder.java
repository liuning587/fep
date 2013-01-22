/*
 * 解码器基类
 */
package fep.bp.utils.decoder;


import fep.bp.model.Dto;
import fep.bp.realinterface.conf.ProtocolConfig;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Thinkpad
 */
public abstract class Decoder {

    
    protected ProtocolConfig config;
    private final static Logger log = LoggerFactory.getLogger(Decoder.class);

    


    public abstract Map<String, Map<String, String>> decode2Map(Object pack);

    public abstract void decode2dto(Object pack, Dto dto);

    public abstract Map<String, Map<String, String>> decode2Map_TransMit(Object pack);
    public abstract Map<String, Map<String, String>> decode2Map_TransMit_WriteBack(Object pack);
    public abstract Map<String, String> decode2Map_TransMit_WriteParameterBack(Object pack,String[] GpArray,String[] CommandArray);
    public abstract Map<String, String> decode2Map_TransMit_ControlBack(Object pack,String[] GpArray,String[] CommandArray);
    public abstract void decode2dto_TransMit(Object pack, Dto dto);
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
}
