package xyz.geekweb.stock;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
import java.util.Map;

/**
 * @author lhao
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "data")
@PropertySource(value = {"classpath:data.properties"})
public class DataProperties {

    private Map<String, String> map;

    //国债逆回购
    private List<String> reverse_bonds;

    //货币基金
    private List<String> monetary_funds;

    //股票
    private List<String> stocks;

    //可转债，元和
    private List<String> stocks_others;

    //分级
    private List<String> fj_funds;
    private List<String> fj_funds_have;

    //FX
    private List<String> fx;

}
