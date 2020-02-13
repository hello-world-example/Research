# 集群部署

## 快速部署

单机 [快速部署]({{< relref "/docs/Nacos/Deploy-Standalone.md" >}}) 的基础上，在 classpath 根目录新增 `conf/cluster.conf` 文件，内容如下：

```bash
# ip:port（同一个机器上启动多台）
192.168.1.4:18848
192.168.1.4:28848
192.168.1.4:38848
```

启动命令去掉 `-m standalone` （ `System.setProperty("nacos.standalone", "false");` ）即可：

```bash
./startup.sh
```

## 自定义启动方式

### pom 依赖

官方 `nacos-console` 没有发布到中央仓库，可以自行打包到私服 或 使用下面这个第三方的依赖

```xml
<dependency>
    <groupId>com.xkcoding.nacos</groupId>
    <artifactId>nacos-console</artifactId>
    <version>1.1.4</version>
</dependency>
```

### application.properties

```bash
# 集群地址
nacos.cluster-addresses=192.168.1.4:18848,192.168.1.4:28848,192.168.1.4:38848
```

### 启动类

```java
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
```

## 其它

- Nacos 有 **配置管理** 和 **服务发现** 两个大功能，模块也相对独立，独立到对 **集群地址** 列表的获取都不太一样
  - Config： `com.alibaba.nacos.config.server.service.ServerListService`
    - `#init` > `#getApacheServerList` ，先读取  `conf/cluster.conf` 文件，**如果读不到，再通过 `addressServerUrl` 远程读取，并定时刷新 `serverList`**
  - Naming(Discovery)：`com.alibaba.nacos.naming.cluster.ServerListManager`
    - `#init` > `#refreshServerList`，先读取  `conf/cluster.conf` 文件，**如果读不到，在通过 `naming_self_service_cluster_ips` 环境变量读取，并定时刷新 `servers`**
  - 对相同资源的处理方式不太统一，感觉有待改进
- 另一个感觉比较挫的地方在于，代码中获取参数配置的地方，大量使用 `System.getProperty`、`System.getenv` ，需要通过设置 `-D` **系统参数** 或 **环境变量** 来获取配置，还有一些写死的常量，但是整个项目又是基于 Spring Boot 开发，`Environment` 也已经注入到类中，不知道为什么不使用 `Environment` 来获取配置。
- console 模块总体感觉不够 Boot，有点偏 Ops



## Read More

- [集群部署说明](https://nacos.io/zh-cn/docs/cluster-mode-quick-start.html)
- Github Issues
  - [nacos 集群采用nginx做负载，nginx监听80端口，client默认连接8848端口](https://github.com/alibaba/nacos/issues/2119)