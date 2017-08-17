package com.yztc.lucene.index;

import com.yztc.lucene.domain.Article;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

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

    private StandardAnalyzer analyzer;//标准分词器
    private Path path;

    public IndexManager() throws IOException {
        path = FileSystems.getDefault().getPath(INDEX_DIR);
        directory = FSDirectory.open(path);//打开索引的目录
        analyzer = new StandardAnalyzer();
        config = new IndexWriterConfig(analyzer);
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

}
