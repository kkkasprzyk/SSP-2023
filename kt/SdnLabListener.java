package pl.edu.agh.kt;

import java.util.Collection;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.types.OFPort;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

import net.floodlightcontroller.core.IFloodlightProviderService;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SdnLabListener implements IFloodlightModule, IOFMessageListener {

	protected IFloodlightProviderService floodlightProvider;
	protected static Logger logger;

	@Override
	public String getName() {
		return SdnLabListener.class.getSimpleName();
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	// ///////// Zmiany dodane tutaj ////////
	@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {

		logger.info("************* NEW PACKET IN *************");
		PacketExtractor extractor = new PacketExtractor();
		extractor.checkIfUDPPort5001(cntx);

		OFPacketIn pin = (OFPacketIn) msg;
		OFPort outPort = OFPort.of(0);

		if (sw.getId().getLong() == 1) { // SW 1
			StatisticsCollector.getInstance(sw);
			logger.debug("SWITCH 1");
			if (pin.getInPort() == OFPort.of(1)) { // 1 -> ?
				logger.debug("LOAD ON SWITCH: "
						+ StatisticsCollector.loadLevelOnSw1OnPort2);
				if (StatisticsCollector.loadLevelOnSw1OnPort2 > 0.7) {
					if (extractor.checkIfUDPPort5001(cntx)) {
						outPort = OFPort.of(4);
						logger.debug("ZMIENIAMY TRASE DLA RUCHU PRIORYTETOWEGO");
					} else {
						outPort = OFPort.of(2);
					}
				} else {
					outPort = OFPort.of(2);
				}

			} else if (pin.getInPort() == OFPort.of(4)) { // 4 -> 1
				outPort = OFPort.of(1);
			} else if (pin.getInPort() == OFPort.of(2)) { // 2 -> 1
				outPort = OFPort.of(1);
			}

		} else if (sw.getId().getLong() == 2) { // SW 2
			if (pin.getInPort() == OFPort.of(1)) { // 1 -> 2
				outPort = OFPort.of(2);
			} else if (pin.getInPort() == OFPort.of(2)) { // 2 -> 1
				outPort = OFPort.of(1);
			}

		} else if (sw.getId().getLong() == 3) { // SW 3
			if (pin.getInPort() == OFPort.of(2)) { // 2 -> ?
				if (StatisticsCollector.loadLevelOnSw1OnPort2 > 0.7) {
					if (extractor.checkIfUDPPort5001(cntx)) {
						outPort = OFPort.of(3);
					} else {
						outPort = OFPort.of(1);
					}
				} else {
					outPort = OFPort.of(1);
				}
			} else if (pin.getInPort() == OFPort.of(1)) { // 1 -> 2
				outPort = OFPort.of(2);
			} else if (pin.getInPort() == OFPort.of(3)) { // 3 -> 2
				outPort = OFPort.of(2);
			}

		} else if (sw.getId().getLong() == 4) { // SW 4
			if (pin.getInPort() == OFPort.of(4)) { // 4 -> 1
				outPort = OFPort.of(1);
			} else if (pin.getInPort() == OFPort.of(1)) { // 1 -> 4
				outPort = OFPort.of(4);
			}

		}

		Flows.forwardFirstPacket(sw, pin, outPort);

		Flows.simpleAdd(sw, pin, cntx, outPort);

		return Command.STOP;

	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method tub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider = context
				.getServiceImpl(IFloodlightProviderService.class);
		logger = LoggerFactory.getLogger(SdnLabListener.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		logger.info("******************* START **************************");

	}

}
