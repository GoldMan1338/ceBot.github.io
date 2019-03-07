package io.github.ceBot;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BotWebCon {
	@RequestMapping("/")
	@ResponseBody
	public String home() {
		return "CE-Bot is locked and loaded brother";
	}
}