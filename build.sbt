lazy val baseName         = "Equal"
lazy val baseNameL        = baseName.toLowerCase

lazy val projectVersion   = "0.1.6"
lazy val mimaVersion      = "0.1.0"

lazy val deps = new {
  val main = new {
    val macros    = "2.1.0"
  }
  val test = new {
    val scalaTest = "3.2.8"
  }
}

lazy val commonJvmSettings = Seq(
  crossScalaVersions  := Seq("3.0.0-RC3", "2.13.5", "2.12.13"),
  unmanagedSourceDirectories in Compile += {
    val sourceDir = (sourceDirectory in Compile).value
    // println("sourceDir[Compile] = " + sourceDir)
    if (isDotty.value) sourceDir / "scala-2.14+" else sourceDir / "scala-2.14-"
  },
  unmanagedSourceDirectories in Test += {
    val sourceDir = (sourceDirectory in Test).value
    // println("sourceDir[Test] = " + sourceDir)
    if (isDotty.value) sourceDir / "scala-2.14+" else sourceDir / "scala-2.14-"
  },
)

// sonatype plugin requires that these are in global
ThisBuild / version      := projectVersion
ThisBuild / organization := "de.sciss"

lazy val commonSettings = Seq(
//  version             := projectVersion,
//  organization        := "de.sciss",
  description         := "Simple macro-based type safe equals operator ===",
  homepage            := Some(url(s"https://git.iem.at/sciss/$baseName")),
  scalaVersion        := "2.13.5",
  licenses            := Seq("LGPL v2.1+" -> url("http://www.gnu.org/licenses/lgpl-2.1.txt")),
  scalacOptions      ++= Seq("-deprecation", "-unchecked", "-feature", "-encoding", "utf8", "-Xlint", "-Xsource:2.13"),
  libraryDependencies ++= {
    if (isDotty.value) Nil else Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided
    )
  },
  libraryDependencies += {
    "org.scalatest" %%% "scalatest" % deps.test.scalaTest % Test
  },
  initialCommands in console := "import de.sciss.equal.Implicits._"
)

// ---- publishing ----

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  developers := List(
    Developer(
      id    = "sciss",
      name  = "Hanns Holger Rutz",
      email = "contact@sciss.de",
      url   = url("https://www.sciss.de")
    )
  ),
  scmInfo := {
    val h = "git.iem.at"
    val a = s"sciss/${name.value}"
    Some(ScmInfo(url(s"https://$h/$a"), s"scm:git@$h:$a.git"))
  },
)

// ---- project definition ----

lazy val root = crossProject(JSPlatform, JVMPlatform).in(file("."))
  .settings(commonSettings)
  .jvmSettings(commonJvmSettings)
  .settings(publishSettings)
  .settings(
    name := baseName,
    mimaPreviousArtifacts := Set("de.sciss" %% baseNameL % mimaVersion)
  )

