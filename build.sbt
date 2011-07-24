
name := "verdandi"

version := "0.1"

organization := "none"

scalaVersion := "2.9.0"

//unmanagedSourceDirectories <+= baseDirectory( _ / "src" / "main" / "rc" ) 

// add compile dependencies on a some dispatch modules
libraryDependencies ++= Seq(
"org.scala-lang" % "scala-swing" % "2.9.0" ,
    "junit" % "junit" % "4.8",
    "org.scalatest" % "scalatest_2.9.0" % "1.6.1",
    "org.scala-tools.testing" %% "specs" % "1.6.8", 
    "log4j" % "log4j" % "1.2.16",
    "commons-logging" % "commons-logging" % "1.1",
"com.weiglewilczek.slf4s" % "slf4s_2.9.0-1" % "1.0.6",
"org.slf4j" % "slf4j-api" % "1.6.1",
"org.slf4j" % "slf4j-log4j12" % "1.6.1",
     // "net.infonode" % "docking" % "1.6.1",
     //"infonode" % "idw" % "1.6.1",
     "com.jgoodies" % "looks" % "2.1.4",
     "org.swinglabs" % "swingx" % "1.6.1"
)


