package com.mudra.loomdemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
	
	@GetMapping(path = "/demo")
	public String getThreadInfo() {
		return Thread.currentThread().toString();
	}

}
