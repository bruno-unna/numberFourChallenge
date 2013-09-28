organization  := "com.numberfour"

version       := "0.1"

scalaVersion  := "2.10.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "nightlies spray repo" at "http://nightlies.spray.io/",
  "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases"  at "http://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= Seq(
  "io.spray"            %   "spray-can"     % "1.2-20130710",
  "io.spray"            %   "spray-routing" % "1.2-20130710",
  "io.spray"            %   "spray-testkit" % "1.2-20130710",
  "com.typesafe.akka"   %%  "akka-actor"    % "2.2.1",
  "com.typesafe.akka"   %%  "akka-testkit"  % "2.2.1",
  "org.specs2"          %%  "specs2"        % "2.2.2" % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")

seq(Revolver.settings: _*)
