package xyz.kail.demo.research.nacos.boot.discovery;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;

import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * http://127.0.0.1:8848/nacos/v1/cs/configs?
 * dataId=common&
 * group=DEFAULT_GROUP&
 * tenant=4c2c9d7b-2346-44be-8170-61c2b73e86b1
 */
public class JavaDiscoveryMain {

    public static void main(String[] args) throws NacosException, InterruptedException {
        String dataId = "common.properties";
        String group = "DEFAULT_GROUP";

        Properties properties = new Properties();
        properties.put(PropertyKeyConst.NAMESPACE, "4c2c9d7b-2346-44be-8170-61c2b73e86b1");
        properties.put(PropertyKeyConst.SERVER_ADDR, "192.168.1.4");

        final NamingService naming = NacosFactory.createNamingService(properties);

        naming.registerInstance("ms_url", "11.11.11.11", 8888, "TEST1");
        naming.registerInstance("ms_url", "2.2.2.2", 9999, "DEFAULT");

        System.out.println(naming.getAllInstances("ms_url"));
        naming.deregisterInstance("ms_url", "2.2.2.2", 9999, "DEFAULT");
        System.out.println(naming.getAllInstances("ms_url"));

        naming.subscribe("ms_url", new EventListener() {
            @Override
            public void onEvent(Event event) {
                System.out.println(((NamingEvent) event).getServiceName());
                System.out.println(((NamingEvent) event).getInstances());
            }
        });
    }

}
