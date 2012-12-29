/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fep.bp.utils.encoder.encoder376;

import fep.bp.realinterface.RealTimeProxy376;
import fep.bp.realinterface.mto.CollectObject;
import fep.bp.realinterface.mto.CollectObject_TransMit;
import fep.bp.realinterface.mto.CommandItem;
import fep.bp.realinterface.mto.MTO_376;
import fep.bp.utils.AFNType;
import fep.bp.utils.BaudRate;
import fep.bp.utils.MeterType;
import fep.bp.utils.SerialPortPara;
import fep.bp.utils.encoder.Encoder;
import fep.codec.protocol.gb.PmPacket;
import fep.codec.protocol.gb.gb376.PmPacket376;
import fep.codec.utils.BcdUtils;
import fep.common.exception.BPException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author THINKPAD
 */
public class Encoder376Test {
    private Encoder encoder;
    
    public Encoder376Test() {
        ApplicationContext app =    new  ClassPathXmlApplicationContext("beans.xml");
        encoder = (Encoder376)app.getBean("encoder");
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of Encode method, of class Encoder376.
     */
    //@Test
    public void testEncode() {
        System.out.println("Encode");
        CommandItem commandItem = new CommandItem();
        commandItem.setIdentifier("10040001");
        
        CollectObject obj = new CollectObject();
        obj.setLogicalAddr("96123456");
        obj.setMpSn(new int[]{0});
        obj.AddCommandItem(commandItem);
        
        byte AFN = AFNType.AFN_GETPARA;
        String expResult = "684A004A00685112965634000A80000001000000000001000F16";
        PmPacket encodePacket = encoder.Encode(obj, AFN);
        String result = BcdUtils.binArrayToString(encodePacket.getValue());
        assertEquals(expResult, result);

    }

    /**
     * Test of EncodeList method, of class Encoder376.
     */
   @Test
    public void testEncodeList() {
        System.out.println("EncodeList");
        Map datacellParams = new TreeMap();
        datacellParams.put("1004000401", "13675834792");//主站电话号码或主站手机号码
        datacellParams.put("1004000402", "8613010360500");//短信中心号码
        
        CommandItem citem = new CommandItem();
        citem.setDatacellParam(datacellParams);
        citem.setIdentifier("10040004");
        
        CollectObject obj = new CollectObject();
        obj.AddCommandItem(citem);
        
        obj.setLogicalAddr("96123456");
        obj.setEquipProtocol("01");
        obj.setMpSn(new int[]{0});
        byte AFN = AFNType.AFN_SETPARA;

        List<PmPacket376> resultList = encoder.EncodeList(obj, AFN);
        for(PmPacket376 packet:resultList)
        {
            String result = packet.toHexString();
            String expResult = "68CA00CA006851129656340004800000080013675834792FFFFF8613010360500FFF000000000000000000000000000000000000000001001716";
            assertEquals(expResult, result);
        }
        

    }

    /**
     * Test of EncodeList_TransMit method, of class Encoder376.
     */
    //@Test
    public void testEncodeList_TransMit() throws BPException {
        System.out.println("EncodeList_TransMit");
        
        Map datacellParams1 = new TreeMap();
        datacellParams1.put("0710", "0002");
      
        CommandItem commandItem = new CommandItem();
        commandItem.setIdentifier("80000710");
        commandItem.setDatacellParam(datacellParams1);
        
        CollectObject_TransMit cob = new CollectObject_TransMit();
        cob.setFuncode((byte)0x1b);
        cob.setMeterAddr("000000000001");
        cob.setMeterType(MeterType.Meter645);
        cob.setPort((byte)1);
        cob.setEquipProtocol("100");
        SerialPortPara spp = new SerialPortPara();
        spp.setBaudrate(BaudRate.bps_9600);
        spp.setCheckbit(0);
        spp.setStopbit(1);
        spp.setOdd_even_bit(1);
        spp.setDatabit(8);
        cob.setSerialPortPara(spp);
        cob.setTerminalAddr("96123456");
        cob.setWaitforByte((byte)5);
        cob.setWaitforPacket((byte)10);
        cob.addCommandItem(commandItem);
        
        StringBuilder commandMark = new StringBuilder("80000710#");
        List<PmPacket376> resultList = encoder.EncodeList_TransMit(cob,commandMark);
        for(PmPacket376 packet:resultList)
        {
            String result = packet.toHexString();
            String expResult = "68F200F2006851129656340010E00000010001C70A05140068010000000000681B08433A333333333533A51600000000000000000000000000000000000000000100C016";
            assertEquals(expResult, result);
        }
    }
    
    @Test
    public void testEncodeList_Upgrade() throws FileNotFoundException, IOException
    {
        String s_FilePath = "F:\\pss\\material\\firmware.bin";
        FileInputStream fin; 
        if(new File(s_FilePath).exists()){  
            fin = new FileInputStream(new File(s_FilePath));  
            int fileSize = fin.available();
            byte[] binFile = new byte[fileSize];
            fin.read(binFile);
            
            String Rtua = "96123456";
            List<PmPacket376> resultList;
            resultList = encoder.EncodeList_Upgrade(Rtua, binFile);
            for(PmPacket376 packet:resultList)
            {
                String result = packet.toHexString();
                String expResult = "68F200F2006851129656340010E00000010001C70A05140068010000000000681B08433A333333333533A51600000000000000000000000000000000000000000100C016";
                assertEquals(expResult, result);
            }
        }
    }

   

    /**
     * @param encoder the encoder to set
     */
    public void setEncoder(Encoder encoder) {
        this.encoder = encoder;
    }
}
