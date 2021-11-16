package com.gunyoung.junit.clean;

public class ComparisonCompactor {
	
	private static final String ELLIPSIS = "...";
	private static final String DELTA_END = "]";
	private static final String DELTA_START = "[";
	
	private int contextLength;
	private String expected;
	private String actual;
	private int fPrefix;
	private int fSuffix;
	
	private ComparisonCompactor(int contextLength, String expected, String actual) {
		super();
		this.contextLength = contextLength;
		this.expected = expected;
		this.actual = actual;
	}
	
	public String compact(String message) {
		if(shouldNotCompact()) 
			return Assert.format(message, expected, actual);
		
		findCommonPrefix();
		findCommonSuffix();
		String expected = compactString(this.expected);
		String actual = compactString(this.actual);
		return Assert.format(message, expected, actual);
	}
	
	private boolean shouldNotCompact() {
		return expected == null || actual == null || areStringEqual();
	}
	
	private void findCommonPrefix() {
		fPrefix = 0;
		int end = Math.min(expected.length(), actual.length());
		for(; fPrefix < end; fPrefix++) {
			if(expected.charAt(fPrefix) != actual.charAt(fPrefix))
				break;
		}
	}
	
	private void findCommonSuffix() {
		int expectedSuffix = expected.length() - 1;
		int actualSuffix = actual.length() - 1;
		for(;
			actualSuffix >= fPrefix && expectedSuffix >= fPrefix;
			actualSuffix--, expectedSuffix--) {
			if(expected.charAt(expectedSuffix) != actual.charAt(actualSuffix))
				break;
		}
		fSuffix = expected.length() - expectedSuffix;
	}
	
	private String compactString(String source) {
		String result = DELTA_START + source.substring(fPrefix, source.length() - fSuffix + 1) + DELTA_END;
		
		if(fPrefix > 0) 
			result = computeCommonPrefix() + result;
		if(fSuffix > 0)
			result = result + computeCommonSuffix();
		return result;
	}
	
	private String computeCommonPrefix() {
		return (fPrefix > contextLength ? ELLIPSIS : "") + expected.substring(Math.max(0, fPrefix - contextLength), fPrefix);
	}
	
	private String computeCommonSuffix() {
		int end = Math.min(expected.length() - fSuffix + 1 + contextLength, expected.length());
		return expected.substring(expected.length() - fSuffix + 1, end) + 
				(expected.length() - fSuffix + 1 < expected.length() - contextLength ? ELLIPSIS : "");
	}
	
	private boolean areStringEqual() {
		return expected.equals(actual);
	}
}
