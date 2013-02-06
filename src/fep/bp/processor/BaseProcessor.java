/*
 * 业务处理器父类
 */

package fep.bp.processor;

import fep.system.SystemConst;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Thinkpad
 */
public class BaseProcessor implements Runnable{
    protected ApplicationContext cxt;
    protected ProcessorStatus status;
    public BaseProcessor(){
        cxt = new ClassPathXmlApplicationContext(SystemConst.SPRING_BEANS);
    }

    @Override
    public void run(){

    }

    public void setProcessorStatus(ProcessorStatus status)
    {
        this.status = status;
    }
}
