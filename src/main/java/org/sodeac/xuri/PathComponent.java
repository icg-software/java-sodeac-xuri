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

/*
 * @see {@link https://tools.ietf.org/html/rfc3986#section-3.3}
 */

/**
 * Path component of URI. Path components contains multiple subcomponents of type {@link PathSegment}.  
 * 
 * @author Sebastian Palarus
 * @since 1.0
 * @version 1.0
 *
 */
public class PathComponent extends AbstractComponent<PathSegment>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2578881338528861330L;
	
	private boolean absolute;
	
	/**
	 * constructor for path component
	 *  
	 * @param absolute path is absolute (starts with / )
	 */
	public PathComponent(boolean absolute)
	{
		super(ComponentType.PATH);
		this.absolute = absolute;
	}

	/**
	 * 
	 * @return path is absolute
	 */
	public boolean isAbsolute()
	{
		return absolute;
	}
}
