/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fep.mina.protocolcodec.gb.gb376;

import fep.bp.dal.StatusService;
import fep.bp.model.StatusDAO;
import fep.codec.protocol.gb.gb376.PmPacket376;
import fep.codec.protocol.gb.gb376.PmPacket376Factroy;
import fep.codec.utils.BcdUtils;
import fep.mina.protocolcodec.gb.PepGbCommunicator;
import fep.system.SystemConst;
import java.util.Date;
import java.util.TreeSet;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author luxiaochung
 */
public class PmPacket376ServerIoHandler extends IoHandlerAdapter {
    private StatusService statusService;
    protected ApplicationContext cxt;
    private PepGbCommunicator rtuMap;
    private boolean showActTestPack = true;
    private final static String SESSION_RTUS = PmPacket376ServerIoHandler.class.getName() + ".rtus";
    private final static Logger LOGGER = LoggerFactory.getLogger(PmPacket376ServerIoHandler.class);

    public PmPacket376ServerIoHandler(PepGbCommunicator rtuMap) {
        super();
        cxt = new ClassPathXmlApplicationContext(SystemConst.SPRING_BEANS);
        statusService = (StatusService) cxt.getBean(SystemConst.STATUS_BEAN);
        this.rtuMap = rtuMap;
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        if (session.getAttribute(SESSION_RTUS) != null) {
            TreeSet<String> rtus = (TreeSet<String>) session.getAttribute(SESSION_RTUS);
            for (String rtua : rtus) {
                rtuMap.rtuDisconnectted(rtua);
                LOGGER.info("rtua<" + rtua + "> disconnect");
                Date curDate = new Date(System.currentTimeMillis());
                statusService.insertStatus(new StatusDAO(rtua,true,curDate,false));
            }
            rtus.clear();
        }
    }


    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        PmPacket376 pack = (PmPacket376) message;
        String rtua = pack.getAddress().getRtua();
        showReceivePacket(rtua,pack);

        if (!pack.getControlCode().getIsUpDirect()) {
            return;
        }
        registRtua(session, rtua);
        
        Date curDate = new Date(System.currentTimeMillis());
        statusService.insertStatus(new StatusDAO(rtua,true,curDate,true));
        
        if (pack.getControlCode().getIsOrgniger()) {//主动上送
            PmPacket376 respPack = PmPacket376Factroy.makeAcKnowledgementPack(pack, 3, (byte) 0);
            session.write(respPack);
        }
        rtuMap.rtuReceiveTcpPacket(rtua, session, pack);
        
    }

    private boolean isActiveTestPack(PmPacket376 pack){
        return pack.getAfn()==2;
    }

    private boolean needNotShow(PmPacket376 pack){
        return isActiveTestPack(pack)&&(!showActTestPack);
    }

    private void showReceivePacket(String rtua, PmPacket376 pack){
        if (!needNotShow(pack)){
            LOGGER.info("Receive from rtua<" + rtua + ">: " + BcdUtils.binArrayToString(pack.getValue()) + '\n');
        }
    }

    private void registRtua(IoSession session, String rtua) {
        TreeSet<String> rtus;
        if (session.getAttribute(SESSION_RTUS) == null) {
            rtus = new TreeSet<String>();
            session.setAttribute(SESSION_RTUS, rtus);
        } else {
            rtus = (TreeSet<String>) session.getAttribute(SESSION_RTUS);
        }

        rtus.add(rtua);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        PmPacket376 pack = (PmPacket376) message;
        if (!((pack.getAfn() == 2) && (!showActTestPack))) {
            LOGGER.info(" Had Sent to rtua<" + pack.getAddress().getRtua() + ">: "
                    + pack.toHexString() + '\n');
           // commLogWriter.insertLog(pack.getAddress().getRtua(),BcdUtils.binArrayToString(pack.getValue()),"D" );
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable thrml){
        LOGGER.info("Catch a exception: "+ thrml.getMessage());
        session.close(true);
    }
}
