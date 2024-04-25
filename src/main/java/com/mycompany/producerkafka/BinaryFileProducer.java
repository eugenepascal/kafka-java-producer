package com.mycompany.producerkafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.Callback;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class BinaryFileProducer {
    public static void main(String[] args) {
        // Path to your file
        String filePath = "/home/eugenepascal/my-workspace/send-binary-file-to-kafka/KafkaCell-CTR.pb.gz";

        // Name of your topic
        String topic = "cdf-consumer";

        // Address of your Kafka broker
        String broker = "172.23.220.242:9092";

        // Create properties for the Kafka producer
        Properties props = new Properties();
        props.put("bootstrap.servers", broker);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

        // Create the Kafka producer
        KafkaProducer<String, byte[]> producer = new KafkaProducer<>(props);

        try {
            // Read all data from the file
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));

            // Create the record to send
            ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, fileBytes);

            // Send the record asynchronously with a callback
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception == null) {
                        // The send was successful
                        System.out.println("File sent successfully. Topic: " + metadata.topic() + " Partition: " + metadata.partition() + " Offset: " + metadata.offset());
                    } else {
                        // There was an error during the send
                        System.err.println("Error sending the file.");
                        exception.printStackTrace();
                    }
                }
            });

        } catch (IOException e) {
            System.err.println("Error reading the file.");
            e.printStackTrace();
        } finally {
            // Explicitly close the producer to ensure all resources are released
            // and all pending messages are sent.
            producer.close();
        }
    }
}
