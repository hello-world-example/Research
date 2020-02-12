package xyz.kail.demo.research.nacos.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.PropertySource;

/**
 * curl http://localhost:8080/client/hello?name=kail
 *
 * @see https://github.com/alibaba/spring-cloud-alibaba/wiki/Nacos-config
 *
 */
@PropertySource("app.properties")
@EnableDiscoveryClient
@SpringBootApplication
public class NacosCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosCloudApplication.class, args);
    }

}
