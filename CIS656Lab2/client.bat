echo off
cd C:\Users\Nick\eclipse-workspace\CIS656Lab2\src

javac -cp compute.jar client\ComputePi.java client\Pi.java client\Primes.java
java -cp C:\Users\Nick\eclipse-workspace\CIS656Lab2\src;C:\Users\Nick\eclipse-workspace\CIS656Lab2\src\compute.jar -Djava.rmi.server.codebase=file:/C:/Users/Nick/eclipse-workspace/CIS656Lab2/src/ -Djava.security.policy=policy -Djava.rmi.server.useCodebaseOnly=false client.ComputePi localhost
