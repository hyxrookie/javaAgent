# 使用premain方式启动
###  1 将项目打包
###  2.1在idea里启动，可以添加vm参数 -javaagent:yourPath\agent-1.0-SNAPSHOT-jar-with-dependencies.jar.jar
###  2.2在cmd里启动，可以添加vm参数 java yourPath\agent-1.0-SNAPSHOT-jar-with-dependencies.jar -jar yourOtherPath.jar
# 使用agentmain方式启动
### 1运行目标项目
### 2运行attach#main方法，找到目标项目的JVM参数，然后输入对应的数组标号即可, com.demo.attach L35 要将jar路径改成自己的
