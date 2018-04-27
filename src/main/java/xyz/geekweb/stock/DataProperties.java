package xyz.geekweb.stock;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "etl")
@PropertySource(value ={"classpath:data.properties"})
public class DataProperties {

    private Map<String,String> map;

    private List<String> list;

    private Map<String,String> prison;//监狱
}
