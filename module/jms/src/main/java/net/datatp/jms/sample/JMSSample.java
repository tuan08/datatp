package net.datatp.jms.sample;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "net.datatp.jms.sample" })
@EnableConfigurationProperties
public class JMSSample {
}