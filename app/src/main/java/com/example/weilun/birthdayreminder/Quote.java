package com.example.weilun.birthdayreminder;

import java.io.Serializable;

/**
 * Created by Wei Lun on 8/17/2017.
 */

public class Quote implements Serializable {
    private String quote;
    private String author;
    private String category;

    public Quote(){};

    public Quote(String quote, String author, String category){
        this.quote = quote;
        this.author = author;
        this.category = category;
    }

    public void setQuote(String quote){
        this.quote = quote;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public String getQuote(){
        return quote;
    }

    public String getAuthor(){
        return author;
    }

    public String getCategory(){
        return category;
    }
}
