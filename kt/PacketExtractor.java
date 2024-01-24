package pl.edu.agh.kt;

import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.packet.ARP;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.packet.UDP;

public class PacketExtractor {
	private static final Logger logger = LoggerFactory
			.getLogger(PacketExtractor.class);
	private FloodlightContext cntx;
	protected IFloodlightProviderService floodlightProvider;
	private Ethernet eth;
	private IPv4 ipv4;
	private ARP arp;
	private TCP tcp;
	private UDP udp;
	private OFMessage msg;

	public PacketExtractor(FloodlightContext cntx, OFMessage msg) {
		this.cntx = cntx;
		this.msg = msg;

	}

	public PacketExtractor() {

	}


	public boolean checkIfUDPPort5001(FloodlightContext cntx) {
		this.cntx = cntx;
		return extractEth();
	}


	public boolean extractEth() {
		eth = IFloodlightProviderService.bcStore.get(cntx,
				IFloodlightProviderService.CONTEXT_PI_PAYLOAD);

		if (eth.getEtherType() == EthType.ARP) {
			arp = (ARP) eth.getPayload();
		}

		if (eth.getEtherType() == EthType.IPv4) {

			ipv4 = (IPv4) eth.getPayload();
			if (IpProtocol.UDP == ipv4.getProtocol()){
				udp = (UDP) ipv4.getPayload();
				if(udp.getDestinationPort().getPort() == 5001){
					return true;
				}
				
			}
			
		}

		return false;
	}

	public void extractArp() {

	}

	public void extractIp() {

	}

}
