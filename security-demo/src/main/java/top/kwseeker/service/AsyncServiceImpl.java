package top.kwseeker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncServiceImpl implements AsyncService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final Integer sharedVar = 0;

    @Async
    @Override
    public String asyncTest() {
        synchronized (sharedVar){   //在非final字段上使用synchronized, 存在引用变更带来的异常的隐患。
            try {
                for (int i = 1;i <= 20;i++){
                    logger.info("----------异步：>"+i);
                    Thread.sleep(100);
                }
                return "请求异步处理完成";
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return "请求异步处理异常退出";
    }
}