package net.datatp.jms.channel;

import java.io.Serializable;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import net.datatp.channel.ChannelGateway;

public class JMSChannelGateway  implements ChannelGateway {
  private Destination destination ;
  private JmsTemplate template ;	

  public void setDestination(Destination dest) { this.destination = dest ; }

  public void setJmsTemplate(JmsTemplate template) { this.template = template ; }

  public void send(final Serializable object) {
    template.send(destination, new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {
        ObjectMessage omessage = session.createObjectMessage(object) ;
        return omessage;
      }
    });
  }
}