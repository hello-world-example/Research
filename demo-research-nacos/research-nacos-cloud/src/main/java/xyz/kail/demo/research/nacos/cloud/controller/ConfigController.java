package xyz.kail.demo.research.nacos.cloud.controller;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 必须叫：bootstrap.properties
 * 不能叫：application.properties
 * <p>
 * 原理： https://github.com/alibaba/spring-cloud-alibaba/blob/master/spring-cloud-alibaba-examples/nacos-example/nacos-config-example/readme-zh.md#%E5%8E%9F%E7%90%86
 */
@Slf4j
@RefreshScope // 修改配置 useLocalCache 会自动改变
@RestController
@RequestMapping("/config")
public class ConfigController {

    @NacosInjected
    private ConfigService configService;

    private String appInfo;

    /**
     * 监听配置变化
     */
    @Value("${app.info}")
    public void setAppInfo(String appInfo) {
        this.appInfo = appInfo;
        System.out.println("setAppInfo：" + appInfo);
    }

    @Value("${app.info.local}")
    private String appInfoLocal;

    @Value("${spring.datasource.url}")
    private String springDatasourceUrl;


    /**
     * http://localhost:8080/config/get
     */
    @RequestMapping("/get")
    public Map<String, Object> get() {
        Map<String, Object> configs = new HashMap<>();
        configs.put("appInfo", appInfo);
        configs.put("appInfoLocal", appInfoLocal);
        configs.put("springDatasourceUrl", springDatasourceUrl);
        return configs;
    }

    /**
     * FIXME groupId 不支持 ${}
     * FIXME 没有效果
     */
    @NacosConfigListener(dataId = "app")
    public void onAppChange(String config) {
        System.out.println("change: " + config);
    }

    /**
     * FIXME configService 不在 容器中
     */
    // @PostConstruct
    public void init() throws NacosException {
        configService.addListener("app.properties", "research-nacos-cloud", new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                System.out.println("receiveConfigInfo: " + configInfo);
            }
        });
    }
}
