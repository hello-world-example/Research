package xyz.kail.demo.research.nacos.cloud.listener;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.cloud.nacos.NacosPropertySourceRepository;
import com.alibaba.cloud.nacos.client.NacosPropertySource;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 监听配置文件的变化
 *
 * @see com.alibaba.nacos.api.config.listener.AbstractSharedListener 订阅 RefreshEvent 无法只获取到变动的信息
 * @see com.alibaba.cloud.nacos.refresh.NacosContextRefresher#registerNacosListener(String, String)  订阅 RefreshEvent 无法只获取到变动的信息
 * ❤❤❤
 * @see com.alibaba.cloud.nacos.NacosConfigAutoConfiguration NacosConfigManager
 */
@Component
public class NacosConfigChangeListener {

    @Resource
    private NacosConfigManager nacosConfigManager;

    @PostConstruct
    public void registerNacosListener() throws NacosException {
        final ConfigService configService = nacosConfigManager.getConfigService();


        final NacosConfigProperties nacosConfigProperties = nacosConfigManager.getNacosConfigProperties();

        final List<NacosConfigProperties.Config> extensionConfigs = nacosConfigProperties.getExtensionConfigs();
        for (NacosConfigProperties.Config config : extensionConfigs) {
            final String group = config.getGroup();
            final String dataId = config.getDataId();
            configService.addListener(dataId, group, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    final NacosPropertySource nacosPropertySource = NacosPropertySourceRepository.getNacosPropertySource(dataId, group);

                    System.out.println("receiveConfigInfo - source: "+nacosPropertySource.getSource());

                    System.out.println("receiveConfigInfo: " + group);
                    System.out.println("receiveConfigInfo: " + dataId);
                    System.out.println("receiveConfigInfo: " + configInfo);
                }
            });
        }
    }

}
