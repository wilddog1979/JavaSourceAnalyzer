package org.eaSTars.javasourcer;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class JavaSourcer 
{
	public static void main( String[] args )
	{
		new SpringApplicationBuilder()
		.headless(false)
		.bannerMode(Mode.OFF)
		.sources(JavaSourcer.class)
		.run(args);
	}
}
