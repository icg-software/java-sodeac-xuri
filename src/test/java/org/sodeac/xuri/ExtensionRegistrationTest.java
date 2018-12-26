package org.sodeac.xuri;

import static org.junit.Assert.assertEquals;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExtensionRegistrationTest
{
	@Test
	public void test001Scheme()
	{
		URIParser parser = new URIParser();
		
		new URI("http://de.wikipedia.org/wiki/Uniform_Resource_Identifier");
		
		int sizeBefore = URIParser.getDecodingExtensionHandler(null, null).size();
		IDecodingExtensionHandler<String> decodingExtension = new IDecodingExtensionHandler()
		{
			
			@Override
			public String getType()
			{
				return "test";
			}

			@Override
			public int parseRawExtensionString(ExtensionHandleObject extensionHandleObject)
			{
				return -1;
			}

			@Override
			public int openerCharactersMatched(ExtensionHandleObject extensionHandleObject)
			{
				return -1;
			}

			@Override
			public ComponentType[] getApplicableComponents()
			{
				return new ComponentType[0];
			}
			
			@Override
			public String decodeFromString(String raw)
			{
				return raw;
			}
		};		
		URIParser.addDecodingExtensionHandler(decodingExtension);
		
		int sizeAfter = URIParser.getDecodingExtensionHandler(null, null).size();
		
		new URI("http://de.wikipedia.org/wiki/Uniform_Resource_Identifier");
		
		assertEquals("size encoding extension handler should be correct", sizeBefore + 1, sizeAfter);
	}
}
