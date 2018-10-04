cd C:\Users\Nick\eclipse-workspace\CIS656Lab2Chat\src
javac -cp PresenceService.jar edu/gvsu/cis/cis656/lab2/PresenceServiceImpl.java
java -cp C:\Users\Nick\eclipse-workspace\CIS656Lab2Chat\src;C:\Users\Nick\eclipse-workspace\CIS656Lab2Chat\src\PresenceService.jar -Djava.rmi.server.codebase=file:/C:/Users/Nick/eclipse-workspace/CIS656Lab2Chat/src/PresenceService.jar -Djava.security.policy=policy edu.gvsu.cis.cis656.lab2.PresenceServiceImpl
