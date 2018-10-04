package client;

import java.io.Serializable;

import compute.Task;

public class Primes implements Task<String>, Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int min;
	int max;
	
	public Primes(int min, int max) {
		this.min = min;
		this.max = max;
	}
	
	public static String computePrimes(int min, int max) {
		String output = "";
		
		boolean[] primeValue = new boolean[max + 1];
		for(int i = 2; i <= max; i++) {
			primeValue[i] = true;
		}
		for(int i = 2; i <= Math.sqrt(max); i++) {
			if(primeValue[i]) {
				for(int j = i; i*j <= max; j++) {
					primeValue[i*j] = false;
				}
			}
		}
		
		for(int k = min; k <= max; k++) {
			if(primeValue[k]) {
				output += k + ", ";
			}
		}
		
		return output;
	}

	@Override
	public String execute() {
		return computePrimes(min, max);
	}

}
