# web-admin后台管理插件
------
基于 [zifish](https://github.com/wayshall/onetwo) 模块的插件机制，编写的后台管理，包含基本的登录（基于spring security）和菜单（权限）管理功能。

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

## 使用Java接口类来实现菜单声明和管理
此插件使用了一个”创新“的技巧，即使用嵌套的Java接口来做组织和构造菜单（权限）树。

```Java
    @PermissionMeta(name="部门管理")
    public static interface DepartmentMgr {
        @PermissionMetaData
        /***
         * 树形列表，对应: /departmentTree.json
         */
        RouteData router = VueRouteDatas.componentView("organMgr/departmentTreeTable");

        /***
         * 列表权限单独列出时，controller注解引用DepartmentMgr.class即可，否则因为DepartmentMgr.List是权限不是菜单，导致没有设置url；
         * 或者把List权限设置为PermissionType.MENU，但此时菜单会多了一个层级
         * @author way
         *
         */
        @PermissionMeta(name = "管理列表", permissionType=PermissionType.FUNCTION)
        public interface List {
        }
        
        /***
         * 组件列表声明为RESOURCE，解决只使用部门组件，不使用管理菜单的问题，因为RESOURCE不查找父菜单
         */
        @PermissionMeta(name = "组件列表", permissionType=PermissionType.RESOURCE)
        public interface TreeList {
        }
        
        @PermissionMeta(name = "新增", permissionType=PermissionType.FUNCTION)
        public interface Create {
        }

        @PermissionMeta(name = "更新", permissionType=PermissionType.FUNCTION)
        public interface Update {
        }

        @PermissionMeta(name = "删除", permissionType=PermissionType.FUNCTION)
        public interface Delete {
        }

        @Deprecated
        @PermissionMeta(name = "企业微信通讯录拉取", permissionType=PermissionType.FUNCTION)
        public interface PullWWContact {
        }
        
    }
```

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
                expireInSeconds: 180 #验证码有效期，默认大约三分钟
```