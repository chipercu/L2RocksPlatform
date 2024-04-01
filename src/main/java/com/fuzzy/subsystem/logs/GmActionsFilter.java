package com.fuzzy.subsystem.logs;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class GmActionsFilter implements Filter
{
	@Override
	public boolean isLoggable(LogRecord record)
	{
		return record.getLoggerName().equals("gmactions");
	}

}
