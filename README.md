
# 简介

#### 1.快速开始

- 引入esRestPlus以及starter的依赖

- 新建bootstrap.properties，或者跟配置中心结合使用，配置es-plus.nodes=xx.xxx.xxx.xx:9200

- ![image-20200909004306346](C:\Users\rui.huang.HL\AppData\Roaming\Typora\typora-user-images\image-20200909004306346.png)

  启动类中加入@EsMappingScan注解扫对应的mapper包

- ![image-20200909004446970](C:\Users\rui.huang.HL\AppData\Roaming\Typora\typora-user-images\image-20200909004446970.png)

  对应包中写抽象类通过@EsMapping注解绑定index以及type，继承BaseElasticSearchService，泛型中为实体类对象即可开始使用

- ![image-20200909004634413](C:\Users\rui.huang.HL\AppData\Roaming\Typora\typora-user-images\image-20200909004634413.png)

  模板类中支持常用的CURD

- ![image-20200909004759734](C:\Users\rui.huang.HL\AppData\Roaming\Typora\typora-user-images\image-20200909004759734.png)

  通过EsCriteria对象可快速构建条件，selectByCriteria为聚合方法，返回对象为聚合结果（参考es聚合api，嵌套聚合会返回嵌套对象）

#### 2.版本说明

​		目前只支持6.x版本的es，如需要7.x版可自行修改源码