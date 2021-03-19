package com.tyt.qiuzhi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

@SpringBootTest
class QiuzhiApplicationTests {

	@Test
	void contextLoads() {
	}


	@Autowired
	DataSource dataSource;

	@Test
	void testDataSource(){
		System.out.println("数据源信息："+dataSource);
	}

}
