package com.example.demo.data.repositories.interfaces;

import com.example.demo.data.models.Book;
import com.example.demo.data.views.interfaces.ICustomBookView;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(
  excerptProjection = ICustomBookView.class,
  collectionResourceRel = "books",
  path = "books",
  exported = true
)
public interface IBookRepository
  extends PagingAndSortingRepository<Book, String> {
  Book findByTitle(@Param("title") String title);

  Book findByAuthorsName(@Param("authorName") String authorName);
}
