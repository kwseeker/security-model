package top.kwseeker.controller;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import top.kwseeker.webmod.async.DeferredResultHolder;
import top.kwseeker.webmod.async.MockQueue;

import java.util.concurrent.Callable;

/**
 * 请求异步处理
 * Runnable
 * DeferredResult
 */

@RestController
@RequestMapping("/async")
public class AsyncController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MockQueue mockQueue;
    @Autowired
    private DeferredResultHolder deferredResultHolder;

    /**
     * DeferredResult异步请求的处理流程：
     * 1) 控制器先返回一个DeferredResult对象，并把它存取在内存（队列或列表等）中以便存取
     * 2) Spring MVC开始进行异步处理
     * 3) DispatcherServlet和所有过滤器都退出Servlet容器线程，但此时方法的响应对象仍未返回
     * 4) 由处理该请求的线程对 DeferredResult进行设值，然后Spring MVC会重新把请求分派回Servlet容器，恢复处理
     * 5) DispatcherServlet再次被调用，恢复对该异步返回结果的处理
     * @return DeferredResult对象
     */
    /*@GetMapping("/order")
    @ResponseBody
    public DeferredResult<String> useDeferredResult() {
        DeferredResult<String> deferredResult = new DeferredResult<>();
        // Save the deferredResult somewhere ..
        return deferredResult;
    }*/
    @GetMapping("/order")
    public DeferredResult<String> order() throws Exception {
        logger.info("主线程开始");

        DeferredResult<String> deferredResult = new DeferredResult<>();

        String orderNumber = RandomStringUtils.randomNumeric(8);
        deferredResultHolder.getMap().put(orderNumber, deferredResult);
        mockQueue.setPlaceOrder(orderNumber);   //耗时操作放在工作队列中通过新线程异步处理

        logger.info("主线程结束");
        return deferredResult;
    }

    /**
     * Callable异步请求的处理流程：
     * 1) 控制器先返回一个Callable对象
     * 2) Spring MVC开始进行异步处理，并把该Callable对象提交给另一个独立线程的执行器TaskExecutor处理
     * 3) DispatcherServlet和所有过滤器都退出Servlet容器线程，但此时方法的响应对象仍未返回
     * 4) Callable对象最终产生一个返回结果，此时Spring MVC会重新把请求分派回Servlet容器，恢复处理
     * 5）DispatcherServlet再次被调用，恢复对Callable异步处理所返回结果的处理
     * @return Callable对象
     */
    @GetMapping("/order2")
    public Callable<String> useCallable() {
//        return new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                return "someView";
//            }
//        };
//        return () -> "someView";
        return () -> {
            logger.info("独立线程开始");
//            logger.info(Thread.currentThread().getName());
            Thread.sleep(3000);
            logger.info("独立线程结束");
            return "someView";
        };
    }

}
