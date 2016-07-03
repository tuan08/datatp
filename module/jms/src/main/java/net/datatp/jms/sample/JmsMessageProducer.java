package net.datatp.jms.sample;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

/**
 * Generates JMS messages
 * @author David Winterfeldt
 */
@Component
public class JmsMessageProducer {
  private static final Logger logger = LoggerFactory.getLogger(JmsMessageProducer.class);
  protected static final String MESSAGE_COUNT = "messageCount";

  @Autowired
  private JmsTemplate template = null;
  private int messageCount = 10;

  /**
   * Generates JMS messages
   */
  @PostConstruct
  public void generateMessages() throws JMSException {
    for (int i = 0; i < messageCount; i++) {
      final int index = i;
      final String text = "Message frequency is " + i + ".";

      template.send(new MessageCreator() {
        public Message createMessage(Session session) throws JMSException {
          TextMessage message = session.createTextMessage(text);
          message.setIntProperty(MESSAGE_COUNT, index);
          logger.info("Sending message: " + text);
          return message;
        }
      });
    }
  }
}