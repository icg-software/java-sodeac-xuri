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
		
		int sizeBefore = URIParser.getEncodingExtensionHandler(null, null).size();
		IEncodingExtensionHandler<String> encodingExtension = new IEncodingExtensionHandler<String>()
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
			public String encodeToString(String extensionDataObject)
			{
				return extensionDataObject;
			}
			
		};		
		URIParser.addEncodingExtensionHandler(encodingExtension);
		
		int sizeAfter = URIParser.getEncodingExtensionHandler(null, null).size();
		
		new URI("http://de.wikipedia.org/wiki/Uniform_Resource_Identifier");
		
		assertEquals("size encoding extension handler should be correct", sizeBefore + 1, sizeAfter);
	}
}
