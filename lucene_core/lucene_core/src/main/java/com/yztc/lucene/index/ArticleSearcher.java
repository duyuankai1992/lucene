package com.yztc.lucene.index;

import com.yztc.lucene.domain.Article;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ArticleSearcher {
    //查询索引的路径
    private final String INDEX_DIR = "./index_dir";
    private Analyzer analyzer;//标准分词器
    private IndexSearcher indexSearcher;//查询分析类
    private IndexReader indexReader;//索引读取类
    private Directory directory;

    public ArticleSearcher() throws IOException {
        analyzer = new SmartChineseAnalyzer();
        directory = FSDirectory.open(FileSystems.getDefault().getPath(INDEX_DIR));
        indexReader = DirectoryReader.open(directory);
        indexSearcher = new IndexSearcher(indexReader);
        //indexSearcher = new IndexSearcher(indexReader, Executors.newFixedThreadPool(4));
    }

    public String highLight(Query query,String fieldName, String text ) throws Exception{
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<font color='red'>","</font>");
        QueryScorer queryScorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(formatter,queryScorer);
        SimpleFragmenter fragmenter = new SimpleFragmenter();
        fragmenter.setFragmentSize(200);
        highlighter.setTextFragmenter(fragmenter);
        String bestString = highlighter.getBestFragment(analyzer,fieldName,text);
        if (bestString==null){
            return text;
        }else{
            return  bestString;
        }
    }

    public void wildcardQuery() throws  Exception{
        WildcardQuery query = new WildcardQuery(new Term("titlle","java"));
        TopDocs topDocs = indexSearcher.search(query, 200);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        //输出结果
    }
    public void multiPhraseQuery(){
        MultiPhraseQuery query = new MultiPhraseQuery();
        query.add(new Term("title","java"));
        query.setSlop(10);//坡度

    }
    /**
     * 多域查询,所查询的关键字可以出现在多个域
     *
     * @param key
     * @return
     */
    public List<Article> queryALLByKey(String key) throws Exception {
        List<Article> list = new ArrayList<>();
        MultiFieldQueryParser queryParser =
                new MultiFieldQueryParser(new String[]{"title", "content"}, analyzer);
        queryParser.setDefaultOperator(QueryParser.Operator.OR);
        Query query = queryParser.parse(key);
        //topDocs,置顶的文档
        TopDocs topDocs = indexSearcher.search(query, 200);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docID = scoreDoc.doc;//提取每一个文档
            Document document = indexSearcher.doc(docID);
            Article article = new Article();
            article.setId(document.get("id"));
            article.setContext(highLight(query,"content",document.get("content")));
            article.setTitle(highLight(query,"title",document.get("title")));
            article.setAuthor(document.get("author"));
            list.add(article);
        }
        return list;
    }
}
