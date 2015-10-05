package shared;

import javax.xml.bind.annotation.adapters.XmlAdapter;
 
	public class MsgEventTypesAdapter extends XmlAdapter<String, MsgEventType> {

	    //private Class[] enumClasses = {Compass.class, Suit.class};

	    @Override
	    public MsgEventType unmarshal(String v) throws Exception {
	        //for(Class enumClass : enumClasses) {
	            try {
	                return (MsgEventType) Enum.valueOf(MsgEventType.class, v);
	            } catch(IllegalArgumentException  e) {
	            }
	        //}
	        return null;
	    }

	    @Override
	    public String marshal(MsgEventType v) throws Exception {
	        return v.toString();
	    }

	}