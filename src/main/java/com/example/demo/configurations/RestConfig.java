package com.example.demo.configurations;

import com.example.demo.data.views.interfaces.ICustomAuthorView;
import com.example.demo.data.views.interfaces.ICustomBookView;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class RestConfig implements RepositoryRestConfigurer {

  @Override
  public void configureRepositoryRestConfiguration(
    RepositoryRestConfiguration repositoryRestConfiguration,
    CorsRegistry cors
  ) {
    repositoryRestConfiguration
      .getProjectionConfiguration()
      .addProjection(ICustomBookView.class);
    repositoryRestConfiguration
      .getProjectionConfiguration()
      .addProjection(ICustomAuthorView.class);
  }
}
