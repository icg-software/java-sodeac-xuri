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
 * An encoding extension handler encodes an extension data object of type {@link T} to formated string   
 * 
 * @author Sebastian Palarus
 * @since 1.0
 * @version 1.0
 * 
 * @param <T>
 */
public interface IEncodingExtensionHandler<T>
{
	/**
	 * extension type of encoding extension handler
	 * 
	 * @return
	 */
	public String getType();
	
	/**
	 * encodes extension object of type {@link T} to string encoded extension
	 * 
	 * @param extensionDataObject
	 * @return
	 */
	public String encodeToString(T extensionDataObject);
}
