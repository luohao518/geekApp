package xyz.geekweb.stock.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.geekweb.config.DataProperties;
import xyz.geekweb.stock.mq.Sender;
import xyz.geekweb.stock.pojo.savesinastockdata.RealTimeDataPOJO;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author lhao
 * @date 2018/4/25
 * 国债逆回购
 */
@Service
public class GZNHGImpl implements FinanceData {


    private List<RealTimeDataPOJO> data;
    private  List<RealTimeDataPOJO> watchData;

    private  DataProperties dataProperties;

    private  Logger logger = LoggerFactory.getLogger(this.getClass());

    public GZNHGImpl() {

    }

    @Autowired
    public GZNHGImpl(DataProperties dataProperties) {
        this.dataProperties = dataProperties;
    }

    public void fetchData(List<RealTimeDataPOJO> data) {

        final double reverse_bonds_value = Double.parseDouble(this.dataProperties.getMap().get("REVERSE_BONDS_VALUE").split(",")[0]);

        this.data = data.stream().filter(item -> item.getFullCode().startsWith("sh204")).collect(toList());
        this.watchData = data.stream().filter(item -> item.getFullCode().startsWith("sh204") && item.getNow() >= reverse_bonds_value).collect(toList());

    }

    @Override
    public List<RealTimeDataPOJO>  getData(){
        return this.data;
    }

    @Override
    public void sendNotify(Sender sender){
       // sender.sendNotify(this.watchData);
    }

    @Override
    public void printInfo() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("-----------国债逆回购-------------\n");
        this.data.forEach(item -> sb.append(String.format("%5s 当前价[%2.2f] 买入价[%2.2f]%n", item.getName(), item.getNow(), item.getBuy1Price())));
        sb.append("---------------------------------\n");

        logger.info(sb.toString());
    }
}
