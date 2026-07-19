package com.example.taskflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TaskflowApplication {

	public static void main(String[] args) {
	ApplicationContext applicationContext = SpringApplication.run(TaskflowApplication.class, args);

	Dev obj = applicationContext.getBean(Dev.class);
	obj.code();


	}

}
