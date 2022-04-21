package com.example.demo.data.views.interfaces;

import com.example.demo.data.models.Author;
import com.example.demo.data.models.Book;
import java.sql.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "customAuthor", types = { Author.class })
public interface ICustomAuthorView {
  @Value("#{target.id}")
  int getId();

  String getName();

  Date getBirthday();

  Book getBook();
}
