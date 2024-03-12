package com.mudra.bestpricebookstore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.concurrent.StructuredTaskScope.Subtask.State;
import java.util.concurrent.ThreadFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class BookRetrievalService {

    @Value("#{${book.store.baseurls}}")
    private Map<String,String> storeUrlMap;
    
    RestClient restClient = RestClient.create();
    
    public List<Book> getBookFromAllStores(String bookName) throws InterruptedException {
        
    	ThreadFactory factory = Thread.ofVirtual().name("book-store-thr",0).factory();
        try (var scope = new StructuredTaskScope<Book>("virtualstore", factory)) {
            
            List<Subtask<Book>> bookTasks = new ArrayList<>();
            storeUrlMap.forEach( (name, url) -> {
                bookTasks.add(scope.fork(() -> getBookFromStore(name,url,bookName)));
            });
            
            scope.join();
            
            // Dump stacktrace of all failures
            bookTasks.stream()
                .filter(t -> t.state() == State.FAILED)
                .map(Subtask::exception)
                .forEach(e -> e.printStackTrace());
            
            return bookTasks.stream()
                .filter(t -> t.state() == State.SUCCESS)
                .map(Subtask::get)
                .toList();
        }
        
    }

    private Book getBookFromStore(String storeName, String url, String bookName) {
        long start = System.currentTimeMillis();
        Book book = restClient.get()
                .uri(url + "/store/book", t -> t.queryParam("name", bookName).build())
                .retrieve()
                .body(Book.class);
        
        long end = System.currentTimeMillis();
        
        RestCallStatistics timeObj = BestPriceBookController.TIMEMAP.get();
        timeObj.addTiming(storeName, end - start);
        
        return book;
    }
    

}
