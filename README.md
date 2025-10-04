# Midas
Project repo for the JPMC Advanced Software Engineering Forage program


## Task 1

增加依赖将系统运行起来

增加maven依赖，注意的要点是，spring-boot starter相关的沿用了parent版本，所以不需要指定version字段。具体见提交。

增加对应依赖后，启动TakeOneTests，发现KafkaProducer这个Bean不能启动。
```shell
Caused by: java.lang.IllegalArgumentException: Could not resolve placeholder 'general.kafka-topic' in value "${general.kafka-topic}"
```

增加配置resources/application.properties即可。

## Task 2

对应的测试函数报错，说明类型转换有问题，需要对KafkaProducer做改进：
```shell
org.apache.kafka.common.errors.SerializationException: Can't convert value of class com.jpmc.midascore.foundation.Transaction to class org.apache.kafka.common.serialization.StringSerializer specified in value.serializer
```

为解决这个问题，对kafkaTemplate做了重载。但是Config中重载会导致目标地址在Config阶段生成，先于Context初始化。动态端口无法正确的和这个预先写死的9092对接：
```shell
Embedded Kafka broker running at: localhost:50231
```

所以需要使用Value注入

