package xyz.geekweb.crawler.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.geekweb.crawler.bean.HSGTSumBean;

/**
 * @author jack.luo
 * @date 2020/7/6
 */
public interface HSGTSumRepository extends JpaRepository<HSGTSumBean, Long> {
    HSGTSumBean findBySCode(String sCode);
}
