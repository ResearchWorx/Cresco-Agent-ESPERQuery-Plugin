package plugincore;

import plugincore.PluginEngine;
import shared.MsgEvent;

public class RPCCall {

	public RPCCall()
	{
		
	}
	public MsgEvent call(MsgEvent me)
	{
		try
		{
			
			String callId = java.util.UUID.randomUUID().toString();
			me.setParam("callId-" + PluginEngine.region + "-" + PluginEngine.agent + "-" + PluginEngine.plugin, callId);
			PluginEngine.msgInQueue.offer(me);
			
			int count = 0;
			int timeout = 300;
			while(count < timeout)
			{
				if(PluginEngine.rpcMap.containsKey(callId))
				{
					MsgEvent ce = null;
					
					synchronized (PluginEngine.rpcMap)
					{
						ce =  PluginEngine.rpcMap.get(callId);
						PluginEngine.rpcMap.remove(callId);
					}
					
					return ce;
				}
				Thread.sleep(100);
				count++;	
			}
			
			return null;
		}
		catch(Exception ex)
		{
			System.out.println("Controller : RPCCall : RPC failed " + ex.toString());
			return null;
		}
  	    
	}
	
}
