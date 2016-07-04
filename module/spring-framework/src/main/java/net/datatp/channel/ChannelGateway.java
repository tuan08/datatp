package net.datatp.channel;

import java.io.Serializable;

public interface ChannelGateway {
  public void send(final Serializable object) ;
}