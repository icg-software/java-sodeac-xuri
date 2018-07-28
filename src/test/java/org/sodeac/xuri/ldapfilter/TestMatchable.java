package org.sodeac.xuri.ldapfilter;

public class TestMatchable implements IMatchable
{

	@Override
	public boolean matches(ComparativeOperator operator, String name, String valueExpression)
	{
		return name.equalsIgnoreCase(Boolean.TRUE.toString());
	}

}
