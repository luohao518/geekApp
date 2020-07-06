package xyz.geekweb.crawler.service;

import xyz.geekweb.crawler.bean.HSGT20GGTJSumBean;
import xyz.geekweb.crawler.bean.HsgthdstaBean;

import java.io.IOException;
import java.util.List;

public interface CrawlerEastmoneyService {

    String getToken() throws IOException;

    List<HsgthdstaBean> getHSGTHDSTAJsonData(String token, String stockCode) throws IOException;

    List<HSGT20GGTJSumBean> getHSGT20_GGTJ_SUMJsonData(String token) throws IOException;
}
