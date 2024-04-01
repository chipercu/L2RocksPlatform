package com.fuzzy.subsystem.gameserver.cache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Впиздовал Diagod.
 * Кешируем месаги из клиента...Хуй его знает зачем эти дебилы отправляют текст от сервера, если можно отправлять ID месаги...
 * Пускай будет, что бы проще было с ПТС АИ работать...
 **/
public class FStringCache
{
	private static final Logger _log = Logger.getLogger(FStringCache.class.getName());

	private static final ConcurrentHashMap<Integer, String> _stringCatch = new ConcurrentHashMap<Integer, String>();

	public static String getString(int id)
	{
		return _stringCatch.get(id);
	}

	public static void reload()
	{
		_stringCatch.clear();
		load();
	}

	public static void load()
	{
		String id = "";
		LineNumberReader lnr = null;
		try
		{
			lnr = new LineNumberReader(new BufferedReader(new FileReader(new File("data/fstring/fstring.txt"))));
			String line = null;

			while((line = lnr.readLine()) != null)
			{
				id = line.split("\t")[0];
				_stringCatch.put(Integer.parseInt(line.split("\t")[0]), line.split("\t")[1]);
			}
		}
		catch(Exception e)
		{
			_log.warning("Error!!!: "+id);
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(lnr != null)
					lnr.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
		}
	}
}