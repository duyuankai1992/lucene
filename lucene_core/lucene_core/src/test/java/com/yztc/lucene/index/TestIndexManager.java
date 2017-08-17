package com.yztc.lucene.index;

import com.yztc.lucene.domain.Article;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TestIndexManager {

    private IndexManager manager;

    @Before
    public void setup(){
        try {
            manager = new IndexManager();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addArticle(){
        Article article1 = new Article();
        article1.setId("1001");
        article1.setAuthor("王富贵");
        article1.setTitle("印度阿三");
        article1.setContext("印度一直视中国为潜在的威胁,中印也遗留了不少历史问题,比如边界问题等等,在1962年,中印还为边界问题大打了一仗,印度为此差点亡国,要不是因为国际压力");
        try {
            manager.createIndex(Arrays.asList(article1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteIndex() throws  Exception{
        manager.deleteIndex("印度");
    }

}
