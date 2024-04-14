package io.collective.start;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.collective.articles.ArticleDataGateway;
import io.collective.articles.ArticleRecord;
import io.collective.articles.ArticlesController;
import io.collective.endpoints.EndpointDataGateway;
import io.collective.endpoints.EndpointTask;
import io.collective.endpoints.EndpointWorkFinder;
import io.collective.endpoints.EndpointWorker;
import io.collective.restsupport.BasicApp;
import io.collective.restsupport.NoopController;
import io.collective.restsupport.RestTemplate;
import io.collective.workflow.WorkScheduler;
import io.collective.workflow.Worker;
import org.eclipse.jetty.server.handler.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.TimeZone;


public class App extends BasicApp {
    private static ArticleDataGateway articleDataGateway = new ArticleDataGateway(List.of(
            new ArticleRecord(10101, "Programming Languages InfoQ Trends Report - October 2019 4", true),
            new ArticleRecord(10106, "Ryan Kitchens on Learning from Incidents at Netflix, the Role of SRE, and Sociotechnical Systems", true)
    ));

    //Method called when the application starts
    @Override
    public void start() {
        super.start();

        // Create a new EndpointWorkFinder
        EndpointWorkFinder workFinder = new EndpointWorkFinder(new EndpointDataGateway());

        // Create a new EndpointWorker
        EndpointWorker endpointWorker = new EndpointWorker(new RestTemplate(), articleDataGateway);

        // Create a list of workers
        List<Worker<EndpointTask>> workerList = Collections.singletonList(endpointWorker);

        // Create a new WorkScheduler and start it
        WorkScheduler<EndpointTask> workScheduler = new WorkScheduler<>(workFinder, workerList, 300);
        workScheduler.start();
    }

    public App(int port) {
        super(port);
    }

    @NotNull
    @Override
    protected HandlerList handlerList() {
        HandlerList handlerList = new HandlerList();
        handlerList.addHandler(new ArticlesController(new ObjectMapper(), articleDataGateway));
        handlerList.addHandler(new NoopController());
        return handlerList;
    }

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        String port = System.getenv("PORT") != null ? System.getenv("PORT") : "8881";
        App application = new App(Integer.parseInt(port));
        application.start();
    }
}
