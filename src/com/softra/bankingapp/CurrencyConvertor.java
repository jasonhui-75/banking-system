package com.softra.bankingapp;


public interface CurrencyConvertor {
	public static double USDToAUD(double USD) {
		return USD*1.39;
	}
	public static double AUDToUSD(double AUD) {
		return AUD*0.72;
	}
	public static double USDToSGD(double USD) {
		return USD*1.36;
	}
	public static double SGDToUSD(double SGD) {
		return SGD*0.73;
	}
	public static double AUDToSGD(double AUD) {
		return AUD*0.98;
	}
	public static double SGDToAUD(double SGD) {
		return SGD*1.02;
	}
}
