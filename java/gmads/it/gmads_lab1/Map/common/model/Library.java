package gmads.it.gmads_lab1.Map.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import gmads.it.gmads_lab1.BookPackage.Book;

public class Library {

    @SerializedName("places")
    List<Book> BookList = new ArrayList();

    public List<Book> getBookList() {
        return BookList;
    }
    public void setBookList( List<Book> books){this.BookList =books;}
    public boolean isEmpty(){return BookList.isEmpty();}
}