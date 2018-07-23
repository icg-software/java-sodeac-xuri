/*******************************************************************************
 * Copyright (c) 2016, 2018 Sebastian Palarus
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Palarus - initial API and implementation
 *******************************************************************************/
package org.sodeac.xuri.ldapfilter;


import org.sodeac.xuri.ComponentType;
import org.sodeac.xuri.ExtensionHandleObject;
import org.sodeac.xuri.FormatException;
import org.sodeac.xuri.IEncodingExtensionHandler;

public class LDAPFilterEncodingHandler implements IEncodingExtensionHandler<IFilterItem>
{
	
	public static final char OPENER = IFilterItem.OPENER;
	public static final char CLOSER = IFilterItem.CLOSER;
	public static final char ESCAPE = IFilterItem.ESCAPE;
	public static final char[] OPENER_CHARACTERS = new char[] {OPENER};
	public static final char[] CLOSER_CHARACTERS = new char[] {CLOSER};
	
	private static volatile LDAPFilterEncodingHandler INSTANCE = null;
	
	public static LDAPFilterEncodingHandler getInstance()
	{
		if(INSTANCE == null)
		{
			INSTANCE = new LDAPFilterEncodingHandler();
		}
		return INSTANCE;
	}
	
	@Override
	public String getType()
	{
		return LDAPFilterExtension.TYPE;
	}

	public char[] getOpenerCharacters(ComponentType component)
	{
		return OPENER_CHARACTERS;
	}

	public char[] getCloserCharacters(ComponentType component)
	{
		return CLOSER_CHARACTERS;
	}
	
	@Override
	public ComponentType[] getApplicableComponents()
	{
		return new ComponentType[] {ComponentType.AUTHORITY,ComponentType.PATH,ComponentType.QUERY,ComponentType.FRAGMENT};
	}

	@Override
	public int parseRawExtensionString(ExtensionHandleObject extensionHandleObject)
	{
		char c;
		int openerCount = 0;
		boolean inEscape = false;
		
		for(; extensionHandleObject.position < extensionHandleObject.fullPath.length(); extensionHandleObject.position++)
		{
			c = extensionHandleObject.fullPath.charAt(extensionHandleObject.position);
			
			if(inEscape)
			{
				inEscape = false;
				extensionHandleObject.rawResult.append(c);
				continue;
			}
			
			if(c == ESCAPE)
			{
				inEscape = true;
				extensionHandleObject.rawResult.append(c);
				continue;
			}
			
			if(c == OPENER)
			{
				openerCount++;
			}
			
			if((c == CLOSER) && (openerCount == 0))
			{
				return extensionHandleObject.position + 1;
			}
			
			extensionHandleObject.rawResult.append(c);
			extensionHandleObject.extension = new LDAPFilterExtension();
		}
		
		throw new FormatException("no closing sequence \"" + new String(getCloserCharacters(extensionHandleObject.component)) + "\" found in " + getType() + " : " + extensionHandleObject.rawResult.toString());
	}
	
	

	@Override
	public int openerCharactersMatched(ExtensionHandleObject extensionHandleObject)
	{
		return extensionHandleObject.fullPath.charAt(extensionHandleObject.position) == OPENER ?  extensionHandleObject.position + 1 : -1;
	}
	
	@Override
	public String encodeToString(IFilterItem extensionDataObject)
	{
		return extensionDataObject.toString();
	}
}
