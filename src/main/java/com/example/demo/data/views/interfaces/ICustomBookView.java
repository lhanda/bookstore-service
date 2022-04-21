package com.example.demo.data.views.interfaces;

import com.example.demo.data.models.Author;
import com.example.demo.data.models.Book;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "customBook", types = { Book.class })
public interface ICustomBookView {
  @Value("#{target.isbn}")
  String getIsbn();

  String getTitle();

  List<Author> getAuthors();

  int getYear();

  double getPrice();

  String getGenre();
}
