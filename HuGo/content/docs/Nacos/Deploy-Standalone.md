# Nacos 快速部署



> - 访问 http://localhost:8848/nacos/
>- 默认账户 nacos / nacos



## Docker 部署

> https://nacos.io/zh-cn/docs/quick-start-docker.html

```bash
docker pull nacos/nacos-server

docker run -d -p 8848:8848 \
  -e "MODE=standalone" \
  -e "SPRING_DATASOURCE_PLATFORM=mysql" \
  -e "MYSQL_DATABASE_NUM=1" \
  -e "MYSQL_MASTER_SERVICE_HOST=192.168.1.4" \
  -e "MYSQL_MASTER_SERVICE_PORT=3307" \
  -e "MYSQL_MASTER_SERVICE_DB_NAME=nacos" \
  -e "MYSQL_MASTER_SERVICE_USER=root" \
  -e "MYSQL_MASTER_SERVICE_PASSWORD=123456" \
  --name nacos nacos/nacos-server

docker logs nacos -f
```



## IDEA 运行

`git clone https://github.com/alibaba/nacos.git` 之后，选择最新的正式版。

```bash
# 查看 tag， 这里选择 1.1.4
git tag --list


# 从 tag 创建分支
git branch dev_kail_1.1.4 1.1.4 
git checkout dev_kail_1.1.4
```



### /pom.xml 改为 aliyun 仓库

加快编译速度

```xml
<repositories>
    <repository>
        <id>ali</id>
        <url>https://maven.aliyun.com/repository/public</url>
    </repository>
</repositories>
```



### 编译

```bash
mvn -Prelease-nacos -Dmaven.test.skip=true clean install -U  
```



### 数据库配置

- 新建数据库 `nacos`
- 找到 `distribution\conf\nacos-mysql.sql` 执行，初始化表
- 找到 `nacos/console/src/main/resources/application.properties`，新增以下配置

```bash
# 新增
spring.datasource.platform=mysql
db.num=1
db.url.0=jdbc:mysql://127.0.0.1:3307/nacos?characterEncoding=utf8&autoReconnect=true
db.user=root
db.password=123456
# 打开 Prometheus 监控端口，http://localhost:8848/nacos/actuator/prometheus
management.endpoints.web.exposure.include=*
```



### 运行

- 找到 `nacos/console/src/main/java/com/alibaba/nacos/Nacos.java`
- `main` 方法新增 `System.setProperty("nacos.standalone", "true");`
- 运行



## 本地部署

mvn 编译之后，找到执行文件运行

```bash
ls -al distribution/target/

cd distribution/target/nacos-server-1.1.4/nacos/bin

# 不加参数 默认是集群模式
# standalone 默认使用嵌入式数据库
./startup.sh -m standalone

# 访问 http://localhost:8848/nacos/
# 默认账户 nacos / nacos
```

## 参考概念

> [Nacos 概念](https://nacos.io/zh-cn/docs/concepts.html)

- 命名空间 / 租户 (`tenant`)： 可以用来区分环境，如： dev、test、prod 等
- `group` ：可以用来标示一个项目
- `dataId` ：可以用来标示一类配置 或 配置文件（common.properties）
  - Spring Cloud 默认情况下，`dataId` 就是应用名，一个应用一个配置
- `tenant` + `group` + `dataId`  唯一标示一个配置
- :8848/nacos/v1/cs/configs?`tenant`=NamespaceId&`group`=GroupName&`dataId`=ConfigFileName

## Read More



- [控制台手册](https://nacos.io/zh-cn/docs/console-guide.html)
- [Open API 指南](https://nacos.io/zh-cn/docs/open-api.html)
- [Nacos 监控手册](https://nacos.io/zh-cn/docs/monitor-guide.html)