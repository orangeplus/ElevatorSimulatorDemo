name := "ElevatorSimulatorDemo"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "mysql" % "mysql-connector-java" % "5.1.26"
)

play.Project.playJavaSettings

resolvers += "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"

resolvers += "Maven Central" at "http://repo.maven.apache.org/maven2/"

