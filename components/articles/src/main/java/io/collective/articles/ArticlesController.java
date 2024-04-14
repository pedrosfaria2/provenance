package io.collective.articles;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.collective.restsupport.BasicHandler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

public class ArticlesController extends BasicHandler {
    private static final String JSON_MIME_TYPE = "application/json";
    private static final String HTML_MIME_TYPE = "text/html";

    private final ArticleDataGateway gateway;

    public ArticlesController(ObjectMapper mapper, ArticleDataGateway gateway) {
        super(mapper);
        this.gateway = gateway;
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        get("/articles", List.of(JSON_MIME_TYPE, HTML_MIME_TYPE), request, servletResponse, () -> {
            List<ArticleInfo> allArticleInfos = mapToArticleInfos(gateway.findAll());
            writeJsonBody(servletResponse, allArticleInfos);
        });

        get("/available", List.of(JSON_MIME_TYPE), request, servletResponse, () -> {
            List<ArticleInfo> availableArticleInfos = mapToArticleInfos(gateway.findAvailable());
            writeJsonBody(servletResponse, availableArticleInfos);
        });
    }

    private List<ArticleInfo> mapToArticleInfos(List<ArticleRecord> articles) {
        return articles.stream()
                .map(record -> new ArticleInfo(record.getId(), record.getTitle()))
                .collect(Collectors.toList());
    }
}