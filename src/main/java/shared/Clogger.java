package shared;

import java.util.concurrent.ConcurrentLinkedQueue;


public class Clogger {

	private String region;
	private String agent;
	private String plugin;
	
	private ConcurrentLinkedQueue<MsgEvent> logOutQueue;
	public Clogger(ConcurrentLinkedQueue<MsgEvent> msgOutQueue, String region, String agent, String plugin)
	{
		this.region = region;
		this.agent = agent;
		this.plugin = plugin;
		
		this.logOutQueue = msgOutQueue;
	}
	
	public void log(String logMessage)
	{
		MsgEvent me = new MsgEvent(MsgEventType.INFO,region,null,null,logMessage);
		me.setParam("src_region", region);
		if(agent != null)
		{
			me.setParam("src_agent", agent);
			if(plugin != null)
			{
				me.setParam("src_plugin", plugin);
			}
		}
		me.setParam("dst_region", region);
		logOutQueue.offer(me);
		System.out.println(logMessage);
	}
	public void log(MsgEvent me)
	{
		logOutQueue.offer(me);
	}
	public MsgEvent getLog(String logMessage)
	{
		MsgEvent me = new MsgEvent(MsgEventType.INFO,region,null,null,logMessage);
		me.setParam("src_region", region);
		if(agent != null)
		{
			me.setParam("src_agent", agent);
			if(plugin != null)
			{
				me.setParam("src_plugin", plugin);
			}
		}
		me.setParam("dst_region", region);
		//logOutQueue.offer(me);
		System.out.println(logMessage);
		return me;
	}
	public void error(String ErrorMessage)
	{
		MsgEvent ee = new MsgEvent(MsgEventType.ERROR,region,null,null,ErrorMessage);
		ee.setParam("src_region", region);
		if(agent != null)
		{
			ee.setParam("src_agent", agent);
			if(plugin != null)
			{
				ee.setParam("src_plugin", plugin);
			}
		}
		ee.setParam("dst_region", region);
		logOutQueue.offer(ee);
		System.out.println(ErrorMessage);
	}
	public MsgEvent getError(String ErrorMessage)
	{
		MsgEvent ee = new MsgEvent(MsgEventType.ERROR,region,null,null,ErrorMessage);
		ee.setParam("src_region", region);
		if(agent != null)
		{
			ee.setParam("src_agent", agent);
			if(plugin != null)
			{
				ee.setParam("src_plugin", plugin);
			}
		}
		ee.setParam("dst_region", region);
		//logOutQueue.offer(ee);
		//System.out.println(ErrorMessage);
		return ee;
	}
	
}
