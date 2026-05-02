package com.freehire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * FreeHire 智能招聘系统
 * 
 * @author FreeHire
 * @since 1.0.0
 */
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class FreeHireApplication {

    public static void main(String[] args) {
        SpringApplication.run(FreeHireApplication.class, args);
        System.out.println("""
            
            ███████╗██████╗ ███████╗███████╗██╗  ██╗██╗██████╗ ███████╗
            ██╔════╝██╔══██╗██╔════╝██╔════╝██║  ██║██║██╔══██╗██╔════╝
            █████╗  ██████╔╝█████╗  █████╗  ███████║██║██████╔╝█████╗  
            ██╔══╝  ██╔══██╗██╔══╝  ██╔══╝  ██╔══██║██║██╔══██╗██╔══╝  
            ██║     ██║  ██║███████╗███████╗██║  ██║██║██║  ██║███████╗
            ╚═╝     ╚═╝  ╚═╝╚══════╝╚══════╝╚═╝  ╚═╝╚═╝╚═╝  ╚═╝╚══════╝
            
            FreeHire 智能招聘系统启动成功!
            API文档: http://localhost:8080/doc.html
            """);
    }
}

