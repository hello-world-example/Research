package xyz.kail.demo.research.nacos.boot.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;

import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * http://127.0.0.1:8848/nacos/v1/cs/configs?
 * dataId=common&
 * group=DEFAULT_GROUP&
 * tenant=4c2c9d7b-2346-44be-8170-61c2b73e86b1
 */
public class JavaConfigMain {

    public static void main(String[] args) throws NacosException, InterruptedException {
//        String dataId = "common";
        String dataId = "common.properties";
        String group = "DEFAULT_GROUP";

        Properties properties = new Properties();
        properties.put(PropertyKeyConst.NAMESPACE, "4c2c9d7b-2346-44be-8170-61c2b73e86b1");
        properties.put(PropertyKeyConst.SERVER_ADDR, "192.168.1.4");

        ConfigService configService = NacosFactory.createConfigService(properties);
        String content = configService.getConfig(dataId, group, 5000);
        System.out.println(content);


        configService.addListener(dataId, group, new Listener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                System.out.println("recieve:" + configInfo);
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        });

//        boolean isPublishOk = configService.publishConfig(dataId, group, "content");
//        System.out.println(isPublishOk);
//
//        Thread.sleep(3000);
//        content = configService.getConfig(dataId, group, 5000);
//        System.out.println(content);

//        boolean isRemoveOk = configService.removeConfig(dataId, group);
//        System.out.println(isRemoveOk);
//        Thread.sleep(3000);
//
//        content = configService.getConfig(dataId, group, 5000);
//        System.out.println(content);
        Thread.sleep(300000);

    }

}
