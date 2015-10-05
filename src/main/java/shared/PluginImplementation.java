package shared;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.configuration.SubnodeConfiguration;

import plugincore.PluginEngine;


public class PluginImplementation implements PluginInterface {

	public PluginEngine pe;
	
	public PluginImplementation()
	{
		pe = new PluginEngine(); //actual plugin code
		
	}
	public String getName()
	{
		   return ((PluginEngine) pe).getName(); 
	}
    public String getVersion()
    {
    	return ((PluginEngine) pe).getVersion();
	}	
	public void msgIn(MsgEvent command)
    {
		((PluginEngine) pe).msgIn(command);
	}
	public boolean initialize(ConcurrentLinkedQueue<MsgEvent> msgOutQueue,ConcurrentLinkedQueue<MsgEvent> msgInQueue, SubnodeConfiguration configObj, String region, String agent, String plugin) 
	{
	   return ((PluginEngine) pe).initialize(msgOutQueue,msgInQueue,configObj,region,agent,plugin);
    }
	public void shutdown()
	{
		   ((PluginEngine) pe).shutdown(); 
	}
	
}

