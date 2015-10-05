package ESPEREngine;



import java.text.ParseException;
import java.util.concurrent.ConcurrentHashMap;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import plugincore.PluginEngine;

public class QueryNode implements Runnable {

	//AMQP
	private ConnectionFactory factory;    
	private Connection connection;
	private QueueingConsumer consumer;
	private String amqp_server;
	private String amqp_login;
	private String amqp_password;
	private static String amqp_outexchange;
	private String amqp_inexchange;
	private String esper_querystring;
	
    
	//ESPER
	private static EPRuntime cepRT;
	private static EPAdministrator cepAdm;
	private static ConcurrentHashMap<String,CEPListener> listners;
	private static ConcurrentHashMap<String,EPStatement> statements;
	private static Channel tx_channel;
	private static Gson gson;
	
	
    public QueryNode()
    {
    	this.amqp_server = PluginEngine.config.getConfig("amqp_server");
    	this.amqp_login = PluginEngine.config.getConfig("amqp_login");
    	this.amqp_password = PluginEngine.config.getConfig("amqp_password");
    	this.amqp_inexchange = PluginEngine.config.getConfig("amqp_inexchange");
    	amqp_outexchange = PluginEngine.config.getConfig("amqp_outexchange");
    	this.esper_querystring = PluginEngine.config.getConfig("esper_querystring");
    	
    	listners = new ConcurrentHashMap<String,CEPListener>();
		statements = new ConcurrentHashMap<String,EPStatement>();
    	
		//
		gson = new GsonBuilder().create();
			
    }
	
    public void run() 
	{
		try
		{
			
			// START AMQP
			factory = new ConnectionFactory();
			factory.setHost(amqp_server);
			factory.setUsername(amqp_login);
			factory.setPassword(amqp_password);
			
			factory.setConnectionTimeout(10000);
			connection = factory.newConnection();
		
			//RX Channel
			Channel rx_channel = connection.createChannel();
			rx_channel.exchangeDeclare(amqp_inexchange, "fanout");
			String queueName = rx_channel.queueDeclare().getQueue();
			rx_channel.queueBind(queueName, amqp_inexchange, "");

			consumer = new QueueingConsumer(rx_channel);
			rx_channel.basicConsume(queueName, true, consumer); 
			//END RX
			
			//TX Channel
			tx_channel = connection.createChannel();
			tx_channel.exchangeDeclare(amqp_outexchange, "fanout");
			//END TX
			
			// END AMQP
			
			//START ESPER
			
			//The Configuration is meant only as an initialization-time object.
	        Configuration cepConfig = new Configuration();
	        //cepConfig.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
	        cepConfig.addEventType("netFlow", netFlow.class.getName());
	        EPServiceProvider cep = EPServiceProviderManager.getProvider("myCEPEngine", cepConfig);
	        cepRT = cep.getEPRuntime();
	        cepAdm = cep.getEPAdministrator();
	        
	        //Add test Query
	        if(!addQuery("0", esper_querystring))
	        {
	        	String errorString = "QueryNode: Unable to add query: " + esper_querystring;
				System.out.println(errorString);
				
	        }
	        
	 		//END ESPER
			
			
		while (PluginEngine.isActive) 
    	{
			try
			{
				QueueingConsumer.Delivery delivery = consumer.nextDelivery(500);
				if(!(delivery == null))
				{
					String message = new String(delivery.getBody());
					//pass messages to processor
					input(message);
				}
			}
			catch(Exception ex)
			{
				String errorString = "QueryNode: Error: " + ex.toString();
				System.out.println(errorString);
			}
    	}
		rx_channel.close();
		tx_channel.close();
		
		}
		catch(Exception ex)
		{
			System.out.println("QueryNode Error: " + ex.toString());
		}
		
	}    

    public static class CEPListener implements UpdateListener {
    	public String query_id;
    	public Channel tx_channel;
    	public String outExchange;
    	
    	public CEPListener(String query_id, Channel tx_channel, String outExchange)
    	{
    		this.query_id = query_id;
    		this.tx_channel = tx_channel;
    		this.outExchange = outExchange;
    	}
    	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if (newEvents != null) 
            {
            	 
            		String str = newEvents[0].getUnderlying().toString();
            		if(str != null)
            		{
            			try
            			{
            				tx_channel.basicPublish(outExchange, "", null, str.getBytes());
            				//System.out.println(str);
            			}
            			catch(Exception ex)
            			{
            				System.out.println("ESPEREngine : Error : " + ex.toString());
            			}
            		}
            		
            	
            }
            if (oldEvents != null) 
            {
            	 System.out.println("Old Event received: " + oldEvents[0].getUnderlying());
            	 //count++;
            }
        }
    }
    
    public static boolean addQuery(String query_id, String query)
    {
    	try
    	{
    		EPStatement cepStatement = cepAdm.createEPL(query);
    		CEPListener c = new CEPListener(query_id, tx_channel, amqp_outexchange);
    		cepStatement.addListener(c); 
    		listners.put(query_id, c);
    		statements.put(query_id, cepStatement);
    		return true;
    	}
    	catch(Exception ex)
    	{
    		System.out.println("ESPEREngine addQuery: " + ex.toString());
    		return false;
    	}
    }
    
    public static boolean delQuery(String query_id)
    {
    	try
    	{
    		EPStatement cepStatement = statements.get(query_id);
    		CEPListener c = listners.get(query_id);
    		cepStatement.removeListener(c);
    		c = null;
    		cepStatement.stop();
    		cepStatement.destroy();
    		cepStatement = null;
    		listners.remove(query_id);
    		statements.remove(query_id);
    		return true;
    	}
    	catch(Exception ex)
    	{
    		System.out.println("ESPEREngine delQuery: " + ex.toString());
    		return false;
    	}
    	
    }
    
    public static void input(String inputStr) throws ParseException 
    {
    	try
    	{
    	    netFlow flow = nFlowFromJson(inputStr);
			cepRT.sendEvent(flow);
    	}
    	catch(Exception ex)
    	{
    		System.out.println("ESPEREngine : Input netFlow Error : " + ex.toString());
    		System.out.println("ESPEREngine : Input netFlow Error : InputStr " + inputStr);
    	}
    	
    }
    
    private static netFlow nFlowFromJson(String json)
	{
		netFlow flow = gson.fromJson(json, netFlow.class);
        return flow;
	}
    
    
}
