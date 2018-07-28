package org.sodeac.xuri.ldapfilter;

public interface IMatchable
{
	public boolean matches(ComparativeOperator operator, String name, String valueExpression);
}
