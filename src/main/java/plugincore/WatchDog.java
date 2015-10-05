package plugincore;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import shared.MsgEvent;
import shared.MsgEventType;



public class WatchDog {
	public Timer timer;
	private long startTS;
	private Map<String,String> wdMap;
	
	public WatchDog() {
		  startTS = System.currentTimeMillis();
		  timer = new Timer();
	      timer.scheduleAtFixedRate(new WatchDogTask(), 500, PluginEngine.config.getWatchDogTimer());
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
	    	 //wdMap.put("runtime", String.valueOf(runTime));
	    	 //wdMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
	    	 MsgEvent le = new MsgEvent(MsgEventType.WATCHDOG,PluginEngine.region,null,null,"WatchDog timer set to " + PluginEngine.config.getWatchDogTimer() + " milliseconds");
	    	 le.setParam("src_region", PluginEngine.region);
			 le.setParam("src_agent", PluginEngine.agent);
			 le.setParam("src_plugin", PluginEngine.plugin);
			 le.setParam("dst_region", PluginEngine.region);
			 le.setParam("runtime", String.valueOf(runTime));
			 le.setParam("timestamp", String.valueOf(System.currentTimeMillis()));
			 
			 PluginEngine.clog.log(le);
	    }
	  }

}
