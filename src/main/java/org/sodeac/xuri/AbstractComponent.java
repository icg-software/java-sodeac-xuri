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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractComponent<T>  implements IComponent<T>, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -613198655700716268L;
	
	private ComponentType componentType = null;
	private String expression = null;
	
	protected AbstractComponent(ComponentType componentType)
	{
		super();
		this.componentType = componentType;
		this.subComponents = new ArrayList<T>();
		this.subComponentsLock = new ReentrantLock();
	}
	
	private List<T> subComponents = null;
	private volatile List<T> subComponentsImmutable = null;
	private Lock subComponentsLock = null;
	
	protected AbstractComponent<T> addSubComponent(T subComponent)
	{
		this.subComponentsLock.lock();
		try
		{
			this.subComponents.add(subComponent);
			this.subComponentsImmutable = null;
		}
		finally 
		{
			this.subComponentsLock.unlock();
		}
		return this;
	}
	
	public List<T> getSubComponentList()
	{
		List<T> subComponentList = this.subComponentsImmutable;
		if(subComponentList == null)
		{
			this.subComponentsLock.lock();
			try
			{
				subComponentList = this.subComponentsImmutable;
				if(subComponentList != null)
				{
					return subComponentList;
				}
				this.subComponentsImmutable = Collections.unmodifiableList(new ArrayList<T>(this.subComponents));
				subComponentList = this.subComponentsImmutable;
			}
			finally 
			{
				this.subComponentsLock.unlock();
			}
		}
		return subComponentList;
	}
	
	public ComponentType getComponentType()
	{
		return this.componentType;
	}

	public String getExpression()
	{
		return expression;
	}

	protected void setExpression(String expression)
	{
		this.expression = expression;
	}
}
