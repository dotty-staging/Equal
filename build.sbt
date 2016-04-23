lazy val baseName         = "Equal"
lazy val baseNameL        = baseName.toLowerCase
lazy val projectVersion   = "0.1.0-SNAPSHOT"

lazy val commonSettings = Seq(
  version             := projectVersion,
  organization        := "de.sciss",
  description         := "Simple macro-based type safe equals operator ===",
  homepage            := Some(url(s"https://github.com/Sciss/$baseName")),
  scalaVersion        := "2.11.8",
  licenses            := Seq("LGPL v2.1+" -> url("http://www.gnu.org/licenses/lgpl-2.1.txt")),
  scalacOptions      ++= Seq("-deprecation", "-unchecked", "-feature", "-encoding", "utf8", "-Xfuture", "-Xlint"),
  libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  libraryDependencies ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      // if Scala 2.11+ is used, quasi-quotes are available in the standard distribution
      case Some((2, scalaMajor)) if scalaMajor >= 11 => Nil
      // in Scala 2.10, quasi-quotes are provided by macro paradise
      case Some((2, 10)) =>
        Seq(
          compilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full),
          "org.scalamacros" %% "quasiquotes" % "2.1.0-M5" cross CrossVersion.binary
        )
    }
  },
  initialCommands in console := "import de.sciss.equal.Implicits._"
)

lazy val root = Project(id = baseNameL, base = file("."))
  .settings(commonSettings)
