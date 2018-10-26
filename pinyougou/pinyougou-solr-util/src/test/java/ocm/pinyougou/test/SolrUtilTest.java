package ocm.pinyougou.test;

import com.pinyougou.solr.SolrUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SolrUtilTest {

    private SolrUtil solrUtil;

    @Before
    public void init(){
        ApplicationContext act= new ClassPathXmlApplicationContext("classpath:spring/spring-solr.xml");

         //获取SolrUtil实例
        solrUtil=act.getBean(SolrUtil.class);
    }

    /**
     *批量导入
     */
    @Test
    public void testBatchAdd(){
        solrUtil.batchAdd();
    }

    /**
     * 删除所有
     */
    @Test
    public void testDeleteAll(){
        solrUtil.deleteAll();
    }
}
