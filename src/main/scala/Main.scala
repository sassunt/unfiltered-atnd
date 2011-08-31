package com.example

object Main {
  def main(args: Array[String]) {
    unfiltered.jetty.Http.anylocal.filter(new AtndSearcher).run
  }
}
