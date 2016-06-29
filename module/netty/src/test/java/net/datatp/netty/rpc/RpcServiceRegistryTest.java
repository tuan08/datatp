package net.datatp.netty.rpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;

import com.google.protobuf.BlockingService;
import com.google.protobuf.Descriptors.MethodDescriptor;

import net.datatp.netty.rpc.ping.PingServiceImpl;
import net.datatp.netty.rpc.ping.protocol.Ping;
import net.datatp.netty.rpc.ping.protocol.PingService;
import net.datatp.netty.rpc.ping.protocol.Pong;
import net.datatp.netty.rpc.server.ServerRpcController;
import net.datatp.netty.rpc.server.ServiceDescriptor;
import net.datatp.netty.rpc.server.ServiceRegistry;

import com.google.protobuf.Message;

public class RpcServiceRegistryTest {
  @Test
  public void testRegisterUnregister() throws Exception {
    ServiceRegistry sRegistry = new ServiceRegistry();

    PingService.BlockingInterface bi = new PingServiceImpl() ;
    BlockingService bPingService = PingService.newReflectiveBlockingService(bi);
    sRegistry.register(bPingService);

    Map<String, ServiceDescriptor<?>> result = sRegistry.getServices();
    assertNotNull(result);
    assertEquals(1, result.size());
    ServiceDescriptor<?> sd = sRegistry.getService(bPingService.getDescriptorForType().getName()) ;
    
    MethodDescriptor methodDesc = sd.findMethodByName("ping");
    System.out.println("method: " + methodDesc.getName()) ; 

    Message requestPrototype = sd.getRequestPrototype(methodDesc);
    System.out.println("request prototype class: " + requestPrototype.getClass()) ;
    
    Ping.Builder pingB = Ping.newBuilder();
    pingB.setMessage("Hello Ping") ;
    Message ping = requestPrototype.newBuilderForType().mergeFrom(pingB.build()).build();
    
    ServerRpcController controller = new ServerRpcController() ;
    Pong response = (Pong)sd.invoke(controller, methodDesc, ping) ;
    System.out.println("response: " + response.getMessage()) ;
    
    sRegistry.remove(bPingService);
    result = sRegistry.getServices();
    assertNotNull(result);
    assertEquals(0, result.size());
  }
}
