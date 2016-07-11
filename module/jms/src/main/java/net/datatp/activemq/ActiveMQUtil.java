package net.datatp.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Session;

public class ActiveMQUtil {
  static public Destination createQueue(ConnectionFactory jmsCF, String name) throws Exception {
    // Create a Connection
    Connection connection = jmsCF.createConnection();
    connection.start();

    // Create a Session
    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

    // Create the destination (Topic or Queue)
    Destination destination = session.createQueue(name);

    // Create a MessageConsumer from the Session to the Topic or Queue
    session.close();
    connection.close();
    return destination;
  }
}
