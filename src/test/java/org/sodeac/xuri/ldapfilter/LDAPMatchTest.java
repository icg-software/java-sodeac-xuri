package org.sodeac.xuri.ldapfilter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LDAPMatchTest
{
	@Test
	public void test001SimpleAttribute()
	{
		Map<String,IMatchable> props = new HashMap<String, IMatchable>();
		props.put(Boolean.TRUE.toString(), new TestMatchable());
		props.put(Boolean.FALSE.toString(), new TestMatchable());
		
		assertTrue("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(true=)").matches(props)));
		assertFalse("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(!(true=))").matches(props)));
		assertFalse("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(false=)").matches(props)));
		assertTrue("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(!(false=))").matches(props)));
		assertFalse("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(xxx=)").matches(props)));
		assertTrue("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(!(xxx=))").matches(props)));
	
	}
	
	@Test
	public void test002SimpleAnd()
	{
		Map<String,IMatchable> props = new HashMap<String, IMatchable>();
		props.put(Boolean.TRUE.toString(), new TestMatchable());
		props.put(Boolean.FALSE.toString(), new TestMatchable());
		
		assertTrue("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(&(true=))").matches(props)));
		assertFalse("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(&(false=))").matches(props)));
		assertTrue("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(&((true=)))").matches(props)));
		assertTrue("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(&(true=)(true=))").matches(props)));
		assertTrue("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(&(true=)(true=)(true=))").matches(props)));
		assertFalse("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(&(true=)(false=)(true=))").matches(props)));
		assertTrue("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(&(true=)(!(false=))(true=))").matches(props)));
		assertTrue("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(&(true=)(&(true=)(true=))(true=))").matches(props)));
		assertFalse("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(&(true=)(&(true=)(ddd=))(true=))").matches(props)));
		assertFalse("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(&(true=)(&(true=)(true=))(ddd=))").matches(props)));
	}
	
	@Test
	public void test003SimpleOr()
	{
		Map<String,IMatchable> props = new HashMap<String, IMatchable>();
		props.put(Boolean.TRUE.toString(), new TestMatchable());
		props.put(Boolean.FALSE.toString(), new TestMatchable());
		
		assertTrue("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(|(true=))").matches(props)));
		assertFalse("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(|(false=))").matches(props)));
		assertTrue("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(|((true=)))").matches(props)));
		assertTrue("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(|(true=)(true=))").matches(props)));
		assertTrue("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(|(true=)(true=)(true=))").matches(props)));
		assertTrue("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(|(true=)(false=))").matches(props)));
		assertTrue("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(|(true=)(false=)(true=))").matches(props)));
		assertTrue("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(|(true=)(xxx=))").matches(props)));
		assertTrue("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(|(true=)(xxx=)(true=))").matches(props)));
		assertTrue("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(|(false=)(!(false=))(false=))").matches(props)));
		assertTrue("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(|(false=)(|(true=)(false=))(false=))").matches(props)));
	}
	
	@Test
	public void test004Mix()
	{
		Map<String,IMatchable> props = new HashMap<String, IMatchable>();
		props.put(Boolean.TRUE.toString(), new TestMatchable());
		props.put(Boolean.FALSE.toString(), new TestMatchable());
		
		assertTrue("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(&((true=)(|(false=)(&(true=)(true=))))").matches(props)));
		assertFalse("matcher should return expected value",(LDAPFilterDecodingHandler.getInstance().decodeFromString("(&((true=)(|(false=)(&(false=)(true=))))").matches(props)));
	}
}
