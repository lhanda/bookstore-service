package com.example.demo.data.repositories.interfaces;

import com.example.demo.data.models.Author;
import com.example.demo.data.views.interfaces.ICustomAuthorView;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(
  excerptProjection = ICustomAuthorView.class,
  collectionResourceRel = "authors",
  path = "authors",
  exported = true
)
public interface IAuthorRepository
  extends PagingAndSortingRepository<Author, Integer> {}
