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

import java.io.Serializable;

public class ExtensionHandleObject implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4292017896595754513L;
	
	public URI uri;
	public ComponentType component;
	public String fullPath;
	public int position;
	public StringBuilder rawResult;
	public IExtension extension;
}
