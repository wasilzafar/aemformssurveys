package com.adobe.aemf.facilities.core;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.junit.Ignore;
import org.junit.Test;

import com.adobe.aemf.facilities.util.PortalUtils;

public class PortalUtilsTest {


	private static final String ORIGINAL_STRING = "105";

	@Test
	public void test_encryptInt() throws UnsupportedEncodingException {
		String  enc = PortalUtils.encryptInt(ORIGINAL_STRING);
		System.out.println("Encrypted : "+enc);
		String  dec = PortalUtils.decryptInt(enc);
		System.out.println("Decrypted : "+dec);

  	assertFalse(!ORIGINAL_STRING.equalsIgnoreCase(dec));
	}

}
