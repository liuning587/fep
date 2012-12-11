/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fep.main;

import fep.bp.config.Config;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Timer;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import fep.bp.processor.SmsRespProcessor;
import fep.mina.common.RtuUnrespPacketChecker;
import fep.mina.protocolcodec.gb.PepGbCommunicator;
import fep.mina.protocolcodec.gb.gb376.PmPacket376CodecFactory;
import fep.mina.protocolcodec.gb.gb376.PmPacket376ServerIoHandler;

/**
 *
 * @author luxiaochung
 */
public class Main {
    
    public static int PORT = 10086;

    public static void main(String[] args) throws IOException {
    	Config.LoadProperties("fepConfig.properties");
        PORT = Integer.parseInt(Config.ReadByKey("port"));
        //组装底层通讯模块
        PepGbCommunicator rtuMap = new PepGbCommunicator();
        PmPacket376ServerIoHandler serverIoHandle = new PmPacket376ServerIoHandler(rtuMap);
        NioSocketAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("protocol", new ProtocolCodecFilter(new PmPacket376CodecFactory()));       
        acceptor.setDefaultLocalAddress(new InetSocketAddress(PORT));
        acceptor.setHandler(serverIoHandle);
        acceptor.bind();
        System.out.println("FEP前置通信器监听端口： " + PORT);
        //System.out.println("Idle Timeout "+acceptor.getSessionConfig().getIdleTime(IdleStatus.BOTH_IDLE));

        Timer checkTimer = new Timer();
        RtuUnrespPacketChecker checker = new RtuUnrespPacketChecker(rtuMap);
        long timestamp = 10*1000;
        checkTimer.schedule(checker, timestamp,timestamp);


        //启动业务处理器
        SmsRespProcessor.setRtuMap(rtuMap);
        MainProcess PBProcessor = new MainProcess(rtuMap);
        PBProcessor.run();
    }
}
