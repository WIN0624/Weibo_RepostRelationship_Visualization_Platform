package com.weibo.weibo;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MapperScan(basePackages = {"com.weibo.weibo.mapper"})
@SpringBootApplication
public class WeiboSpringboot192Application {

	public static void main(String[] args) {
		SpringApplication.run(WeiboSpringboot192Application.class, args);
	}

}
