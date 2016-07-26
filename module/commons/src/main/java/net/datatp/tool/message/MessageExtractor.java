package net.datatp.tool.message;

import net.datatp.util.json.JSONSerializer;

public interface MessageExtractor {
  static public MessageExtractor DEFAULT_MESSAGE_EXTRACTOR = new MessageExtractor() {
    @Override
    public Message extract(byte[] messagePayload) {
      return JSONSerializer.INSTANCE.fromBytes(messagePayload, Message.class) ;
    }
  };
  public Message extract(byte[] message) ;
}
