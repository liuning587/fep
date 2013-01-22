/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fep.bp.realinterface.conf;

import fep.codec.utils.CastorUtil;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.xml.sax.InputSource;

/**
 *
 * @author Thinkpad
 */
public final class ProtocolConfig {

	private static String PROTOCOL_DATA_CONFIG_MAPPING="protocol-data-config-mapping.xml";
	private static String PROTOCOL_DATA_CONFIG="protocol-data-config.xml";
	private static ProtocolConfig instance = null;
	private static ProtocolCommandItems commandItems;
/*
        public ProtocolConfig(final String str1, final String str2) throws IOException {
            try {  
                InputSource is = getTransFileReader(str2);                
                Mapping map = new Mapping();  
                map.loadMapping(this.getClass().getResource(str1));  
                Unmarshaller unmarshaller = new Unmarshaller(ProtocolCommandItems.class);  
                unmarshaller.setMapping(map);  
                commandItems= (ProtocolCommandItems) unmarshaller.unmarshal(is);  
            
              //  commandItems = (ProtocolCommandItems) CastorUtil.unmarshal(resource1.getURL(), resource2.getURI());
             //   commandItems.FillMap();
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        }
*/
        
	public ProtocolConfig(final String str1, final String str2) throws IOException {
            if (instance == null) {
                try {
                    PROTOCOL_DATA_CONFIG_MAPPING = str1;
                    PROTOCOL_DATA_CONFIG = str2;
                    ResourceLoader resourceLoader = new DefaultResourceLoader();
                    Resource resource1 = resourceLoader.getResource(str1);
                    Resource resource2 = resourceLoader.getResource(str2);
                    commandItems = (ProtocolCommandItems) CastorUtil.unmarshal(resource1.getURL(), resource2.getURI());
                    commandItems.FillMap();
                } catch (IOException iOException) {
                   throw iOException;
                }
            }
	}

     /*   
        public ProtocolConfig(final String MapFile, final String ConfigFile) throws IOException, MappingException, MarshalException, ValidationException {
            if (instance == null) {
                try {
                    Mapping mapping = new Mapping();
                    mapping.loadMapping(MapFile);
                    Reader reader = new InputStreamReader(new FileInputStream(ConfigFile), "UTF-8"); 
                    Unmarshaller unmarshaller = new Unmarshaller(ProtocolCommandItems.class);
                    unmarshaller.setMapping(mapping);

                    commandItems = (ProtocolCommandItems) unmarshaller.unmarshal(reader);
                   // Reader reader = new InputStreamReader(new FileInputStream("person.xml"), "UTF-8"); 
                    commandItems.FillMap();
                } catch (IOException iOException) {
                   throw iOException;
                }
            }
	}
        */
	public static ProtocolConfig getInstance() throws IOException, MappingException, MarshalException, ValidationException {
		if (instance == null) {
			try {
				instance = new ProtocolConfig(PROTOCOL_DATA_CONFIG_MAPPING, PROTOCOL_DATA_CONFIG);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw e;
			}
			commandItems.FillMap();
		}
		return instance;
	}

	public String getFormat(String DataItemCode) {
		ProtocolDataItem dataItem = commandItems.getDataItem(DataItemCode);
		if (null != dataItem) {
			return dataItem.getFormat();

		} else {
			return "";

		}
	}

	public int getLength(String DataItemCode) {
		ProtocolDataItem dataItem = commandItems.getDataItem(DataItemCode);
		if (null != dataItem) {
			return dataItem.getLength();
		} else {
			return -1;

		}
	}

    @SuppressWarnings("static-access")
	public Map<String, ProtocolDataItem> getDataItemMap(String CommandItemCode) {
		return this.commandItems.getCommandItem(CommandItemCode).getDataItemMap();
	}

        public List<ProtocolDataItem> getDataItemList(String CommandItemCode){
            return commandItems.getCommandItem(CommandItemCode).getDataItems();
        }
        
        public InputSource getTransFileReader(String transFile) throws FileNotFoundException 
        {  
            InputSource is = null;  
            is = new InputSource(new FileReader(transFile));  
            return is;  
        }  

}
