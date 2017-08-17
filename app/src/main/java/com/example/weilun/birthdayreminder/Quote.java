package com.example.weilun.birthdayreminder;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by Wei Lun on 8/17/2017.
 */

public class Quote implements Serializable, Comparable<Quote>{
    private String quote;
    private String author;
    private String category;

    public Quote() {
    }

    public Quote(String quote, String author, String category) {
        this.quote = quote;
        this.author = author;
        this.category = category;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public int compareTo(@NonNull Quote o) {
        return this.author.compareTo(o.getAuthor());
    }
}
