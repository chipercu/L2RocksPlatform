package com.fuzzy.subsystem.gameserver.communitybbs.Manager;

import javolution.util.FastMap;
import com.fuzzy.subsystem.gameserver.communitybbs.BB.Forum;
import com.fuzzy.subsystem.gameserver.communitybbs.BB.Post;
import com.fuzzy.subsystem.gameserver.communitybbs.BB.Post.CPost;
import com.fuzzy.subsystem.gameserver.communitybbs.BB.Topic;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import org.apache.commons.lang3.StringEscapeUtils;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

public class PostBBSManager extends BaseBBSManager
{
	private Map<Topic, Post> _PostByTopic;
	private static PostBBSManager _Instance;

	public static PostBBSManager getInstance()
	{
		if(_Instance == null)
			_Instance = new PostBBSManager();
		return _Instance;
	}

	public PostBBSManager()
	{
		_PostByTopic = new FastMap<Topic, Post>();
	}

	public Post getGPosttByTopic(Topic t)
	{
		Post post = null;
		post = _PostByTopic.get(t);
		if(post == null)
		{
			post = load(t);
			_PostByTopic.put(t, post);
		}
		return post;
	}

	public void delPostByTopic(Topic t)
	{
		_PostByTopic.remove(t);
	}

	public void addPostByTopic(Post p, Topic t)
	{
		if(_PostByTopic.get(t) == null)
			_PostByTopic.put(t, p);
	}

	private Post load(Topic t)
	{
		Post p;
		p = new Post(t);
		return p;
	}

	@Override
	public void parsecmd(String command, L2Player player)
	{
		if(player.getEventMaster() != null && player.getEventMaster().blockBbs())
			return;
		if(command.startsWith("_bbsposts;read;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			int idf = Integer.parseInt(st.nextToken());
			int idp = Integer.parseInt(st.nextToken());
			String index = null;
			if(st.hasMoreTokens())
				index = st.nextToken();
			int ind = 0;
			if(index == null)
				ind = 1;
			else
				ind = Integer.parseInt(index);

			showPost(TopicBBSManager.getInstance().getTopicByID(idp), ForumsBBSManager.getInstance().getForumByID(idf), player, ind);
		}
		else if(command.startsWith("_bbsposts;edit;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			int idf = Integer.parseInt(st.nextToken());
			int idt = Integer.parseInt(st.nextToken());
			int idp = Integer.parseInt(st.nextToken());
			showEditPost(TopicBBSManager.getInstance().getTopicByID(idt), ForumsBBSManager.getInstance().getForumByID(idf), player, idp);
		}
		else
			separateAndSend("<html><body><br><br><center>the command: " + command + " is not implemented yet</center><br><br></body></html>", player);
	}

	private void showEditPost(Topic topic, Forum forum, L2Player player, int idp)
	{
		Post p = getGPosttByTopic(topic);
		if(forum == null || topic == null || p == null)
			separateAndSend("<html><body><br><br><center>Error, this forum, topic or post does not exit !</center><br><br></body></html>", player);
		else
			ShowHtmlEditPost(topic, player, forum, p);
	}

	/**
	 * @param posttByTopic
	 * @param forumByID
	 * @param player
	 * @param ind
	 * @param idf
	 */
	private void showPost(Topic topic, Forum forum, L2Player player, int ind)
	{
		if(forum == null || topic == null)
			separateAndSend("<html><body><br><br><center>Error, this forum is not implemented yet</center><br><br></body></html>", player);
		else if(forum.getType() == Forum.MEMO)
			ShowMemoPost(topic, player, forum);
		else
			separateAndSend("<html><body><br><br><center>the forum: " + forum.getName() + " is not implemented yet</center><br><br></body></html>", player);
	}

	/**
	 * @param topic
	 * @param player
	 * @param forum
	 * @param p
	 */
	private void ShowHtmlEditPost(Topic topic, L2Player player, Forum forum, Post p)
	{
		StringBuilder html = new StringBuilder("<html>");
		html.append("<body><br><br>");
		html.append("<table border=0 width=610><tr><td width=10></td><td width=600 align=left>");
		html.append("<a action=\"bypass -h _bbshome\">HOME</a>&nbsp;>&nbsp;<a action=\"bypass -h _bbsmemo\">" + forum.getName() + " Form</a>");
		html.append("</td></tr>");
		html.append("</table>");
		html.append("<img src=\"L2UI.squareblank\" width=\"1\" height=\"10\">");
		html.append("<center>");
		html.append("<table border=0 cellspacing=0 cellpadding=0>");
		html.append("<tr><td width=610><img src=\"sek.cbui355\" width=\"610\" height=\"1\"><br1><img src=\"sek.cbui355\" width=\"610\" height=\"1\"></td></tr>");
		html.append("</table>");
		html.append("<table fixwidth=610 border=0 cellspacing=0 cellpadding=0>");
		html.append("<tr><td><img src=\"l2ui.mini_logo\" width=5 height=20></td></tr>");
		html.append("<tr>");
		html.append("<td><img src=\"l2ui.mini_logo\" width=5 height=1></td>");
		html.append("<td align=center FIXWIDTH=60 height=29>&$413;</td>");
		html.append("<td FIXWIDTH=540>" + topic.getName() + "</td>");
		html.append("<td><img src=\"l2ui.mini_logo\" width=5 height=1></td>");
		html.append("</tr></table>");
		html.append("<table fixwidth=610 border=0 cellspacing=0 cellpadding=0>");
		html.append("<tr><td><img src=\"l2ui.mini_logo\" width=5 height=10></td></tr>");
		html.append("<tr>");
		html.append("<td><img src=\"l2ui.mini_logo\" width=5 height=1></td>");
		html.append("<td align=center FIXWIDTH=60 height=29 valign=top>&$427;</td>");
		html.append("<td align=center FIXWIDTH=540><MultiEdit var =\"Content\" width=535 height=313></td>");
		html.append("<td><img src=\"l2ui.mini_logo\" width=5 height=1></td>");
		html.append("</tr>");
		html.append("<tr><td><img src=\"l2ui.mini_logo\" width=5 height=10></td></tr>");
		html.append("</table>");
		html.append("<table fixwidth=610 border=0 cellspacing=0 cellpadding=0>");
		html.append("<tr><td><img src=\"l2ui.mini_logo\" width=5 height=10></td></tr>");
		html.append("<tr>");
		html.append("<td><img src=\"l2ui.mini_logo\" width=5 height=1></td>");
		html.append("<td align=center FIXWIDTH=60 height=29>&nbsp;</td>");
		html.append("<td align=center FIXWIDTH=70><button value=\"&$140;\" action=\"Write Post " + forum.getID() + ";" + topic.getID() + ";0 _ Content Content Content\" back=\"L2UI_CT1.Button_DF_Small_Down\" width=65 height=20 fore=\"L2UI_CT1.Button_DF_Small\" ></td>");
		html.append("<td align=center FIXWIDTH=70><button value = \"&$141;\" action=\"bypass -h _bbsmemo\" back=\"L2UI_CT1.Button_DF_Small_Down\" width=65 height=20 fore=\"L2UI_CT1.Button_DF_Small\"> </td>");
		html.append("<td align=center FIXWIDTH=400>&nbsp;</td>");
		html.append("<td><img src=\"l2ui.mini_logo\" width=5 height=1></td>");
		html.append("</tr></table>");
		html.append("</center>");
		html.append("</body>");
		html.append("</html>");
		send1001(html.toString(), player);
		send1002(player, p.getCPost(0)._PostTxt, topic.getName(), DateFormat.getInstance().format(new Date(topic.getDate())));
	}

	/**
	 * @param topic
	 * @param player
	 * @param forum
	 */
	private void ShowMemoPost(Topic topic, L2Player player, Forum forum)
	{
		Post p = getGPosttByTopic(topic);
		Locale locale = Locale.getDefault();
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL, locale);
		StringBuilder html = new StringBuilder("<html><body><br><br>");
		html.append("<table border=0 width=610><tr><td width=10></td><td width=600 align=left>");
		html.append("<a action=\"bypass -h _bbshome\">HOME</a>&nbsp;>&nbsp;<a action=\"bypass -h _bbsmemo\">Memo Form</a>");
		html.append("</td></tr>");
		html.append("</table>");
		html.append("<img src=\"L2UI.squareblank\" width=\"1\" height=\"10\">");
		html.append("<center>");
		html.append("<table border=0 cellspacing=0 cellpadding=0 bgcolor=333333>");
		html.append("<tr><td height=10></td></tr>");
		html.append("<tr>");
		html.append("<td fixWIDTH=55 align=right valign=top>&$413; : &nbsp;</td>");
		html.append("<td fixWIDTH=380 valign=top>" + topic.getName() + "</td>");
		html.append("<td fixwidth=5></td>");
		html.append("<td fixwidth=50></td>");
		html.append("<td fixWIDTH=120></td>");
		html.append("</tr>");
		html.append("<tr><td height=10></td></tr>");
		html.append("<tr>");
		html.append("<td align=right><font color=\"AAAAAA\" >&$417; : &nbsp;</font></td>");
		html.append("<td><font color=\"AAAAAA\">" + topic.getOwnerName() + "</font></td>");
		html.append("<td></td>");
		html.append("<td><font color=\"AAAAAA\">&$418; :</font></td>");
		html.append("<td><font color=\"AAAAAA\">" + dateFormat.format(p.getCPost(0)._PostDate) + "</font></td>");
		html.append("</tr>");
		html.append("<tr><td height=10></td></tr>");
		html.append("</table>");
		html.append("<br>");
		html.append("<table border=0 cellspacing=0 cellpadding=0>");
		html.append("<tr>");
		html.append("<td fixwidth=5></td>");
		String Mes = p.getCPost(0)._PostTxt;

		Mes = StringEscapeUtils.escapeHtml4(Mes).replace("\n", "<br1>");

		html.append("<td FIXWIDTH=600 align=left>" + Mes + "</td>");
		html.append("<td fixqqwidth=5></td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("<br>");
		html.append("<img src=\"L2UI.squareblank\" width=\"1\" height=\"5\">");
		html.append("<img src=\"L2UI.squaregray\" width=\"610\" height=\"1\">");
		html.append("<img src=\"L2UI.squareblank\" width=\"1\" height=\"5\">");
		html.append("<table border=0 cellspacing=0 cellpadding=0 FIXWIDTH=610>");
		html.append("<tr>");
		html.append("<td width=50>");
		html.append("<button value=\"&$422;\" action=\"bypass -h _bbsmemo\" back=\"L2UI_CT1.Button_DF_Small_Down\" width=65 height=20 fore=\"L2UI_CT1.Button_DF_Small\">");
		html.append("</td>");
		html.append("<td width=560 align=right><table border=0 cellspacing=0><tr>");
		html.append("<td FIXWIDTH=300></td><td><button value = \"&$424;\" action=\"bypass -h _bbsposts;edit;" + forum.getID() + ";" + topic.getID() + ";0\" back=\"L2UI_CT1.Button_DF_Small_Down\" width=65 height=20 fore=\"L2UI_CT1.Button_DF_Small\" ></td>&nbsp;");
		html.append("<td><button value = \"&$425;\" action=\"bypass -h _bbstopics;del;" + forum.getID() + ";" + topic.getID() + "\" back=\"L2UI_CT1.Button_DF_Small_Down\" width=65 height=20 fore=\"L2UI_CT1.Button_DF_Small\" ></td>&nbsp;");
		html.append("<td><button value = \"&$421;\" action=\"bypass -h _bbstopics;crea;" + forum.getID() + "\" back=\"L2UI_CT1.Button_DF_Small_Down\" width=65 height=20 fore=\"L2UI_CT1.Button_DF_Small\" ></td>&nbsp;");
		html.append("</tr></table>");
		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("<br>");
		html.append("<br>");
		html.append("<br></center>");
		html.append("</body>");
		html.append("</html>");
		separateAndSend(html.toString(), player);
	}

	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2Player player)
	{
		StringTokenizer st = new StringTokenizer(ar1, ";");
		int idf = Integer.parseInt(st.nextToken());
		int idt = Integer.parseInt(st.nextToken());
		int idp = Integer.parseInt(st.nextToken());

		Forum f = ForumsBBSManager.getInstance().getForumByID(idf);
		if(f == null)
			separateAndSend("<html><body><br><br><center>the forum: " + idf + " does not exist !</center><br><br></body></html>", player);
		else
		{
			Topic t = f.gettopic(idt);
			if(t == null)
				separateAndSend("<html><body><br><br><center>the topic: " + idt + " does not exist !</center><br><br></body></html>", player);
			else
			{
				CPost cp = null;
				Post p = getGPosttByTopic(t);
				if(p != null)
					cp = p.getCPost(idp);
				if(cp == null || p == null)
					separateAndSend("<html><body><br><br><center>the post: " + idp + " does not exist !</center><br><br></body></html>", player);
				else
				{
					p.getCPost(idp)._PostTxt = ar4;
					p.updatetxt(idp);
					parsecmd("_bbsposts;read;" + f.getID() + ";" + t.getID(), player);
				}
			}
		}
	}
}