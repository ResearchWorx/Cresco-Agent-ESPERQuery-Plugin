package plugincore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import shared.MsgEvent;
import shared.MsgEventType;



public class WatchPerf {
	public Timer timer;
	private long startTS;
	private Map<String,String> wdMap;
	private Random rand;
	
	public WatchPerf() {
		  rand = new Random();
		  startTS = System.currentTimeMillis();
		  timer = new Timer();
	      timer.scheduleAtFixedRate(new WatchDogTask(), 1000, PluginEngine.config.getWatchDogTimer());
	      wdMap = new HashMap<String,String>(); //for sending future WD messages
	      	  
	      MsgEvent le = new MsgEvent(MsgEventType.INFO,PluginEngine.config.getRegion(),null,null,"WatchDog timer set to " + PluginEngine.config.getWatchDogTimer() + " milliseconds");
	      le.setParam("src_region", PluginEngine.region);
		  le.setParam("src_agent", PluginEngine.agent);
		  le.setParam("src_plugin", PluginEngine.plugin);
		  le.setParam("dst_region", PluginEngine.region);
		  PluginEngine.clog.log(le);
	      
	  }


	class WatchDogTask extends TimerTask {
		
	    public void run() {
	    	
	    	long runTime = System.currentTimeMillis() - startTS;
	    	 MsgEvent le = new MsgEvent(MsgEventType.WATCHDOG,PluginEngine.region,null,null,"WatchDog timer set to " + PluginEngine.config.getWatchDogTimer() + " milliseconds");
	    	 le.setParam("src_region", PluginEngine.region);
			 le.setParam("src_agent", PluginEngine.agent);
			 le.setParam("src_plugin", PluginEngine.plugin);
			 le.setParam("dst_region", PluginEngine.region);
			 le.setParam("isGlobal", "true");
			 le.setParam("resource_id", PluginEngine.config.getResourceId());
			 le.setParam("inode_id", PluginEngine.config.getINodeId());
			 //le.setParam("perfmetric",PluginEngine.config.getPerfLevel());
			 //le.setParam("perfmetric",String.valueOf(rand.nextInt(100 - 0 + 1) + 0));
			 le.setParam("runtime", String.valueOf(runTime));
			 le.setParam("timestamp", String.valueOf(System.currentTimeMillis()));
			 PluginEngine.clog.log(le);
	    }
	  }

}
