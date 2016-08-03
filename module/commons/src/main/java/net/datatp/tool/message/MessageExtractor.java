package net.datatp.tool.message;

import net.datatp.util.dataformat.DataSerializer;

public interface MessageExtractor {
  static public MessageExtractor DEFAULT_MESSAGE_EXTRACTOR = new MessageExtractor() {
    @Override
    public Message extract(byte[] messagePayload) {
      return DataSerializer.JSON.fromBytes(messagePayload, Message.class) ;
    }
  };
  public Message extract(byte[] message) ;
}
