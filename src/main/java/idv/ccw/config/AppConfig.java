package idv.ccw.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass=true)
//@ComponentScan(basePackages = "idv.ccw.*")
public class AppConfig {
}