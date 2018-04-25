package xyz.geekweb.stock;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import xyz.geekweb.stock.impl.*;
import xyz.geekweb.stock.pojo.json.JsonRootBean;
import xyz.geekweb.stock.pojo.json.Rows;
import xyz.geekweb.stock.savesinastockdata.RealTimeData;
import xyz.geekweb.stock.savesinastockdata.RealTimeDataPOJO;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lhao
 */
@Service
public class SearchFinanceData {

    private static final String URL = "https://www.jisilu.cn/data/sfnew/funda_list/";
    /**
     * 国债逆回购
     */
    private final static String[] REVERSE_BONDS = {"sh204001", "sh204002", "sh204003", "sh204004", "sh204007", "sh204014"};
    /**
     * 货币基金
     */
    private final static String[] MONETARY_FUNDS = {"sh511990", "sh511660", "sh511810", "sh511690", "sh511900"};
    /**
     * 股票
     */
    private final static String[] STOCKS = {"sh600185", "sh600448", "sh601098", "sz000066", "sz002570", "sh601828", "sh601928", "sh601369", "sz000417"};
    /**
     * 可转债，元和
     */
    private final static String[] STOCKS_OTHERS = {"sh132003", "sh110030", "sh505888"};

    private Logger logger = LoggerFactory.getLogger(SearchFinanceData.class);
    private Map<FinanceTypeEnum, FinanceData> lstFinanceData;

    public Map<FinanceTypeEnum, FinanceData> getLstFinanceData() {
        return lstFinanceData;
    }

    /**
     * getALLDataForOutput
     * @return str
     */
    public String getALLDataForOutput() {
        logger.debug("execute getALLDataForOutput()");

        StringBuilder sb = new StringBuilder();
        if (this.lstFinanceData == null) {
            this.fillALLData();
        }
        lstFinanceData.forEach((k, v) -> sb.append(v.print()));
        return sb.toString();
    }

    /**
     * get all data
     */
    public void fillALLData() {

        logger.debug("execute fillALLData()");
        final List<RealTimeDataPOJO> realTimeDataPOJOS = fetchSinaData();

        final List<Rows> rows = fetchJSLData();

        this.lstFinanceData = new HashMap<>(10);
        FinanceData<RealTimeDataPOJO> monetaryBondImpl = new GZNHGImpl();
        monetaryBondImpl.initData(realTimeDataPOJOS);
        this.lstFinanceData.put(FinanceTypeEnum.GZNHG, monetaryBondImpl);

        FinanceData<RealTimeDataPOJO> reverseBondImpl = new HBFundImpl();
        reverseBondImpl.initData(realTimeDataPOJOS);
        this.lstFinanceData.put(FinanceTypeEnum.HB_FUND, reverseBondImpl);

        FinanceData<RealTimeDataPOJO> convertibleBondImpl = new KZZImpl();
        convertibleBondImpl.initData(realTimeDataPOJOS);
        this.lstFinanceData.put(FinanceTypeEnum.KZZ, convertibleBondImpl);

        FinanceData<RealTimeDataPOJO> stockImpl = new StockImpl();
        stockImpl.initData(realTimeDataPOJOS);
        this.lstFinanceData.put(FinanceTypeEnum.STOCK, stockImpl);

        FinanceData<Rows> fjFundImpl = new FjFundImpl();
        fjFundImpl.initData(rows);
        this.lstFinanceData.put(FinanceTypeEnum.FJ_FUND, fjFundImpl);
    }

    private List<RealTimeDataPOJO> fetchSinaData() {
        String[] codes = ArrayUtils.addAll((ArrayUtils.addAll(ArrayUtils.addAll(REVERSE_BONDS, MONETARY_FUNDS), STOCKS_OTHERS)), STOCKS);
        return RealTimeData.getRealTimeDataObjects(codes);
    }

    private List<Rows> fetchJSLData() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException("服务器端错误: ", e);
        }
        if (!response.isSuccessful()) {
            throw new RuntimeException("服务器端错误: " + response.message());
        }
        JsonRootBean jsonData;
        try {
            jsonData = new Gson().fromJson(response.body().string(), JsonRootBean.class);
        } catch (IOException e) {
            throw new RuntimeException("服务器端错误: ", e);
        }
        return jsonData.getRows();
    }
}
