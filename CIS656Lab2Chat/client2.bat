cd C:\Users\Nick\eclipse-workspace\CIS656Lab2Chat\src

javac -cp PresenceService.jar edu/gvsu/cis/cis656/lab2/client/ChatClient.java edu/gvsu/cis/cis656/lab2/client/ProcessIncomingRequest.java
java -cp C:\Users\Nick\eclipse-workspace\CIS656Lab2Chat\src;C:\Users\Nick\eclipse-workspace\CIS656Lab2Chat\src\PresenceService.jar -Djava.rmi.server.codebase=file:/C:/Users/Nick/eclipse-workspace/CIS656Lab2Chat/src/ -Djava.security.policy=policy edu.gvsu.cis.cis656.lab2.client.ChatClient Lisa localhost
