package com.example.demo;

import javax.jms.Session;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import org.springframework.util.ErrorHandler;

import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazon.sqs.javamessaging.ExtendedClientConfiguration;
import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

/**
 * Config jms connection with AWS SQS with S3.
 * <p>
 * During the test, check SQS. Message Available is the total messages in this
 * queue that are not get, yet. Message in Flight is the total messages are read
 * but not completed.
 * 
 * @author Yu-Hua Chang
 * @See https://aws.amazon.com/blogs/developer/using-amazon-sqs-with-spring-boot-and-spring-jms/
 */
@Configuration
public class JmsConfig {

    // Default AWS Region
    public static final Regions DEFAULT_REGION = Regions.US_WEST_2;

    // S3 bucket name (need to create first.)
    public static final String S3_BUCKET_NAME = "mec-edi-data";

    // SQS name (standard queue is enough. need to create first.)
    public static final String QUEUE_NAME = "dev-demo-test";

    // Create basic AWS credential by access key and secret key.
    // This credential should have SQS and S3 permission.
    private AWSCredentialsProvider awsCredentialsProvider() {
        final String accessKey = "<accessKey>";
        final String secretKey = "<secretKey>";
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        return new AWSStaticCredentialsProvider(awsCreds);
    }

    @Bean(name = "awsQueueConnectionFactory")
    public SQSConnectionFactory getSQSConnectionFactory() {

        // Create S3 client
        AmazonS3ClientBuilder s3ClientBuilder = AmazonS3ClientBuilder.standard()
                .withCredentials(awsCredentialsProvider()).withRegion(DEFAULT_REGION);

        AmazonS3 s3 = s3ClientBuilder.build();

        // Set the SQS extended client configuration with large payload support
        // enabled. All message put to the queues will be placed in this S3
        // bucket (root level) with random file name.
        // To prevent messages in different environment being mixed together,
        // we can create different S3 buckets for different environment.
        // Also, it is better to use different IAM roles for different
        // environments.
        ExtendedClientConfiguration extendedClientConfig = new ExtendedClientConfiguration()
                .withLargePayloadSupportEnabled(s3, S3_BUCKET_NAME).withAlwaysThroughS3(true);

        // Create SQS client
        AmazonSQSClientBuilder sqsClientBuilder = AmazonSQSClientBuilder.standard()
                .withCredentials(awsCredentialsProvider()).withRegion(DEFAULT_REGION);

        AmazonSQS sqs = new AmazonSQSExtendedClient(sqsClientBuilder.build(), extendedClientConfig);

        return new SQSConnectionFactory(new ProviderConfiguration(), sqs);
    }

    @Bean(name = "jmsListenerContainerFactory")
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {

        // Create JmsListener
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();

        // Use AWS queue connection factory to connect to AWS queues.
        factory.setConnectionFactory(getSQSConnectionFactory());

        // ActiveMQ support transacted
        // factory.setSessionTransacted(true);
        // factory.setSessionAcknowledgeMode(Session.SESSION_TRANSACTED);

        // SQS does not support transacted
        factory.setDestinationResolver(new DynamicDestinationResolver());
        factory.setConcurrency("3-10");
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);

        // Set error handler (optional)
        factory.setErrorHandler(defaultErrorHandler());

        // Set message converter (optional)
        // Will have error if converter cannot convert the message.
        //factory.setMessageConverter(jacksonJmsMessageConverter());

        return factory;
    }

    // Serialize message content to json using TextMessage
    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Bean
    public ErrorHandler defaultErrorHandler() {
        return new ErrorHandler() {
            @Override
            public void handleError(Throwable throwable) {
                // print error...
                // send email and SMS...
                System.err.println(throwable.getMessage());
            }
        };
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(getSQSConnectionFactory());
    }
}
