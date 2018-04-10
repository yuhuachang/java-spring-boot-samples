package com.example.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.QueueConnection;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

public class JmsReceiver {

    public static void main(String[] args) {

        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL("tcp://172.28.128.5:61616");

        QueueConnection connection = null;
        try {
            connection = activeMQConnectionFactory.createQueueConnection();

            // start connection
            connection.start();

            // create session
            QueueSession session = (QueueSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // get queue
            ActiveMQQueue queue = new ActiveMQQueue("sampe-input");

            // create receiver
            QueueReceiver receiver = session.createReceiver(queue);

            // create listener
            MessageListener listener = new MessageListener() {

                @Override
                public void onMessage(Message message) {
                    TextMessage msg = (TextMessage) message;
                    try {
                        System.out.println(msg.getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            };
            
            // register the listener
            receiver.setMessageListener(listener);

            // infinity loop to wait and receive message
            for (int i = 0; i < 3; i++) {
                Thread.sleep(500);
            }
            
            // end connection
            connection.close();

        } catch (JMSException | InterruptedException e) {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e1) {
                    // ignore error
                }
            }
            e.printStackTrace();
        }
    }
}