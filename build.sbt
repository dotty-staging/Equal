lazy val baseName         = "Equal"
lazy val baseNameL        = baseName.toLowerCase

lazy val projectVersion   = "0.1.5"
lazy val mimaVersion      = "0.1.0"

lazy val deps = new {
  val main = new {
    val macros    = "2.1.0"
  }
  val test = new {
    val scalaTest = "3.2.2"
  }
}

lazy val commonSettings = Seq(
  version             := projectVersion,
  organization        := "de.sciss",
  description         := "Simple macro-based type safe equals operator ===",
  homepage            := Some(url(s"https://git.iem.at/sciss/$baseName")),
  scalaVersion        := "2.13.3",
  crossScalaVersions  := Seq("0.27.0-RC1", "2.13.3", "2.12.12"),
  licenses            := Seq("LGPL v2.1+" -> url("http://www.gnu.org/licenses/lgpl-2.1.txt")),
  scalacOptions      ++= Seq("-deprecation", "-unchecked", "-feature", "-encoding", "utf8", "-Xlint", "-Xsource:2.13"),
  libraryDependencies ++= {
    if (isDotty.value) Nil else Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided
    )
  },
  libraryDependencies += {
    "org.scalatest" %% "scalatest" % deps.test.scalaTest % Test
  },
  unmanagedSourceDirectories in Compile += {
    val sourceDir = (sourceDirectory in Compile).value
    if (isDotty.value) sourceDir / "scala-2.14+" else sourceDir / "scala-2.14-"
  },
  unmanagedSourceDirectories in Test += {
    val sourceDir = (sourceDirectory in Test).value
    if (isDotty.value) sourceDir / "scala-2.14+" else sourceDir / "scala-2.14-"
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

