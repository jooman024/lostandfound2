package icia.js.lostandfound.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.DeleteConsumerGroupsOptions;
import org.apache.kafka.clients.admin.DeleteConsumerGroupsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import icia.js.lostandfound.beans.ItemsBean;
import icia.js.lostandfound.repository.userrepo.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Kafka {

    @Autowired
    ProjectUtils util;
    @Autowired
    UserRepository urepo;

    private volatile boolean running = false;
    private Consumer<String, String> kafkaConsumer;

    public void start() {
        running = true;
        log.info("{}","kafka start");
        new Thread(() -> consumer()).start();
    }
    public void stop() {
        running = false;
        kafkaConsumer.wakeup(); 
    }
    public void consumer() {
    	 String groupId = "my-group"; 
         Properties props = new Properties();
         props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
         props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
         props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
         props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
         props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); 
         props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);
         
         kafkaConsumer = new KafkaConsumer<>(props);
         kafkaConsumer.subscribe(Collections.singletonList("Imgtoken"));
         
        try {
            while (running) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(10));
                if (records.isEmpty()) {
                    log.info("No records received from Kafka");
                } else {
                    log.info("Received {} records from Kafka", records.count());
                }
                for (ConsumerRecord<String, String> record : records) {
                    String serviceCode = record.key();
                    String serializedModel = record.value();
                    ItemsBean items = deserialize(serializedModel);
                    if (items.getFileInfo() != null && items.getFileInfo().getFileName() != null) {
                        items = util.imageSave(serviceCode, items);
                        items = ((ItemsBean) urepo.backController(serviceCode, items));
                        log.info("{}","kafka end");
                        stop();
                    } else {
                    	log.info("{}",record.key() + record.value());
                    }
                }
                kafkaConsumer.commitSync(); 
            }
            kafkaConsumer.wakeup(); 
        } catch (WakeupException e) {
        	 log.info("{}","Kafka consumer has been interrupted: "+e.getMessage());
        } finally {
        	try {
                kafkaConsumer.close();
            } catch (Exception e) {
            	 log.info("{}","Failed to close the Kafka consumer: "+e.getMessage());
            }
        }
    }
    public void produceImgAnalysisToken(String serviceCode, ItemsBean item) {
    	 log.info("{}","produceImgAnalysisToken start");
    	start();
    	Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.37:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        String serializedModel = serialize(item);
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        ProducerRecord<String, String> record = new ProducerRecord<>("Imgtoken", serviceCode, serializedModel);
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception != null) {
                	log.info("{}","Failed to send the message to Kafka: " + exception.getMessage());
                } else {
                	log.info("{}","Sent message to topic=%s, partition=%d, offset=%d%n",
                            metadata.topic(), metadata.partition(), metadata.offset());
                }
            }
        });
        producer.flush();
        producer.close();
    }
	    private String serialize(ItemsBean item) {
	        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
	             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
	            oos.writeObject(item);
	            return Base64.getEncoder().encodeToString(baos.toByteArray());
	        } catch (IOException e) {
	            throw new RuntimeException(e);
	        }
	    }

	    private ItemsBean deserialize(String str) {
	        try (ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(str));
	             ObjectInputStream ois = new ObjectInputStream(bais)) {
	            Object obj = ois.readObject();
	            return (ItemsBean) obj;
	        } catch (IOException | ClassNotFoundException e) {
	            throw new RuntimeException(e);
	        }
	    }
}
