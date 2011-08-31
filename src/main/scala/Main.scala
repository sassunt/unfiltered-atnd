package com.example

object Main {
  def main(args: Array[String]) {
    unfiltered.jetty.Http.anylocal.filter(new App()).run
  }
}
