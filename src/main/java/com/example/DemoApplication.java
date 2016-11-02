package com.example;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.transformer.FileToStringTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

@SpringBootApplication
@EnableAutoConfiguration(exclude=DataSourceAutoConfiguration.class)
public class DemoApplication {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final String FILE_PATH = "/home/ryan/int/input/";
	public static final String TEXT_OUTPUT_CHANNEL = "textOutputChannel";
	public static final String FILE_INPUT_CHANNEL  = "fileInputChannel";
	
	
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	
	@Bean
	@InboundChannelAdapter(poller=@Poller(fixedRate="1000"), channel=FILE_INPUT_CHANNEL)
	public MessageSource<File> fileInputChannelAdapter(){
		FileReadingMessageSource result = new FileReadingMessageSource();
		result.setDirectory(new File(FILE_PATH));
		return result;
	} 
	
	@Bean(name=FILE_INPUT_CHANNEL)
	public MessageChannel fileInputChannel(){
		return new DirectChannel();
	}

	@Bean
	@Transformer(inputChannel=FILE_INPUT_CHANNEL, outputChannel=TEXT_OUTPUT_CHANNEL)
	public FileToStringTransformer fileToStringTransformer(){
		return new FileToStringTransformer();
	}
	
	@Bean(name=TEXT_OUTPUT_CHANNEL)
	public MessageChannel textOutputChannel(){
		return new DirectChannel();
	}
	
	@ServiceActivator (inputChannel=TEXT_OUTPUT_CHANNEL)
	public void processText(Message<String> message){
		logger.info(message.getPayload());
	}

}
