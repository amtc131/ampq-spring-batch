package mx.com.app.spring_batch_amqp;

import java.net.URL;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.amqp.AmqpItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import mx.com.app.demo.customer.CustomerFieldSetMapper;
import mx.com.app.demo.model.Customer;

@Configuration
@EnableBatchProcessing
public class JobConfig {
	  
    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    
    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory("localhost");
    }
    
    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setRoutingKey(myQueue().getName());
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }


    @Bean
    public Queue myQueue() {
       return new Queue("myqueue");
    }
    


    @Bean
    public FlatFileItemReader<Customer> customerItemReader() {
    	
    	System.out.println("customerItemReader");
    	
    	ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource("customer.csv");
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + "customer.csv");
        }
    	
        FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
        reader.setLinesToSkip(1);
        reader.setResource(new ClassPathResource("customer.csv"));


        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(new String[] { "id", "firstName", "lastName", "birthdate" });


        DefaultLineMapper<Customer> customerLineMapper = new DefaultLineMapper<>();
        customerLineMapper.setLineTokenizer(tokenizer);
        customerLineMapper.setFieldSetMapper(new CustomerFieldSetMapper());
        customerLineMapper.afterPropertiesSet();


        reader.setLineMapper(customerLineMapper);


        return reader;
    }
        
    @Bean
    public AmqpItemWriter<Customer> amqpWriter(){
        AmqpItemWriter<Customer> amqpItemWriter = new AmqpItemWriter<>(this.rabbitTemplate());
        return amqpItemWriter;
    }
        
    @Bean
    public Step step1() {
    	System.out.println("step1 ZZZZZZZ");
        return stepBuilderFactory.get("step1")
                .<Customer, Customer>chunk(10)
                .reader(customerItemReader())
                .writer(amqpWriter())
                .build();
    }
    
    @Bean
    public Job job(){
    	System.out.println("job ZZZZZZZ");
        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .build();
    }
}
