echo off
cd C:\Users\Nick\eclipse-workspace\CIS656Lab2

java -cp C:\Users\Nick\eclipse-workspace\CIS656Lab2\src;C:\Users\Nick\eclipse-workspace\CIS656Lab2\src\compute.jar -Djava.rmi.server.codebase=file:/C:/Users/Nick/eclipse-workspace/CIS656Lab2/src/compute.jar -Djava.security.policy=policy -Djava.rmi.server.useCodebaseOnly=false engine.ComputeEngine
