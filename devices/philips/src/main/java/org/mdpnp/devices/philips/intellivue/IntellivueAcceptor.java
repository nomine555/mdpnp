package org.mdpnp.devices.philips.intellivue;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.util.List;

import org.mdpnp.devices.philips.intellivue.association.AssociationAccept;
import org.mdpnp.devices.philips.intellivue.association.impl.AssociationAcceptImpl;
import org.mdpnp.devices.philips.intellivue.attribute.Attribute;
import org.mdpnp.devices.philips.intellivue.attribute.AttributeFactory;
import org.mdpnp.devices.philips.intellivue.connectindication.ConnectIndicationImpl;
import org.mdpnp.devices.philips.intellivue.data.ComponentId;
import org.mdpnp.devices.philips.intellivue.data.ProductionSpecification;
import org.mdpnp.devices.philips.intellivue.data.ProductionSpecificationType;
import org.mdpnp.devices.philips.intellivue.data.ProtocolSupport.ProtocolSupportEntry;
import org.mdpnp.devices.philips.intellivue.data.ProtocolSupport.ProtocolSupportEntry.ApplicationProtocol;
import org.mdpnp.devices.philips.intellivue.data.ProtocolSupport.ProtocolSupportEntry.TransportProtocol;
import org.mdpnp.devices.philips.intellivue.data.SystemModel;
import org.mdpnp.devices.philips.intellivue.dataexport.command.EventReport;
import org.mdpnp.devices.philips.intellivue.dataexport.command.Set;
import org.mdpnp.devices.philips.intellivue.dataexport.command.impl.EventReportImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.event.impl.MdsCreateEventImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.impl.DataExportInvokeImpl;
import org.mdpnp.x73.cmise.CmiseOperation;
import org.mdpnp.x73.mddl.AttributeId;
import org.mdpnp.x73.mddl.OIDType;
import org.mdpnp.x73.mddl.ObjectClass;
import org.mdpnp.x73.rose.RoseInvoke;

public class IntellivueAcceptor extends  Intellivue {
	protected final TaskQueue.Task<Void> beacon = new TaskQueue.TaskImpl<Void>() {
		@Override
		public Void doExecute(TaskQueue queue) {

			try {
				final List<Network.AddressSubnet> address = Network.getBroadcastAddresses(); 
				
				

				
				for(Network.AddressSubnet as : address) {
					ConnectIndicationImpl ci = new ConnectIndicationImpl();
					
					ProtocolSupportEntry e = new ProtocolSupportEntry();
					e.setAppProtocol(ApplicationProtocol.DataOut);
					e.setTransProtocol(TransportProtocol.UDP);
					e.setPortNumber(port);
					e.setOptions(0);
					ci.getProtocolSupport().getList().add(e);
					
					ci.getIpAddressInformation().setInetAddress(as.getLocalAddress());
					Network.prefix(ci.getIpAddressInformation().getSubnetMask(), as.getPrefixLength());
					
					ByteBuffer bb = ByteBuffer.allocate(5000);
					bb.order(ByteOrder.BIG_ENDIAN);
					ci.format(bb);
					byte[] bytes = new byte[bb.position()];
					bb.position(0);
					bb.get(bytes);
					
					DatagramSocket ds = new DatagramSocket();
					DatagramPacket dp = new DatagramPacket(bytes, bytes.length, null, 24005);
					

					
					System.out.println("Transmit to " + as.getInetAddress());
					
					dp.setAddress(as.getInetAddress());
					
					ds.send(dp);
					ds.close();
				}
				
			} catch (SocketException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;
		}		
	};
	@Override
	protected void handle(Set set, boolean confirmed) {
		super.handle(set, confirmed);
		if(confirmed) {
		}
	}
	@Override

	protected synchronized void handle(SocketAddress sockaddr, org.mdpnp.devices.philips.intellivue.association.AssociationConnect message) {
		super.handle(sockaddr, message);
		AssociationAccept acc = new AssociationAcceptImpl();
		log.debug("Sending accept:"+acc);
		send(acc);
		MdsCreateEventImpl m = new MdsCreateEventImpl();
		Attribute<SystemModel> asm = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_ID_MODEL, SystemModel.class);
		Attribute<org.mdpnp.devices.philips.intellivue.data.String> as = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_ID_BED_LABEL, org.mdpnp.devices.philips.intellivue.data.String.class);
		Attribute<ProductionSpecification> ps = AttributeFactory.getAttribute(AttributeId.NOM_ATTR_ID_PROD_SPECN, ProductionSpecification.class);
		
		ProductionSpecification.Entry e = new ProductionSpecification.Entry();
		e.getProdSpec().setString("1234567");
		e.setComponentId(ComponentId.ID_COMP_PRODUCT);
		e.setSpecType(ProductionSpecificationType.SERIAL_NUMBER);
		ps.getValue().getList().add(e);
		asm.getValue().setManufacturer("MD PNP");
		asm.getValue().setModelNumber("ICE TEST ONE");
		m.getAttributes().add(asm);
		m.getAttributes().add(as);
		m.getAttributes().add(ps);
		
		EventReport er = new EventReportImpl();
		er.setEvent(m);
		er.setEventType(OIDType.lookup(ObjectClass.NOM_NOTI_MDS_CREAT.asInt()));
		
		RoseInvoke der = new DataExportInvokeImpl();
		der.setCommandType(CmiseOperation.EventReport);
		der.setCommand(er);
		
		send(der);
	};
	public IntellivueAcceptor(NetworkLoop networkLoop) throws IOException {
		this(networkLoop, DEFAULT_UNICAST_PORT);
	}
	public IntellivueAcceptor(NetworkLoop networkLoop, int port) throws IOException {
		super(networkLoop);
		beacon.setInterval(10000L);
		this.port = port;
		
	}
	
	public void accept() throws IOException {
		networkLoop.add(new TaskQueue.TaskImpl<Void>() {
			@Override
			public Void doExecute(TaskQueue queue) {
				try {
					DatagramChannel channel = DatagramChannel.open();
					channel.configureBlocking(false);
					channel.socket().setReuseAddress(true);
					channel.socket().bind(new InetSocketAddress(port));
					register(channel);
					networkLoop.add(beacon);
					
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
				return null;
			}
			
		});
		
	}
	protected final int port;

	public static void main(String[] args) throws IOException {
		final int port = args.length > 0 ? Integer.parseInt(args[0]) : Intellivue.DEFAULT_UNICAST_PORT;
		final NetworkLoop networkLoop = new NetworkLoop();
		final IntellivueAcceptor ia = new IntellivueAcceptor(networkLoop, port);
		ia.accept();
		networkLoop.runLoop();
		
	}
}
