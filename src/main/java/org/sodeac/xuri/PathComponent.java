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

public class PathComponent extends AbstractComponent<PathSegment>
{
	/*
	 * https://tools.ietf.org/html/rfc3986#section-3.3
	 */
	private boolean absolute;
	
	public PathComponent(boolean absolute)
	{
		super(ComponentType.PATH);
		this.absolute = absolute;
	}

	public boolean isAbsolute()
	{
		return absolute;
	}
}
