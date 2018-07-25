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

package org.sodeac.xuri.ldapfilter;

import java.io.Serializable;

public class Attribute implements IFilterItem, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5791677902733009628L;

	public Attribute()
	{
		super();
	}
	
	private boolean invert = false;
	private String name = null;
	private ComparativeOperator operator = null;
	private String value = null;
	private AttributeLinker parent;
	
	public String getName() 
	{
		return name;
	}
	public Attribute setName(String name) 
	{
		this.name = name;
		return this;
	}
	public ComparativeOperator getOperator() 
	{
		return operator;
	}
	public Attribute setOperator(ComparativeOperator operator) 
	{
		this.operator = operator;
		return this;
	}
	public String getValue() 
	{
		return value;
	}
	public Attribute setValue(String value) 
	{
		this.value = value;
		return this;
	}
	
	@Override
	public boolean isInvert() 
	{
		return invert;
	}
	
	@Override
	public Attribute setInvert(boolean invert) 
	{
		this.invert = invert;
		return this;
	}
	
	@Override
	public AttributeLinker getParent()
	{
		return this.parent;
	}
	
	protected void setParent(AttributeLinker parent)
	{
		this.parent = parent;
	}
	
	@Override
	public String toString() 
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("(");
		
		if(invert)
		{
			stringBuilder.append("!(");
		}
		
		stringBuilder.append(name);
		
		if(operator != null)
		{
			if(operator == ComparativeOperator.EQUAL)
			{
				stringBuilder.append("=");
			}
			else if(operator == ComparativeOperator.GREATER)
			{
				stringBuilder.append(">=");
			}
			else if(operator == ComparativeOperator.LESS)
			{
				stringBuilder.append("<=");
			}
			else if(operator == ComparativeOperator.APPROX)
			{
				stringBuilder.append("~=");
			}
		}
		
		if(value != null)
		{
			stringBuilder.append(value);
		}
		
		if(invert)
		{
			stringBuilder.append(")");
		}
		
		stringBuilder.append(")");
		
		return stringBuilder.toString();
	}
}
