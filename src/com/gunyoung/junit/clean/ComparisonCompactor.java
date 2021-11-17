package com.gunyoung.junit.clean;

public class ComparisonCompactor {
	
	private static final String ELLIPSIS = "...";
	private static final String DELTA_END = "]";
	private static final String DELTA_START = "[";
	
	private int contextLength;
	private String expected;
	private String actual;
	private int prefixLength;
	private int suffixLength;
	
	private ComparisonCompactor(int contextLength, String expected, String actual) {
		super();
		this.contextLength = contextLength;
		this.expected = expected;
		this.actual = actual;
	}
	
	private String compactExpected;
	private String compactActual;
	
	public String formatCompactedComparison(String message) {
		if(shouldBeCompacted()) {
			compactExpectedAndActual(message);
			return Assert.format(message, compactExpected, compactActual);
		}
		return Assert.format(message, expected, actual);
	}
	
	private boolean shouldBeCompacted() {
		return !shouldNotBeCompacted();
	}
	
	private boolean shouldNotBeCompacted() {
		return expected == null || actual == null &&  expected.equals(actual);
	}
	
	private void compactExpectedAndActual(String message) {
		findCommonPrefixAndSuffix();
		compactExpected = compactString(expected);
		compactActual = compactString(actual);
	}
	
	private void findCommonPrefixAndSuffix() {
		findCommonPrefix();
		suffixLength = 0;
		for(;!suffixOverlapsPrefix(suffixLength); suffixLength++) {
			if(charFromEnd(expected, suffixLength) != charFromEnd(actual, suffixLength))
				break;
		}
	}
	
	private void findCommonPrefix() {
		prefixLength = 0;
		int end = Math.min(expected.length(), actual.length());
		for(; prefixLength < end; prefixLength++) {
			if(expected.charAt(prefixLength) != actual.charAt(prefixLength))
				break;
		}
	}
	
	private boolean suffixOverlapsPrefix(int suffixLength) {
		return actual.length() - suffixLength <= prefixLength || expected.length() - suffixLength <= prefixLength;
	}
	
	private char charFromEnd(String s, int i) {
		return s.charAt(s.length() - i - 1);
	}
	
	private String compactString(String s) {
		return new StringBuilder()
				.append(startingEllipsis())
				.append(startingContext())
				.append(DELTA_START)
				.append(delta(s))
				.append(DELTA_END)
				.append(endingContext())
				.append(endingEllipsis())
				.toString();
	}
	
	private String startingEllipsis() {
		return prefixLength > contextLength ? ELLIPSIS : "";
	}
	
	private String startingContext() {
		int contextStart = Math.max(0, prefixLength - contextLength);
		int contextEnd = prefixLength;
		return expected.substring(contextStart, contextEnd);
	}
	
	private String delta(String s) {
		int deltaStart = prefixLength;
		int deltaEnd = s.length() - suffixLength;
		return s.substring(deltaStart, deltaEnd);
	}
	
	private String endingContext() {
		int contextStart = expected.length() - suffixLength;
		int contextEnd = Math.min(contextStart + contextLength, expected.length());
		return expected.substring(contextStart, contextEnd);
	}
	
	private String endingEllipsis() {
		return expected.length() - suffixLength < expected.length() - contextLength ? ELLIPSIS : "";
	}
}
