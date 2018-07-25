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
import java.util.LinkedList;

import org.sodeac.xuri.FormatException;
import org.sodeac.xuri.IDecodingExtensionHandler;

public class LDAPFilterDecodingHandler implements IDecodingExtensionHandler<IFilterItem>, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9187580171255086052L;
	
	private transient static volatile LDAPFilterDecodingHandler INSTANCE = null;
	
	public static LDAPFilterDecodingHandler getInstance()
	{
		if(INSTANCE == null)
		{
			INSTANCE = new LDAPFilterDecodingHandler();
		}
		return INSTANCE;
	}
	
	@Override
	public IFilterItem decodeFromString(String raw)
	{
		IFilterItem rootFilter = null;
		IFilterItem currentFilter = null;
		IFilterItem previewsFilter = null;
		LinkedList<IFilterItem> filterItemPath = new LinkedList<IFilterItem>(); 
		LinkedList<Integer> unclosedChildOpenerPath = new LinkedList<Integer>();
		
		StringBuilder sb = new StringBuilder();
		boolean openMode = true;
		boolean inLinkerMode = false;
		boolean inAttributeMode = false;
		
		boolean invert = false;
		
		boolean inAttributeNameMode = false;
		boolean inAttributeValueMode = false;
		
		int unclosedOpener = 0;
		int unclosedChildOpener = 0;
		
		char c;
		rawloop:
		for(int i = 0; i < raw.length(); i++)
		{
			c = raw.charAt(i);
			
			switch (c) 
			{
				case IFilterItem.OPENER:
					if(inAttributeMode)
					{
						throw new FormatException("unexpected position for " + c + " : " + i + " / (attribute mode)");
					}
					unclosedOpener++;
					unclosedChildOpener++;
					openMode = true;
					break;
				case IFilterItem.CLOSER:
					unclosedOpener--;
					unclosedChildOpener--;
					
					if(unclosedOpener < 0)
					{
						throw new FormatException("unexpected position for " + c + " : " + i + " / too much closer");
					}
					
					if(unclosedChildOpener < 0)
					{
						if(inAttributeMode)
						{
							if(! inAttributeValueMode)
							{
								throw new FormatException("unexpected position for " + c + " : " + i + " / no operator");
							}
							((Attribute)currentFilter).setValue(sb.toString());
						}
						filterItemPath.removeLast();
						currentFilter = null;
						if(! filterItemPath.isEmpty())
						{
							currentFilter = filterItemPath.getLast();
						}
						if(currentFilter == null)
						{
							if(! filterItemPath.isEmpty())
							{
								throw new FormatException("unexpected position for " + c + " : " + i + " / filterItemPath is not empty");
							}
							break rawloop;
						}
						unclosedChildOpener = unclosedChildOpenerPath.removeLast() - 1;
					}
					
					if(currentFilter instanceof Attribute)
					{
						throw new FormatException("unexpected position for " + c + " : " + i + " / in attribute");
					}
					
					openMode = false;
					inAttributeNameMode = false;
					inAttributeValueMode = false;
					inLinkerMode = currentFilter instanceof AttributeLinker;
					inAttributeMode = false;
					
					break;
				case ' ':
					if(inAttributeValueMode)
					{
						sb.append(c);
					}
					break;
				case '\t':
					if(inAttributeValueMode)
					{
						sb.append(c);
					}
					break;
				case IFilterItem.LESS_STARTSEQ:
					
					if(! inAttributeMode)
					{
						throw new FormatException("unexpected position for " + c + " : " + i + " / not in attribute mode");
					}
					
					if(inAttributeNameMode)
					{
						if(raw.charAt(i+1) == IFilterItem.EQUAL)
						{
							if(sb.toString().isEmpty())
							{
								throw new FormatException("unexpected position for " + c + " : " + i + " / attribute name is empty");
							}
							((Attribute)currentFilter).setName(sb.toString());
							((Attribute)currentFilter).setOperator(ComparativeOperator.LESS);
							sb.setLength(0);
							inAttributeNameMode = false;
							inAttributeValueMode = true;
							i++;
						}
						else
						{
							throw new FormatException("unexpected position for " + c + " : " + (i+1) + " / expect '=' ");
						}
					}
					else if(inAttributeValueMode)
					{
						sb.append(c);
					}
					else
					{
						throw new FormatException("unexpected position for " + c + " : " + i + " / in mad attribute mode ");
					}
				
					break;
				case IFilterItem.GREATER_STARTSEQ:
					
					if(! inAttributeMode)
					{
						throw new FormatException("unexpected position for " + c + " : " + i + " / not in attribute mode");
					}
					
					if(inAttributeNameMode)
					{
						if(raw.charAt(i+1) == IFilterItem.EQUAL)
						{
							if(sb.toString().isEmpty())
							{
								throw new FormatException("unexpected position for " + c + " : " + i + " / attribute name is empty");
							}
							((Attribute)currentFilter).setName(sb.toString());
							((Attribute)currentFilter).setOperator(ComparativeOperator.GREATER);
							sb.setLength(0);
							inAttributeNameMode = false;
							inAttributeValueMode = true;
							i++;
						}
						else
						{
							throw new FormatException("unexpected position for " + c + " : " + (i+1) + " / expect '=' ");
						}
					}
					else if(inAttributeValueMode)
					{
						sb.append(c);
					}
					else
					{
						throw new FormatException("unexpected position for " + c + " : " + i + " / in mad attribute mode ");
					}
				
					break;
				case IFilterItem.APPROX_STARTSEQ:
					
					if(! inAttributeMode)
					{
						throw new FormatException("unexpected position for " + c + " : " + i + " / not in attribute mode");
					}
					
					if(inAttributeNameMode)
					{
						if(raw.charAt(i+1) == IFilterItem.EQUAL)
						{
							if(sb.toString().isEmpty())
							{
								throw new FormatException("unexpected position for " + c + " : " + i + " / attribute name is empty");
							}
							((Attribute)currentFilter).setName(sb.toString());
							((Attribute)currentFilter).setOperator(ComparativeOperator.APPROX);
							sb.setLength(0);
							inAttributeNameMode = false;
							inAttributeValueMode = true;
							i++;
						}
						else
						{
							throw new FormatException("unexpected position for " + c + " : " + (i+1) + " / expect '=' ");
						}
					}
					else if(inAttributeValueMode)
					{
						sb.append(c);
					}
					else
					{
						throw new FormatException("unexpected position for " + c + " : " + i + " / in mad attribute mode ");
					}
				
					break;
				case IFilterItem.EQUAL:
					
					if(! inAttributeMode)
					{
						throw new FormatException("unexpected position for " + c + " : " + i + " / not in attribute mode");
					}
					
					if(inAttributeNameMode)
					{
						if(sb.toString().isEmpty())
						{
							throw new FormatException("unexpected position for " + c + " : " + i + " / attribute name is empty");
						}
						((Attribute)currentFilter).setName(sb.toString());
						((Attribute)currentFilter).setOperator(ComparativeOperator.EQUAL);
						sb.setLength(0);
						inAttributeNameMode = false;
						inAttributeValueMode = true;
					}
					else if(inAttributeValueMode)
					{
						sb.append(c);
					}
					else
					{
						throw new FormatException("unexpected position for " + c + " : " + i + " / in mad attribute mode ");
					}
				
					break;
				case IFilterItem.NOT:
					
					if(! openMode)
					{
						throw new FormatException("unexpected position for " + c + " : " + i);
					}
					
					invert = !invert;
					break;
				case IFilterItem.AND:
					
					if(! openMode)
					{
						throw new FormatException("unexpected position for " + c + " : " + i);
					}
					
					// add filter
					previewsFilter = currentFilter;
					currentFilter = new AttributeLinker();
					currentFilter.setInvert(invert);
					((AttributeLinker)currentFilter).setOperator(LogicalOperator.AND);
					if(previewsFilter != null)
					{
						((AttributeLinker)previewsFilter).addItem(currentFilter);
					}
					filterItemPath.addLast(currentFilter);
					unclosedChildOpenerPath.addLast(unclosedChildOpener);
					unclosedChildOpener = 0;
					
					// (re)set modes
					inLinkerMode = true;
					inAttributeMode = false;
					inAttributeNameMode = false;
					inAttributeValueMode = false;
					openMode = false;
					invert = false;
					
					// set as root if first
					if(rootFilter == null)
					{
						rootFilter = currentFilter;
					}
					break;
				case IFilterItem.OR:
					
					if(! openMode)
					{
						throw new FormatException("unexpected position for " + c + " : " + i);
					}
					
					// add filter
					previewsFilter = currentFilter;
					currentFilter = new AttributeLinker();
					currentFilter.setInvert(invert);
					((AttributeLinker)currentFilter).setOperator(LogicalOperator.OR);
					if(previewsFilter != null)
					{
						((AttributeLinker)previewsFilter).addItem(currentFilter);
					}
					filterItemPath.addLast(currentFilter);
					unclosedChildOpenerPath.addLast(unclosedChildOpener);
					unclosedChildOpener = 0;
					
					// (re)set modes
					inLinkerMode = true;
					inAttributeMode = false;
					inAttributeNameMode = false;
					inAttributeValueMode = false;
					openMode = false;
					invert = false;
					
					// set as root if first
					if(rootFilter == null)
					{
						rootFilter = currentFilter;
					}
					break;
				default:
					if(openMode)
					{
						previewsFilter = currentFilter;
						currentFilter = new Attribute();
						currentFilter.setInvert(invert);
						filterItemPath.addLast(currentFilter);
						unclosedChildOpenerPath.addLast(unclosedChildOpener);
						unclosedChildOpener = 0;
						
						if(previewsFilter != null)
						{
							if(! (previewsFilter instanceof AttributeLinker))
							{
								throw new FormatException("parent of attribute must be a linker! pos: " + i);
							}
							if(previewsFilter != null)
							{
								((AttributeLinker)previewsFilter).addItem(currentFilter);
							}
						}
						
						// (re)set modes
						inLinkerMode = false;
						inAttributeMode = true;
						inAttributeNameMode = true;
						inAttributeValueMode = false;
						openMode = false;
						invert = false;
						
						// append char
						sb.setLength(0);
						sb.append(c);
						
						// set as root if first
						if(rootFilter == null)
						{
							rootFilter = currentFilter;
						}
					}
					else if(inAttributeMode)
					{
						// append char
						sb.append(c);
					}
					else
					{
						throw new FormatException("unexpected position for " + c + " : " + i + " => not in linkermode");
					}
					break;
			}
		}
		
		return rootFilter;
	}
	
}
