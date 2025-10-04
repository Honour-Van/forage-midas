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
