package shared;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.configuration.SubnodeConfiguration;

public interface PluginInterface {

	   public boolean initialize(ConcurrentLinkedQueue<MsgEvent> msgOutQueue,ConcurrentLinkedQueue<MsgEvent> msgInQueue, SubnodeConfiguration configObj, String region,String agent, String plugin);
	   public void msgIn(MsgEvent command);
	   public String getName();
	   public String getVersion();
	   public void shutdown();
	}


