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

## Task 3

通过完善jpa的封装，增加updateBalance的功能，在消费时编写简单的金额转移逻辑即可。

可以使用debug日志，打印完整的交易过程。也可以最终输出，从而找到目标用户的余额。

## Task 4

为了通过Task 4，方法和task3一样，只需要最后输出时查询另一个人的结果就可以了。

在IDEA直接运行transaction-incentive-api.jar，会启动一个服务，虽然有很多condition条件不满足，但不影响serving，通过post请求测试可以返回结果的。
```shell
curl -X POST http://127.0.0.1:8080/incentive \
  -H 'Content-Type: application/json' \
  -d '{"senderId":3,"recipientId":2,"amount":164.17}'
```

经过几个尝试，都会返回`{"amount":0.0}`。所以其实调用与否都无所谓。

## Task 5

