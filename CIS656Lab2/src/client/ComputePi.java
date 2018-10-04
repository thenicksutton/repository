package client;


import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.math.BigDecimal;
import compute.Compute;

public class ComputePi {
	
	public static void help() {
		System.out.println("\r\nSelect an option:");
		System.out.println("1. Compute Pi");
		System.out.println("2. Compute Primes");
		System.out.println("3. Exit");
		System.out.print(">>>  ");
	}
    public static void main(String args[]) {
    	Scanner scanner = new Scanner(System.in);
    	
		try {

	    	String name = "Compute";
			Registry registry = LocateRegistry.getRegistry(args[0]);
			Compute comp = (Compute) registry.lookup(name);
			
			while(true) {
	    		help();

	    		String readline = scanner.nextLine();
	    		int selection = Integer.parseInt(readline);

	    		if (System.getSecurityManager() == null) {
	    			System.setSecurityManager(new SecurityManager());
	    		}
	    		
	    		
	    		
	    		if(selection == 1) {
	    			try {
	    				System.out.print("How many digits?  ");
	    				int digits = Integer.parseInt(scanner.nextLine());
	    				Pi task = new Pi(digits);
	    				BigDecimal pi = comp.executeTask(task);
	    				System.out.println(pi);
	    			} catch (Exception e) {
	    				System.err.println("ComputePi exception:");
	    				e.printStackTrace();
	    			}
	    		} else if(selection == 2) {
	    			try {
	    				System.out.print("Min:  ");
	    				int min = Integer.parseInt(scanner.nextLine());
	    				System.out.print("Max:  ");
	    				int max = Integer.parseInt(scanner.nextLine());
	    				Primes task = new Primes(min, max);
	    				String primes = comp.executeTask(task);
	    				System.out.println(primes);
	    			} catch (Exception e) {
	    				System.err.println("ComputePi exception:");
	    				e.printStackTrace();
	    			}
	    		} else if(selection == 3) {
	    			break;
	    		} else {
	    			System.out.println("Improper selection");
	    		}
	    	}
			
			
		} catch (AccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NotBoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	scanner.close();
    }
}