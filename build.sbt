organization  := "com.numberfour"

version       := "0.1"

scalaVersion  := "2.10.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "nightlies spray repo" at "http://nightlies.spray.io/",
  "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases"  at "http://oss.sonatype.org/content/repositories/releases",
  "apache maven repo" at "http://repo.maven.apache.org/maven2"
)

libraryDependencies ++= Seq(
  "io.spray"            %   "spray-can"     % "1.2-20130710",
  "io.spray"            %   "spray-routing" % "1.2-20130710",
  "io.spray"            %   "spray-testkit" % "1.2-20130710",
  "com.typesafe.akka"   %%  "akka-actor"    % "2.2.1",
  "com.typesafe.akka"   %%  "akka-testkit"  % "2.2.1",
  "org.specs2"          %%  "specs2"        % "2.2.2" % "test",
  "org.mongodb"         %%  "casbah"        % "2.6.3",
  "org.slf4j"           %   "slf4j-simple"  % "1.7.5",
  "org.clapper"         %   "grizzled-slf4j_2.10" % "1.0.1"
)

scalacOptions in Test ++= Seq("-Yrangepos")

seq(Revolver.settings: _*)
