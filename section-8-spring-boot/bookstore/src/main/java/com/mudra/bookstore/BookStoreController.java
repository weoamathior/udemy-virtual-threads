package com.mudra.bookstore;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/store")
public class BookStoreController {
    
    @Autowired
    private BookCollection bookColn;
    
    @GetMapping("/book")
    public Book getBookByName(@RequestParam String name) {
        
    	// Add an artificial delay of 5 secs for testing
        delayOf5Secs();
        
        return bookColn.findBook(name);
    }

    private void delayOf5Secs() {
        try {
            Thread.sleep(Duration.ofSeconds(5));
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
