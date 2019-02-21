# web-admin后台管理插件
------
基于 [zifish](https://github.com/wayshall/onetwo) 模块的插件机制，使用jquery-easyui编写的简单后台管理，包含了基本的权限管理和菜单功能。   

##  maven依赖
```xml
<repository>
     <id>oss</id>
     <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</repository>   

<dependencies>
    <dependency>
	  <groupId>org.onetwo4j</groupId>
	  <artifactId>onetwo-plugin-web-admin</artifactId>
	  <version>4.7.2-SNAPSHOT</version>
    </dependency>
</dependencies>
```

## 插件截图
![web-admin1](doc/web-admin1.jpg)

## 配置
```yaml

jfish: 
    security: 
        cookie: 
            path: ${server.contextPath} #设置cookies path
    plugin: 
        web-admin: 
            viewMapping: 
                /login: ~/login
                /admin: ~/admin
            captcha: 
                salt: ASDFA@GUythsdgasdf37890fghjkltyhj #验证码盐值，单实例部署时可不用配置，随机生成
                expireInSeconds: 180 #验证码有效期，默认大约三分钟
```