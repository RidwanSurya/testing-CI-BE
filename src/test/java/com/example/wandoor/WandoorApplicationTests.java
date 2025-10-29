package com.example.wandoor;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "spring.flyway.enabled=false")
class WandoorApplicationTests {

	@Test
	void contextLoads() {
	}

}
