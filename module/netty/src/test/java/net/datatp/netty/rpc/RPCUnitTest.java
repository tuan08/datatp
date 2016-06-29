package net.datatp.netty.rpc;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.google.protobuf.ByteString;

import net.datatp.netty.rpc.client.RPCClient;
import net.datatp.netty.rpc.protocol.Request;
import net.datatp.netty.rpc.protocol.Response;
import net.datatp.netty.rpc.server.RPCServer;

public class RPCUnitTest {
  static protected RPCServer server  ;
  static protected RPCClient client ;
  
  @BeforeClass
  static public void setup() throws Exception {
    server = new RPCServer() ;
    server.startAsDeamon(); 
    
    client = new RPCClient() ;
    
//    Request.Builder requestB = Request.newBuilder();
//    requestB.setServiceId("AService") ;
//    requestB.setMethodId("AMethod") ;
//    requestB.setParams(ByteString.EMPTY) ;
//    Response response = client.call(requestB.build()) ;
//    System.out.format("Server RPCMessage = " + new String(response.getResult().toByteArray()));
  }
  
  @AfterClass
  static public void teardown() {
    client.close();
    server.shutdown();
  }
}
