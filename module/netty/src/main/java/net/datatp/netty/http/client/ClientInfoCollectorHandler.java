package net.datatp.netty.http.client;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import net.datatp.netty.http.rest.RestRouteHandler;
import net.datatp.util.ExceptionUtil;
import net.datatp.util.dataformat.DataSerializer;

public class ClientInfoCollectorHandler extends RestRouteHandler {
  
  public ClientInfoCollectorHandler() {
  }
  
  protected Object get(ChannelHandlerContext ctx, FullHttpRequest request) {
    QueryStringDecoder reqDecoder = new QueryStringDecoder(request.getUri()) ;
    List<String> values = reqDecoder.parameters().get("jsonp");
    String jsonp = values.get(0);
    ClientInfo clientInfo = DataSerializer.JSON.fromString(jsonp, ClientInfo.class);
    clientInfo.user.ipAddress = getIpAddress(ctx);
    return onClientInfo(clientInfo, jsonp.length());
  }
  
  protected Object post(ChannelHandlerContext ctx, FullHttpRequest request) {
    try {
      ByteBuf bBuf = request.content();
      byte[] bytes = new byte[bBuf.readableBytes()];
      bBuf.readBytes(bytes);
      ClientInfo clientInfo = DataSerializer.JSON.fromBytes(bytes, ClientInfo.class);
      clientInfo.user.ipAddress = getIpAddress(ctx);
      return onClientInfo(clientInfo, bytes.length);
    } catch(Throwable t) {
      return ExceptionUtil.getStackTrace(t);
    }
  }
  
  protected Object onClientInfo(ClientInfo clientInfo, int dataSize) {
    return "{}";
  }
  
  String getIpAddress(ChannelHandlerContext ctx) {
    InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
    InetAddress inetaddress = socketAddress.getAddress();
    String ipAddress = inetaddress.getHostAddress(); 
    return ipAddress;
  }
} 