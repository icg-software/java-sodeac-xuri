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

public interface IFilterItem 
{
	public static final char OPENER = '(';
	public static final char CLOSER = ')';
	public static final char ESCAPE = '\\';
	public static final char NOT = '!';
	public static final char AND = '&';
	public static final char OR = '|';
	public static final char LESS_STARTSEQ = '<';
	public static final char GREATER_STARTSEQ = '>';
	public static final char APPROX_STARTSEQ = '~';
	public static final char EQUAL = '=';
	
	public boolean isInvert() ;
	public IFilterItem setInvert(boolean invert) ;
	public AttributeLinker getParent();
}