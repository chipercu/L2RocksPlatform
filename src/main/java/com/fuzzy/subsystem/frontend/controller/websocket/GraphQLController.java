package com.fuzzy.subsystem.frontend.controller.websocket;

import com.infomaximum.main.Subsystems;
import com.infomaximum.network.mvc.ResponseEntity;
import com.infomaximum.network.mvc.anotation.Controller;
import com.infomaximum.network.mvc.anotation.ControllerAction;
import com.infomaximum.network.protocol.standard.packet.TargetPacket;
import com.infomaximum.network.protocol.standard.session.StandardTransportSession;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;

import java.util.concurrent.CompletableFuture;

/**
 * Created with IntelliJ IDEA.
 * User: kris
 * Date: 18.04.13
 * Time: 19:22
 */
@Controller("graphql")
public class GraphQLController {

	@ControllerAction("exec")
	public CompletableFuture<ResponseEntity> exec(StandardTransportSession transportSession, TargetPacket targetPacket) {
		FrontendSubsystem frontEndSubSystem = Subsystems.getInstance().getCluster().getAnyLocalComponent(FrontendSubsystem.class);

		return frontEndSubSystem.getControllers().websocket.graphQLController
				.exec(transportSession, targetPacket);
	}

}
