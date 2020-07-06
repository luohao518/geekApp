package xyz.geekweb.crawler.service;

import xyz.geekweb.crawler.bean.HSGTSumBean;
import xyz.geekweb.crawler.bean.HSGTHdStaBean;

import java.io.IOException;
import java.util.List;

public interface CrawlerEastmoneyService {

    String getToken() throws IOException;

    List<HSGTHdStaBean> getHSGTHdStaJsonData(String token, String stockCode) throws IOException;

    List<HSGTSumBean> getHSGTSumJsonData(String token) throws IOException;
}
