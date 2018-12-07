package edu.gvsu.cis.cis656.lab5;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

@Entity
public class PresenceServiceImpl implements PresenceService {

	@Id Long id = 1L;
	private Vector<RegistrationInfo> clients;
	
	public PresenceServiceImpl() {
		super();
		clients = new Vector<RegistrationInfo>();
	}

	@Override
	public void register(RegistrationInfo reg) throws Exception {
		for(RegistrationInfo r : clients) {
			if(r.getUserName().equals(reg.getUserName())) {
				return;
			}
		}
		System.out.println(reg.getUserName() + " has joined");
		clients.add(reg);
	}

	@Override
	public void unregister(String userName) throws Exception {
		for(RegistrationInfo r : clients) {
			if(r.getUserName().equals(userName)) {
				clients.remove(r);
				break;
			}
		}
		System.out.println(userName + " has left");
	}

	@Override
	public RegistrationInfo lookup(String name) throws Exception {
		for(RegistrationInfo r : clients) {
			if(r.getUserName().equals(name)) {
				return r;
			}
		}
		return null;
	}

	@Override
	public void setStatus(String userName, boolean status) {
		Iterator<RegistrationInfo> itr = clients.iterator();
		while(itr.hasNext()) {
			RegistrationInfo next = itr.next();
			if(next.getUserName().equals(userName)){
				clients.remove(next);
				next.setStatus(status);
				clients.add(next);
				return;
			}
		}
	}

	@Override
	public RegistrationInfo[] listRegisteredUsers() {
//		return (RegistrationInfo[]) clients.toArray();
		RegistrationInfo[] reginfoarray = new RegistrationInfo[clients.size()];
		for(int i = 0; i < clients.size(); i++){
			reginfoarray[i] = clients.get(i);
		}
		return reginfoarray;
	}

}
