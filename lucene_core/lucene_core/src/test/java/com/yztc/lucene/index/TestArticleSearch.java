package com.yztc.lucene.index;

import com.yztc.lucene.domain.Article;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class TestArticleSearch {

    private ArticleSearcher searcher;

    @Before
    public void init(){
        try {
            searcher = new ArticleSearcher();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void query(){
        try {

            List<Article> list =  searcher.queryALLByKey("印度");
            list.forEach(new Consumer<Article>() {
                @Override
                public void accept(Article article) {
                    System.out.println(article);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
