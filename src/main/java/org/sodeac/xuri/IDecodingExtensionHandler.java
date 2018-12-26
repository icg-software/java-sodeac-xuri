/*******************************************************************************
 * Copyright (c) 2016, 2018 Sebastian Palarus
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Palarus - initial API and implementation
 *******************************************************************************/
package org.sodeac.xuri;

/**
 * An decoding extension handler decodes a formated string to extension data object of type {@link T} 
 * 
 * @author Sebastian Palarus
 * @since 1.0
 * @version 1.0
 *
 * @param <T>
 */
public interface IDecodingExtensionHandler<T>
{
	/**
	 * decodes a string encoded extension to extension object of type {@link T}
	 * 
	 * @param raw string represents extension object
	 * @return decoded extension data object
	 */
	public T decodeFromString(String raw);
	
	/**
	 * extension type of decoding extension handler
	 * 
	 * @return
	 */
	public String getType();
	
	/**
	 * parse a string encoded extension and set an extension object in {@code extensionHandleObject}
	 * 
	 * @param extensionHandleObject worker object
	 * 
	 * @return position after extension ends
	 */
	public int parseRawExtensionString(ExtensionHandleObject extensionHandleObject);
	
	/**
	 * check if current position of parsed URI worker object is an special start sequence of extension
	 * 
	 * @param extensionHandleObject worker object
	 * 
	 * @return -1, if current position is not special start sequence, otherwise first position of 
	 */
	public int openerCharactersMatched(ExtensionHandleObject extensionHandleObject);
	
	/**
	 * 
	 * @return applicable components for extension
	 */
	public ComponentType[] getApplicableComponents();
}
