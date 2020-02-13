package xyz.kail.demo.research.nacos.console;

import com.alibaba.nacos.Nacos;
import com.alibaba.nacos.config.server.service.ServerListService;
import com.alibaba.nacos.core.utils.SystemUtils;
import com.alibaba.nacos.naming.misc.NetUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
@Import(Nacos.class)
@SpringBootApplication
public class NacosConsoleApplication {

    /**
     * classpath 根目录
     */
    private static final String ROOT_PATH = NacosConsoleApplication.class.getResource("/").getPath();

    /**
     * 集群列表
     */
    @Value("${nacos.cluster-addresses}")
    private List<String> nacosClusterAddresses;

    @PostConstruct
    public void init() throws IOException {
        Files.write(
                Paths.get(SystemUtils.getConfFilePath(), "cluster.conf"),
                nacosClusterAddresses,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE
        );
    }

    public static void main(String[] args) {

        System.setProperty(SystemUtils.NACOS_HOME_KEY, ROOT_PATH);

        SpringApplication.run(NacosConsoleApplication.class, args);
    }

}
