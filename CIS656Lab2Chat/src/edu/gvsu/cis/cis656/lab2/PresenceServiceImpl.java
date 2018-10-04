package edu.gvsu.cis.cis656.lab2;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;


public class PresenceServiceImpl implements PresenceService {

	private Vector<RegistrationInfo> clients;
	
	public PresenceServiceImpl() {
		super();
		clients = new Vector<RegistrationInfo>();
	}

	@Override
	public boolean register(RegistrationInfo reg) throws RemoteException {
		for(RegistrationInfo r : clients) {
			if(r.getUserName().equals(reg.getUserName())) {
				return false;
			}
		}
		System.out.println(reg.getUserName() + " has joined");
		clients.add(reg);
		return true;
	}

	@Override
	public boolean updateRegistrationInfo(RegistrationInfo reg) throws RemoteException {
		Iterator<RegistrationInfo> itr = clients.iterator();
		while(itr.hasNext()) {
			RegistrationInfo next = itr.next();
			if(next.getUserName().equals(reg.getUserName())){
				clients.remove(next);
				clients.add(reg);
				return true;
			}
		}
		return false;
	}

	@Override
	public void unregister(String userName) throws RemoteException {
		for(RegistrationInfo r : clients) {
			if(r.getUserName().equals(userName)) {
				clients.remove(r);
				break;
			}
		}
		System.out.println(userName + " has left");
	}

	@Override
	public RegistrationInfo lookup(String name) throws RemoteException {
		for(RegistrationInfo r : clients) {
			if(r.getUserName().equals(name)) {
				return r;
			}
		}
		return null;
	}

	@Override
	public Vector<RegistrationInfo> listRegisteredUsers() throws RemoteException {
		return clients;
	}
	
	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
		try {
            String name = "PresenceService";
            PresenceService engine = new PresenceServiceImpl();
            PresenceService stub =
                    (PresenceService) UnicastRemoteObject.exportObject(engine, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("PresenceService bound");
        } catch (Exception e) {
            System.err.println("PresenceService exception:");
            e.printStackTrace();
        }
	}

}
