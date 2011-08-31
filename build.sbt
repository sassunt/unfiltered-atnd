organization := "com.example"

name := "unfiltered-atnd"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.0-1"

seq(sbtappengine.AppenginePlugin.webSettings:  _*)

libraryDependencies <++= scalaVersion{ v =>
  "net.databinder" %% "unfiltered-filter" % "0.4.1" ::
  "net.databinder" %% "unfiltered-jetty" % "0.4.1" ::
  "net.databinder" %% "unfiltered-spec" % "0.4.1" % "test" ::
  // uncomment the following line for persistence
  //, val jdo = "javax.jdo" % "jdo2-api" % "2.3-ea"
  "org.scala-tools.time" % "time_%s".format(v) % "0.4" ::
  "org.scalaz" % "scalaz-core_%s".format(v) % "6.0.1" ::
  Nil
}

resolvers ++= Seq(
 "jboss" at  "https://repository.jboss.org/nexus/content/groups/public/"
  // app engine repo, uncomment the following line for persistence resolver
  //, "nexus" at "http://maven-gae-plugin.googlecode.com/svn/repository/"
)




