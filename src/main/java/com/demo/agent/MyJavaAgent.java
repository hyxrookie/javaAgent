package com.demo.agent;
 
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyJavaAgent {
    private static final Logger LOGGER = Logger.getLogger(MyJavaAgent.class.getName());

    public static void premain(String agentArgs, Instrumentation inst) {
        LOGGER.info("=========premain方法执行========");
        MyClassFileTransformer transformer = new MyClassFileTransformer();
        inst.addTransformer(transformer, true);
    }

    public static void agentmain(String agentOps, Instrumentation inst) {
        LOGGER.info("=========agentmain方法执行========3");
        MyClassFileTransformer transformer = new MyClassFileTransformer();
        inst.addTransformer(transformer, true);

        // 遍历所有已加载的类
        for (Class<?> allLoadedClass : inst.getAllLoadedClasses()) {
            // 检查类名是否包含指定字符串
            LOGGER.info("正在检查类：" + allLoadedClass.getName());
            if (allLoadedClass.getName().startsWith("org.springframework.web.servlet.DispatcherServlet")) {
                try {
                    LOGGER.info("尝试对类 " + allLoadedClass.getName() + " 进行重新转换");
                    inst.retransformClasses(allLoadedClass);
                    LOGGER.info("类 " + allLoadedClass.getName() + " 重新转换成功");
                } catch (UnmodifiableClassException e) {
                    LOGGER.log(Level.SEVERE, "类 " + allLoadedClass.getName() + " 不可修改，无法重新转换", e);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "对类 " + allLoadedClass.getName() + " 进行重新转换时发生未知异常", e);
                }
            }
        }
    }
}