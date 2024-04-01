package com.fuzzy.subsystem.gameserver.tables.comp;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

public class DTDEntityResolver implements EntityResolver
{
	private String _fileName;

	public DTDEntityResolver(File f)
	{
		_fileName = f.getAbsolutePath();
	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
	{
		return new InputSource(_fileName);
	}
}