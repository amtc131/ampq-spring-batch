package mx.com.app.spring_batch_amqp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;

//@EnableBatchProcessing
@SpringBootApplication
@EnableBinding(Source.class)
public class App 
{
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
