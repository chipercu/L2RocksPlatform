package services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.xml.parsers.DocumentBuilderFactory;

import l2open.config.ConfigValue;
import l2open.extensions.scripts.Functions;
import l2open.extensions.scripts.ScriptFile;
import l2open.gameserver.handler.AdminCommandHandler;
import l2open.gameserver.handler.IAdminCommandHandler;
import l2open.gameserver.model.L2Player;
import l2open.gameserver.model.instances.L2NpcInstance;
import l2open.util.Files;
import l2open.util.GArray;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Сервис трансляции цитат с сайта bash.org.ru в игру
 * @Author: SYS
 */
@SuppressWarnings("unused")
public class Bash extends Functions implements IAdminCommandHandler, ScriptFile
{
	private static enum Commands
	{
		admin_bashreload
	}

	private static String wrongPage = "data/scripts/services/Bash-wrongPage.htm";
	private static String notPage = "data/scripts/services/Bash-notPage.htm";
	private static String readPage = "data/scripts/services/Bash-readPage.htm";

	private static String xmlData = ConfigValue.DatapackRoot + "/data/bash.xml";
	private static GArray<String> quotes = new GArray<String>();

	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, L2Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().IsEventGm)
			return false;

		loadData();
		activeChar.sendMessage("Bash service reloaded.");

		return true;
	}

	public void showQuote(String[] var)
	{
		L2Player player = (L2Player) getSelf();
		L2NpcInstance lastNpc = player.getLastNpc();

		if(!L2NpcInstance.canBypassCheck(player, lastNpc))
			return;

		int page = 1;
		int totalPages = quotes.size();

		try
		{
			page = Integer.parseInt(var[0]);
		}
		catch(NumberFormatException e)
		{
			show(Files.read(wrongPage, player) + navBar(1, totalPages), player, lastNpc);
			return;
		}

		if(page > totalPages && page == 1)
		{
			show(Files.read(notPage, player), player, lastNpc);
			return;
		}

		if(page > totalPages || page < 1)
		{
			show(Files.read(wrongPage, player) + navBar(1, totalPages), player, lastNpc);
			return;
		}

		String html = Files.read(readPage, player);
		html = html.replaceFirst("%quote%", quotes.get(page - 1));
		html = html.replaceFirst("%page%", String.valueOf(page));
		html = html.replaceFirst("%total_pages%", String.valueOf(totalPages));
		html += navBar(page, totalPages);
		show(html, player, lastNpc);
	}

	private int parseRSS()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		Document doc = null;
		try
		{
			doc = factory.newDocumentBuilder().parse(new File(xmlData));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(doc == null)
			return 0;

		quotes.clear();

		int quotesCounter = 0;
		for(Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			if("rss".equalsIgnoreCase(n.getNodeName()))
				for(Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					if("channel".equalsIgnoreCase(d.getNodeName()))
						for(Node i = d.getFirstChild(); i != null; i = i.getNextSibling())
							if("item".equalsIgnoreCase(i.getNodeName()))
								for(Node z = i.getFirstChild(); z != null; z = z.getNextSibling())
									if("description".equalsIgnoreCase(z.getNodeName()))
									{
										//Убираем лишние обратные слэши и знаки $
										quotes.add(z.getTextContent().replaceAll("\\\\", "").replaceAll("\\$", ""));
										quotesCounter++;
									}
		return quotesCounter;
	}

	public String getPage(String url_server, String url_document)
	{
		StringBuffer buf = new StringBuffer();
		Socket s;
		try
		{
			try
			{
				s = new Socket(url_server, 80);
			}
			catch(Exception e)
			{
				return null;
			}

			s.setSoTimeout(30000); //Таймут 30 секунд
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "Cp1251"));
			PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));

			out.print("GET http://" + url_server + "/" + url_document + " HTTP/1.1\r\n" + //
			"User-Agent: L2Phoenix Lineage 2 Server\r\n" + //
			"Host: " + url_server + "\r\n" + //
			"Accept: */*\r\n" + //
			"Connection: close\r\n" + //
			"\r\n");
			out.flush();

			boolean header = true;
			for(String line = in.readLine(); line != null; line = in.readLine())
			{
				if(header && line.startsWith("<?xml "))
					header = false;
				if(!header)
					buf.append(line).append("\r\n");
				if(!header && line.startsWith("</rss>"))
					break;
			}

			s.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}

	private String navBar(int curPage, int totalPages)
	{
		String html;
		html = "<br><center><table border=0 width=240><tr><td widht=30>";
		if(curPage > 1)
			html += "<a action=\"bypass -h scripts_services.Bash:showQuote " + (curPage - 1) + "\">";
		html += "&lt;&lt;&lt; Назад";
		if(curPage > 1)
			html += "</a>";
		html += "</td><td widht=160>&nbsp;[" + curPage + "]&nbsp;</td><td widht=40>";
		if(curPage < totalPages)
			html += "<a action=\"bypass -h scripts_services.Bash:showQuote " + (curPage + 1) + "\">";
		html += "Вперед &gt;&gt;&gt;";
		if(curPage < totalPages)
			html += "</a>";
		html += "</td></tr></table></center>";
		html += "<table border=0 width=240><tr><td width=150>";
		html += "Перейти на страницу:</td><td><edit var=\"page\" width=40></td><td>";
		html += "<button value=\"Перейти\" action=\"bypass -h scripts_services.Bash:showQuote $page\" width=60 height=20 back=L2UI_CT1.Button_DF fore=L2UI_CT1.Button_DF>";
		html += "</td></tr></table>";
		return html;
	}

	public static String DialogAppend_30086(Integer val)
	{
		if(val != 0 || !ConfigValue.BashEnabled)
			return "";
		return "<br><center><a action=\"bypass -h scripts_services.Bash:showQuote 1\">Почитать Цитатник Рунета</a></center>";
	}

	public void loadData()
	{
		if(ConfigValue.BashReloadTime > 0)
			executeTask("services.Bash", "loadData", new Object[0], ConfigValue.BashReloadTime * 60 * 60 * 1000L);

		// Скачиваем файл и сохраняем его на диске
		String data;
		try
		{
			data = getPage("bash.org.ru", "rss/");
		}
		catch(Exception E)
		{
			data = null;
		}
		if(data == null)
		{
			_log.info("Service: Bash - RSS data download failed.");
			return;
		}
		data = data.replaceFirst("windows-1251", "utf-8");

		if(!ConfigValue.BashSkipDownload)
		{
			Files.writeFile(xmlData, data);
			_log.info("Service: Bash - RSS data download completed.");
		}

		int parse = parseRSS();
		if(parse == 0)
		{
			_log.info("Service: Bash - RSS data parse error.");
			return;
		}
		_log.info("Service: Bash - RSS data parsed: loaded " + parse + " quotes.");
	}

	public void onLoad()
	{
		_log.info("Loaded Service: Bash [" + (ConfigValue.BashEnabled ? "enabled]" : "disabled]"));
		if(ConfigValue.BashEnabled)
		{
			AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
			loadData();
		}
	}

	public void onReload()
	{}

	public void onShutdown()
	{}

	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}