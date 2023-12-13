package com.company.data;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class FileInfoDto {
	private static final long kb = 1024;
	private static final long mb = 1024 * 1024;
	private static final long gb = 1024 * 1024 * 1024;
	
	public abstract String getPath();
	public abstract long getSizeInBytes();
	
	public String sizeInHumanText() {
		long sizeInBytes = getSizeInBytes();
		
		if (sizeInBytes >= 10 * mb) {
			return divideAndScale(sizeInBytes, gb) + " ГБ";
		}
		
		if (sizeInBytes >= 10 * kb) {
			return divideAndScale(sizeInBytes, mb) + " МБ";
		}
		
		if (sizeInBytes >= 10) {
			return divideAndScale(sizeInBytes, kb) + " КБ";
		}
		
		return "" + sizeInBytes;
	}
	
	private String divideAndScale(long numerator, long denominator) {
		return BigDecimal.valueOf(numerator)
		.divide(BigDecimal.valueOf(denominator))
		.setScale(3, RoundingMode.HALF_UP)
		.toString();
	}
}
