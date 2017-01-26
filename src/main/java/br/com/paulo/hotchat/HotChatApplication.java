package br.com.paulo.hotchat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class HotChatApplication {

	private static final Logger log = LoggerFactory.getLogger(HotChatApplication.class);
	
    public static void main(String[] args) throws Throwable {
        ConfigurableApplicationContext ctx = SpringApplication.run(HotChatApplication.class, args);
        
        log.info("Contexto: {}", ctx.getApplicationName());
    }
}
