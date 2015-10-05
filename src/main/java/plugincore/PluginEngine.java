package plugincore;


import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.apache.commons.configuration.SubnodeConfiguration;

import ESPEREngine.QueryNode;
import shared.Clogger;
import shared.MsgEvent;
import shared.MsgEventType;
import shared.PluginImplementation;



public class PluginEngine {

	public static boolean isActive;
	public static PluginConfig config;
	
	public static String pluginName;
	public static String pluginVersion;
	public static String plugin;
	public static String agent;
	public static String region;
	
	public static CommandExec commandExec;
	
	public static ConcurrentMap<String,MsgEvent> rpcMap;
	public static RPCCall rpcc;
	
	public static ConcurrentLinkedQueue<MsgEvent> logOutQueue;
	
	public static WatchDog wd;
	public static WatchPerf wp;
	
	public static Clogger clog;

	public static ConcurrentLinkedQueue<MsgEvent> msgInQueue;
	
	public PluginEngine()
	{
		try
		{
			File jarLocation = new File(PluginEngine.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			pluginName = getPluginName(jarLocation.getAbsolutePath());
			pluginVersion = getPluginVersion(jarLocation.getAbsolutePath());
		}
		catch(Exception ex)
		{
			System.out.println("PluginEngine: Could not set plugin name: " + ex.toString());
			pluginName="cresco-agent-AMQPMonitor-plugin";
			pluginVersion="unknown";
		}
		
	}
	public void shutdown()
	{
		System.out.println("Plugin Shutdown : Agent=" + agent + "pluginname=" + plugin);
		isActive = false;
		wd.timer.cancel(); //prevent rediscovery
		wp.timer.cancel(); //prevent rediscovery
		
		try
		{
			MsgEvent me = new MsgEvent(MsgEventType.CONFIG,region,null,null,"disabled");
			me.setParam("src_region",region);
			me.setParam("src_agent",agent);
			me.setParam("src_plugin",plugin);
			me.setParam("dst_region",region);
			
			//msgOutQueue.offer(me);
			msgInQueue.offer(me);
			//PluginEngine.rpcc.call(me);
			System.out.println("Sent disable message");
		}
		catch(Exception ex)
		{
			String msg2 = "Plugin Shutdown Failed: Agent=" + agent + "pluginname=" + plugin;
			clog.error(msg2);
			
		}
		
	}
	public String getName()
	{
		   return pluginName; 
	}
	public String getVersion() //This should pull the version information from jar Meta data
    {
		   String version;
		   try{
		   String jarFile = PluginImplementation.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		   File file = new File(jarFile.substring(5, (jarFile.length() -2)));
           FileInputStream fis = new FileInputStream(file);
           @SuppressWarnings("resource")
		   JarInputStream jarStream = new JarInputStream(fis);
		   Manifest mf = jarStream.getManifest();
		   
		   Attributes mainAttribs = mf.getMainAttributes();
           version = mainAttribs.getValue("Implementation-Version");
		   }
		   catch(Exception ex)
		   {
			   String msg = "Unable to determine Plugin Version " + ex.toString();
			   clog.error(msg);
			   version = "Unable to determine Version";
		   }
		   
		   return pluginName + "." + version;
	   }
	//steps to init the plugin
	public boolean initialize(ConcurrentLinkedQueue<MsgEvent> msgOutQueue,ConcurrentLinkedQueue<MsgEvent> msgInQueue, SubnodeConfiguration configObj, String region,String agent, String plugin)  
	{
		isActive = true;
		commandExec = new CommandExec();
		rpcMap = new ConcurrentHashMap<String,MsgEvent>();
		rpcc = new RPCCall();
		
		//this.msgOutQueue = msgOutQueue; //send directly to log queue
		this.msgInQueue = msgInQueue; //messages to agent should go here
		
		this.agent = agent;
		this.plugin = plugin;
		
		this.region = region;
		try{
			
			if(msgInQueue == null)
			{
				System.out.println("MsgInQueue==null");
				return false;
			}
			
			this.config = new PluginConfig(configObj);
			
			//create logger
			clog = new Clogger(msgInQueue,region,agent,plugin); //send logs directly to outqueue
			
			String startmsg = "Initializing Plugin: Region=" + region + " Agent=" + agent + " plugin=" + plugin + " version" + getVersion();
			clog.log(startmsg);
			//
			
	    	try
	    	{
	    		System.out.println("Starting ESPER Query Service");
	    		QueryNode qn = new QueryNode();
	    		Thread qnThread = new Thread(qn);
	    		qnThread.start();
		        
	    	}
	    	catch(Exception ex)
	    	{
	    		System.out.println("Unable to Start AMQP Monitor Service Service : " + ex.toString());
	    	}
	    	
	    	
	    	wd = new WatchDog();
			wp = new WatchPerf();
	    	
    		return true;
    		
		
		}
		catch(Exception ex)
		{
			String msg = "ERROR IN PLUGIN: : Region=" + region + " Agent=" + agent + " plugin=" + plugin + " " + ex.toString();
			clog.error(msg);
			return false;
		}
		
	}
	
	public void msgIn(MsgEvent me)
	{
		
		final MsgEvent ce = me;
		try
		{
		Thread thread = new Thread(){
		    public void run(){
		
		    	try 
		        {
					MsgEvent re = commandExec.cmdExec(ce);
					if(re != null)
					{
						re.setReturn(); //reverse to-from for return
						msgInQueue.offer(re); //send message back to queue
					}
					
				} 
		        catch(Exception ex)
		        {
		        	System.out.println("Controller : PluginEngine : msgIn Thread: " + ex.toString());
		        }
		    }
		  };
		  thread.start();
		}
		catch(Exception ex)
		{
			System.out.println("Controller : PluginEngine : msgIn Thread: " + ex.toString());        	
		}
		
	}
	
	public static String getPluginName(String jarFile) //This should pull the version information from jar Meta data
	{
			   String version;
			   try{
			   File file = new File(jarFile);
	          FileInputStream fis = new FileInputStream(file);
	          @SuppressWarnings("resource")
			   JarInputStream jarStream = new JarInputStream(fis);
			   Manifest mf = jarStream.getManifest();
			   
			   Attributes mainAttribs = mf.getMainAttributes();
	          version = mainAttribs.getValue("artifactId");
			   }
			   catch(Exception ex)
			   {
				   String msg = "Unable to determine Plugin Version " + ex.toString();
				   System.err.println(msg);
				   version = "Unable to determine Version";
			   }
			   return version;
	}
	
	public static String getPluginVersion(String jarFile) //This should pull the version information from jar Meta data
	{
			   String version;
			   try{
			   File file = new File(jarFile);
	          FileInputStream fis = new FileInputStream(file);
	          @SuppressWarnings("resource")
			   JarInputStream jarStream = new JarInputStream(fis);
			   Manifest mf = jarStream.getManifest();
			   
			   Attributes mainAttribs = mf.getMainAttributes();
	          version = mainAttribs.getValue("Implementation-Version");
			   }
			   catch(Exception ex)
			   {
				   String msg = "Unable to determine Plugin Version " + ex.toString();
				   System.err.println(msg);
				   version = "Unable to determine Version";
			   }
			   return version;
	}
	
		
}
