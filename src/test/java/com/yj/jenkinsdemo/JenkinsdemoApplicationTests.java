package com.yj.jenkinsdemo;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.RequestBuilder;

import java.io.*;
import java.util.Properties;

@SpringBootTest
class JenkinsdemoApplicationTests {
public static Properties prop=null;
	@Test
	public void python() {
		/*PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.execfile("D:\\labs\\hello.py");

		PyFunction pyFunction = interpreter.get("hello", PyFunction.class); // 第一个参数为期望获得的函数（变量）的名字，第二个参数为期望返回的对象类型
		PyObject pyObject = pyFunction.__call__(); // 调用函数

		System.out.println(pyObject);*/
		if(prop==null){
			prop =new Properties();
			ClassPathResource classPathResource=new ClassPathResource("setting.properties");
			try {
				prop.load(new InputStreamReader(classPathResource.getInputStream(),"gbk"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("路径信息：" + prop.getProperty("url.python"));
		int a = 18;
		int b = 23;
		try {
			String[] args = new String[] { "python", prop.getProperty("url.python"), String.valueOf(a), String.valueOf(b) };
			Process proc = Runtime.getRuntime().exec(args);// 执行py文件

			BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
			in.close();
			proc.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
