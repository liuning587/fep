/*
 * 短信通知处理器
 */
package fep.bp.processor;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fep.bp.dal.SmsService;
import fep.bp.model.SMSDAO;
import fep.system.SystemConst;

/**
 *
 * @author Thinkpad
 */
public class SMSCheckProcessor extends BaseProcessor {

    private final long TIME_BETWEEN_CHECKING = 5 * 1000;
    private final static Logger log = LoggerFactory.getLogger(SMSCheckProcessor.class);
    private SmsService smsService;

    public SMSCheckProcessor() {
        super();
        smsService = (SmsService) cxt.getBean(SystemConst.SMSSERVICE_BEAN);
    }

    @Override
    public void run() {
        for (;;) {
                List<SMSDAO> smsList = smsService.getRecvSMS();
                if (null != smsList) {
                    for (SMSDAO sms : smsList) {
                        SmsRespProcessor.receiveLoubaoOperateMsg(sms.getSmsid(), sms.getLogicAddress(), sms.getGp_addr());
                        smsService.deleteRecvSMS(sms.getSmsid());
                    }
            }
            try {
                Thread.sleep(TIME_BETWEEN_CHECKING);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }
}
