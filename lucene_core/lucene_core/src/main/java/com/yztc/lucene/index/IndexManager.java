package com.yztc.lucene.index;

import com.yztc.lucene.domain.Article;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * 负责创建索引的类
 */
public class IndexManager {

    //表示索引的目录
    private final String INDEX_DIR = "./index_dir";
    //创建索引的类
    private IndexWriter indexWriter;

    private Directory directory;

    private IndexWriterConfig config;

    private Analyzer analyzer;//标准分词器

    private Path path;

    public IndexManager() throws IOException {
        path = FileSystems.getDefault().getPath(INDEX_DIR);
        directory = FSDirectory.open(path);//打开索引的目录
        //analyzer = new StandardAnalyzer();
        analyzer = new SmartChineseAnalyzer();
        config = new IndexWriterConfig(analyzer);//分词器
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        indexWriter = new IndexWriter(directory, config);

    }

    /**
     * 创建索引
     *
     * @param articles
     */
    public void createIndex(List<Article> articles) throws IOException {
        if (null != articles && !articles.isEmpty()) {
            for (Article article : articles) {
                Document document = new Document();
                document.add(new StringField("id", article.getId(), Field.Store.YES));
                document.add(new StringField("title", article.getTitle(), Field.Store.YES));
                document.add(new TextField("content", article.getContext(), Field.Store.YES));
                document.add(new StringField("author", article.getAuthor(), Field.Store.YES));
                indexWriter.addDocument(document);
            }
            //提交索引，关闭索引操作
            indexWriter.commit();
            indexWriter.close();
        }
    }

    public void deleteIndex(String key) throws IOException {
        //按照id删除
//        Term term = new Term("id", id);
//        indexWriter.deleteDocuments(term);
//        indexWriter.commit();
//        indexWriter.close();
        //要求：查询 印度 出现在title 或者 content 中出现，就删除该索引
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(new TermQuery(new Term("title", key)), BooleanClause.Occur.SHOULD);
        builder.add(new TermQuery(new Term("content",key)), BooleanClause.Occur.SHOULD);
        indexWriter.deleteDocuments(builder.build());
        indexWriter.commit();
        indexWriter.close();
    }

    public void updateIndex(String id) throws  Exception{
        Document document = new Document();
        document.add(new StringField("id","1001", Field.Store.YES));
        document.add(new StringField("title","XXX", Field.Store.YES));
        document.add(new StringField("author","CCC", Field.Store.YES));
        document.add(new TextField("content","YYYYYY", Field.Store.YES));
        indexWriter.updateDocument(new Term("id",id),document);
        indexWriter.commit();
        indexWriter.close();
    }

}
