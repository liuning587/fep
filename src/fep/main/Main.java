/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fep.main;

import fep.bp.config.Config;
import fep.bp.processor.SmsRespProcessor;
import fep.mina.common.unrespchecker.RtuUnresponseChecker;
import fep.mina.protocolcodec.gb.PepGbCommunicator;
import fep.mina.protocolcodec.gb.gb376.PmPacket376CodecFactory;
import fep.mina.protocolcodec.gb.gb376.PmPacket376ServerIoHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

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



        RtuUnresponseChecker checker = new RtuUnresponseChecker(rtuMap);
        checker.run();


        //启动业务处理器
        SmsRespProcessor.setRtuMap(rtuMap);
        MainProcess PBProcessor = new MainProcess(rtuMap);
        PBProcessor.run();
    }
}
