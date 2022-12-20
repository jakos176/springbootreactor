package com.example.springbootreactor.models;

import java.util.ArrayList;
import java.util.List;

public class Comments {

  private List<String> comments;

  public Comments() {
    this.comments = new ArrayList<>();
  }

  public void AddComment(String comment) {
    this.comments.add(comment);
  }

  @Override
  public String toString() {
    return "comments=" + comments;
  }
}
