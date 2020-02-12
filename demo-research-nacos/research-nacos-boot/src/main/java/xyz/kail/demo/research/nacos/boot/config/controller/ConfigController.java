package xyz.kail.demo.research.nacos.boot.config.controller;

import com.alibaba.boot.nacos.common.PropertiesUtils;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.alibaba.nacos.api.config.annotation.NacosProperty;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.alibaba.nacos.spring.context.annotation.config.NacosRefresh;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Properties;
import java.util.concurrent.Executor;

@Controller
@RequestMapping("/config")
@NacosPropertySource(dataId = "common")
public class ConfigController {

    // @Resource 不行
    @NacosInjected
    private ConfigService configService;

    @NacosValue(value = "${content}", autoRefreshed = true)
    private String content;

    @ResponseBody
    @GetMapping("/get")
    public String get() {
        return content;
    }

    /**
     * 2. 监听配置变化
     */
    @NacosConfigListener(dataId = "common")
    public void onCommonChange(String config) {
        System.out.println(config);
    }

    /**
     * 1. 监听配置变化
     */
    @PostConstruct
    public void init() throws NacosException {
        configService.addListener("common", null, new Listener() {
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