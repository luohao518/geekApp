package xyz.geekweb.crawler.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.geekweb.crawler.bean.HSGTHdStaBean;
import xyz.geekweb.crawler.bean.HSGTSumBean;

/**
 * @author jack.luo
 * @date 2020/7/6
 */
public interface HSGTHdStaRepository extends JpaRepository<HSGTHdStaBean, Long> {
    HSGTSumBean findBySCode(String sCode);
}
