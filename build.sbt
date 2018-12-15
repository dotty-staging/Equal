lazy val baseName         = "Equal"
lazy val baseNameL        = baseName.toLowerCase

lazy val projectVersion   = "0.1.2"
lazy val mimaVersion      = "0.1.0"

lazy val deps = new {
  val main = new {
    val macros    = "2.1.0"
  }
  val test = new {
    val scalaTest = "3.0.5"
  }
}

lazy val commonSettings = Seq(
  version             := projectVersion,
  organization        := "de.sciss",
  description         := "Simple macro-based type safe equals operator ===",
  homepage            := Some(url(s"https://git.iem.at/sciss/$baseName")),
  scalaVersion        := "2.12.8",
  crossScalaVersions  := Seq("2.12.8", "2.11.12", "2.13.0-M5"),
  licenses            := Seq("LGPL v2.1+" -> url("http://www.gnu.org/licenses/lgpl-2.1.txt")),
  scalacOptions      ++= Seq("-deprecation", "-unchecked", "-feature", "-encoding", "utf8", "-Xfuture", "-Xlint"),
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided
  ),
  libraryDependencies += {
    val v = if (scalaVersion.value == "2.13.0-M5") "3.0.6-SNAP5" else deps.test.scalaTest
    "org.scalatest"  %% "scalatest" % v % Test
  },
  libraryDependencies ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      // if Scala 2.11+ is used, quasi-quotes are available in the standard distribution
      case Some((2, scalaMajor)) if scalaMajor >= 11 => Nil
      // in Scala 2.10, quasi-quotes are provided by macro paradise
      case Some((2, 10)) =>
        Seq(
          compilerPlugin(
            "org.scalamacros" %  "paradise"    % deps.main.macros cross CrossVersion.full
          ),
            "org.scalamacros" %% "quasiquotes" % deps.main.macros cross CrossVersion.binary
        )
    }
  },
  initialCommands in console := "import de.sciss.equal.Implicits._"
)

// ---- publishing ----

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo := {
    Some(if (isSnapshot.value)
      "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
    else
      "Sonatype Releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
    )
  },
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  pomExtra := { val n = name.value
    <scm>
      <url>git@git.iem.at:sciss/{n}.git</url>
      <connection>scm:git:git@git.iem.at:sciss/{n}.git</connection>
    </scm>
    <developers>
      <developer>
        <id>sciss</id>
        <name>Hanns Holger Rutz</name>
        <url>http://www.sciss.de</url>
      </developer>
    </developers>
  }
)

// ---- project definition ----

lazy val root = project.withId(baseNameL).in(file("."))
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(
    mimaPreviousArtifacts := Set("de.sciss" %% baseNameL % mimaVersion)
  )

