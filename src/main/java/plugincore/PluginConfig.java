package plugincore;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SubnodeConfiguration;

public class PluginConfig {

	private SubnodeConfiguration configObj; 
	  
	
	public PluginConfig(SubnodeConfiguration configObj) throws ConfigurationException
	{
	    this.configObj = configObj;
	}
	public Boolean webDb()
	{
		if(configObj.getString("webdb").equals("1"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public String getResourceId()
	{
		return configObj.getString("resource_id");
	}
	public String getINodeId()
	{
		return configObj.getString("inode_id");
	}
	 
	public Map<String,String> getPluginConfigMap()
	{
		final Map<String,String> result=new TreeMap<String,String>();
		  final Iterator it=configObj.getKeys();
		  while (it.hasNext()) {
		    final Object key=it.next();
		    final String value=configObj.getString(key.toString());
		    result.put(key.toString(),value);
		  }
		  return result;	
	}
	
	public String getPluginConfigString()
	{
		//final Map<String,String> result=new TreeMap<String,String>();
		  StringBuilder sb = new StringBuilder();
			final Iterator it=configObj.getKeys();
		  while (it.hasNext()) {
		    final Object key=it.next();
		    final String value=configObj.getString(key.toString());
		    //result.put(key.toString(),value);
		    sb.append(key.toString() + "=" + value + ",");
		  }
		  return sb.toString().substring(0, sb.length() -1);
		  //return result;	
	}
	
	public String getPluginName()
	{
		return configObj.getString("pluginname");
	}
	/*
	public String getPerfLevel()
	{
		return configObj.getString("perflevel");
	}
	*/
	public String getRegion()
	{
		return configObj.getString("region");
	}
	public boolean getLogConsumerEnabled()
	{
		if(configObj.getString("enablelogconsumer").equals("1"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public int getWatchDogTimer()
	{
		return configObj.getInt("watchdogtimer");
	}
	
	public String getAMQPServer()
	{
		return configObj.getString("ampq_server");
	}
	
	public String getConfig(String configKey)
	{
		return configObj.getString(configKey);
	}
	
	
	
}