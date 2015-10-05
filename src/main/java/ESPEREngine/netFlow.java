package ESPEREngine;


import java.lang.reflect.Field;

public class netFlow {

	public String tcp_flags;
	public String src_as_path;
	public String stamp_updated;
	public String cos;
	public String etype;
	public String tag2;
	public String timestamp_end;
	public String vlan;
	public String peer_as_src;
	public String bytes;
	public String tag;
	public String ip_dst;
	public String as_path;
	public String peer_ip_src;
	public String label;
	public String as_dst;
	public String port_dst;
	public String src_comms;
	public String med;
	public String nat_event;
	public String mac_dst;
	public String comms;
	public String as_src;
	public String peer_ip_dst;
	public String iface_out;
	public String mac_src;
	public String src_med;
	public String peer_as_dst;
	public String src_local_pref;
	public String local_pref;
	public String post_nat_port_src;
	public String iface_in;
	public String mpls_vpn_rd;
	public String ip_src;
	public String post_nat_ip_dst;
	public String ip_proto;
	public String mask_src;
	public String mask_dst;
	public String port_src;
	public String tos;
	public String sampling_rate;
	public String post_nat_ip_src;
	public String post_nat_port_dst;
	public String stamp_inserted;
	public String mpls_label_top;
	public String mpls_label_bottom;
	public String mpls_stack_depth;
	public String packets;
	public String timestamp_start;
	public String flows;
	
	public netFlow(String tcp_flags, String src_as_path, String stamp_updated, String cos, String etype, String tag2, String timestamp_end, String vlan, String peer_as_src, String bytes, String tag, String ip_dst, String as_path, String peer_ip_src, String label, String as_dst, String port_dst, String src_comms, String med, String nat_event, String mac_dst, String comms, String as_src, String peer_ip_dst, String iface_out, String mac_src, String src_med, String peer_as_dst, String src_local_pref, String local_pref, String post_nat_port_src, String iface_in, String mpls_vpn_rd, String ip_src, String post_nat_ip_dst, String ip_proto, String mask_src, String mask_dst, String port_src, String tos, String sampling_rate, String post_nat_ip_src, String post_nat_port_dst, String stamp_inserted, String mpls_label_top, String mpls_label_bottom, String mpls_stack_depth, String packets, String timestamp_start, String flows)
	{
		this.tcp_flags = tcp_flags;
		this.src_as_path = src_as_path;
		this.stamp_updated = stamp_updated;
		this.cos = cos;
		this.etype = etype;
		this.tag2 = tag2;
		this.timestamp_end = timestamp_end;
		this.vlan = vlan;
		this.peer_as_src = peer_as_src;
		this.bytes = bytes;
		this.tag = tag;
		this.ip_dst = ip_dst;
		this.as_path = as_path;
		this.peer_ip_src = peer_ip_src;
		this.label = label;
		this.as_dst = as_dst;
		this.port_dst = port_dst;
		this.src_comms = src_comms;
		this.med = med;
		this.nat_event = nat_event;
		this.mac_dst = mac_dst;
		this.comms = comms;
		this.as_src = as_src;
		this.peer_ip_dst = peer_ip_dst;
		this.iface_out = iface_out;
		this.mac_src = mac_src;
		this.src_med = src_med;
		this.peer_as_dst = peer_as_dst;
		this.src_local_pref = src_local_pref;
		this.local_pref = local_pref;
		this.post_nat_port_src = post_nat_port_src;
		this.iface_in = iface_in;
		this.mpls_vpn_rd = mpls_vpn_rd;
		this.ip_src = ip_src;
		this.post_nat_ip_dst = post_nat_ip_dst;
		this.ip_proto = ip_proto;
		this.mask_src = mask_src;
		this.mask_dst = mask_dst;
		this.port_src = port_src;
		this.tos = tos;
		this.sampling_rate = sampling_rate;
		this.post_nat_ip_src = post_nat_ip_src;
		this.post_nat_port_dst = post_nat_port_dst;
		this.stamp_inserted = stamp_inserted;
		this.mpls_label_top = mpls_label_top;
		this.mpls_label_bottom = mpls_label_bottom;
		this.mpls_stack_depth = mpls_stack_depth;
		this.packets = packets;
		this.timestamp_start = timestamp_start;
		this.flows = flows;
	}
	/*
	@Override
    public String toString() {
        //return "Price: " + price.toString() + " time: " + timeStamp;
    	return "ip_src:" + ip_src + " ip_dest:" + ip_dst;
    }
    */
	
	public String toString() {
		  StringBuilder result = new StringBuilder();
		  String newLine = System.getProperty("line.separator");

		  result.append( this.getClass().getName() );
		  result.append( " Object {" );
		  result.append(newLine);

		  //determine fields declared in this class only (no fields of superclass)
		  Field[] fields = this.getClass().getDeclaredFields();

		  //print field names paired with their values
		  for ( Field field : fields  ) {
		    result.append("  ");
		    try {
		      result.append( field.getName() );
		      result.append(": ");
		      //requires access to private field:
		      result.append( field.get(this) );
		    } catch ( IllegalAccessException ex ) {
		      System.out.println(ex);
		    }
		    result.append(newLine);
		  }
		  result.append("}");

		  return result.toString();
		}
	
	
}
