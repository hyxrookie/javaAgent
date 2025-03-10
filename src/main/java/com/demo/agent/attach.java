package com.demo.agent;

 
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.sun.tools.attach.*;

/**
 * 目标项目启动后，attach到目标进程
 */
public class attach {
    private static final Logger LOGGER = Logger.getLogger(attach.class.getName());
    public static void main(String[] args) {
        //查找所有jvm进程，排除attach测试工程
        List<VirtualMachineDescriptor> attach = VirtualMachine.list()
                .stream()
                .filter(jvm -> {
                    return !jvm.displayName().contains("attach");
                }).collect(Collectors.toList());
        for (int i = 0; i < attach.size(); i++) {
            System.out.println("[" + i + "] " + attach.get(i).displayName()+":"+attach.get(i).id());
        }
        System.out.println("请输入需要attach的数组编号");
        Scanner scanner = new Scanner(System.in);
        int s = scanner.nextInt();

        VirtualMachineDescriptor virtualMachineDescriptor = attach.get(s);
        try {
            //D:\MyProject\IdeaProject\javaAgent\target\agent-1.0-SNAPSHOT-jar-with-dependencies.jar
            VirtualMachine virtualMachine = VirtualMachine.attach(virtualMachineDescriptor.id());
            virtualMachine.loadAgent("D:\\MyProject\\IdeaProject\\javaAgent\\target\\agent-1.0-SNAPSHOT-jar-with-dependencies.jar", "param");
            virtualMachine.detach();
        } catch (AttachNotSupportedException e) {
            LOGGER.warning("AttachNotSupportedException：" + e.getMessage());
        } catch (IOException e) {
            LOGGER.warning("IOException：" + e.getMessage());
        } catch (AgentLoadException e) {
            LOGGER.warning("AgentLoadException：" + e.getMessage());
        } catch (AgentInitializationException e) {
            LOGGER.warning("AgentInitializationException：" + e.getMessage());
        }
    }
}