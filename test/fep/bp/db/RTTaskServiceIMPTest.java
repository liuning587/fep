/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fep.bp.db;

import fep.bp.dal.RTTaskService;
import fep.bp.dal.RTTaskServiceIMP;
import fep.bp.model.RealTimeTaskDAO;
import fep.bp.model.UpgradeTaskDAO;
import fep.system.SystemConst;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Thinkpad
 */
public class RTTaskServiceIMPTest {
    private static RTTaskService taskService;
    public RTTaskServiceIMPTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        ApplicationContext cxt = new ClassPathXmlApplicationContext(SystemConst.SPRING_BEANS);
        taskService = (RTTaskService)cxt.getBean(SystemConst.REALTIMETASK_BEAN);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }


   


    /**
     * Test of insertTask method, of class RTTaskServiceIMP.
     */
   // @Test
    public void testInsertTask() {
        RealTimeTaskDAO task = new RealTimeTaskDAO();
        task.setSequencecode(1);
        task.setSendmsg("test");
        taskService.insertTask(task);
    }

    /**
     * Test of insertTasks method, of class RTTaskServiceIMP.
     */
    //@Test
    public void testInsertTasks() {
        List<RealTimeTaskDAO> Tasks = new ArrayList(5);
        for(int i=1;i<=5;i++){
            RealTimeTaskDAO task = new RealTimeTaskDAO();
            task.setSequencecode(i);
            task.setSendmsg("test"+i);
            Tasks.add(task);
        }
        taskService.insertTasks(Tasks);
    }

    /**
     * Test of insertRecvMsg method, of class RTTaskServiceIMP.
     */
    //@Test
    public void testInsertRecvMsg() {
        System.out.println("insertRecvMsg");
        long sequnceCode = 0L;
        String recvMsg = "";
        RTTaskServiceIMP instance = new RTTaskServiceIMP();
        instance.insertRecvMsg(sequnceCode, "",recvMsg);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

     /**
     * Test of getTasks method, of class RTTaskServiceIMP.
     */
    //@Test
    public void testGetTasks() {
        List<RealTimeTaskDAO> tasks = taskService.getTasks();
        assertTrue(tasks.size()>0);

    }
    
    @Test
    public void testInsertUpgradeFile() throws FileNotFoundException {
        String s_FilePath = "F:\\pss\\material\\firmware.bin";
        FileInputStream fin; 
        if(new File(s_FilePath).exists()){ 
            fin = new FileInputStream(new File(s_FilePath));  
            taskService.insertUpgradeFile("1.0","376（20121229）",fin);
        }
    }
    
   // @Test
    public void testInsertUpgradeTask() throws FileNotFoundException, IOException, SerialException, SQLException
    {
        String s_FilePath = "F:\\pss\\material\\firmware.bin";
        if(new File(s_FilePath).exists()){  
            FileInputStream fin2 = new FileInputStream(new File(s_FilePath));  
            if(fin2!=null)
            {
                int fileSize = fin2.available();
                byte[] binFile = new byte[fileSize];
                fin2.read(binFile);              
                UpgradeTaskDAO task = new UpgradeTaskDAO();
                task.setLogicAddress("96123456");
                task.setSequenceCode(2);
                task.setBinFileID(2);
                taskService.insertUpgradeTask(task);
                
                List<UpgradeTaskDAO> taskList = taskService.getUpgradeTasks();
                byte[] result  =null;
                if(taskList!= null)
                {
                    for(UpgradeTaskDAO upgradeTask :taskList )
                    {
                        assertTrue(upgradeTask.getLogicAddress().equals("96123456"));
                        assertTrue(upgradeTask.getSequenceCode()==2);
                        result = upgradeTask.getBinFile2ByteArray();
                    }
                }
                assertTrue(binFile.length ==result.length);
                assertTrue(Arrays.equals(binFile,result));
            }
        }
    }
   // @Test
    public void testGetUpradeTasks() {
        List<UpgradeTaskDAO> tasks = taskService.getUpgradeTasks();
        assertTrue(tasks.size()>0);

    }
}