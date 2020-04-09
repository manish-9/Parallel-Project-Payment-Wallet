package com.ibm.walletpro.util;

import java.util.Random;

public class Utils {
	static Random random;
	public static double generatePin(){
		
		random = new Random();
		
		int first = random.nextInt(10);
		int second = random.nextInt(10);
		int third = random.nextInt(10);
		int fourth = random.nextInt(10);
		//To make sure that it is not palindrome in nature
		if(first == fourth){
			first++;
		}
		int total = first*1000 + second*100 + third*10 + fourth;
		return (double)total;
		
	}
	
	public static double generateId(){
		int first = random.nextInt(10);
		int second = random.nextInt(10);
		int third = random.nextInt(10);
		int fourth = random.nextInt(10);
	
		//generating a 6 digit id
		
		if(first == fourth){
			fourth++;
		}
		if(second == third){
			third++;
		}
		int total = first*100000 + second*10000 + third*1000 + third*100 + fourth*10 + second;
		return (double)total;
	}
}
