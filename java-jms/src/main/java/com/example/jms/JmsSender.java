package com.example.jms;

import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

public class JmsSender {

    // admin page: http://172.28.128.5:8161/admin/ (admin/admin)

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

            // create sender
            QueueSender sender = session.createSender(queue);

            // create message
            TextMessage message = session.createTextMessage("Hello World!");

            // send message
            sender.send(message);

            // end connection
            connection.close();

        } catch (JMSException e) {
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