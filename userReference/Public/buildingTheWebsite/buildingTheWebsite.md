Title: Building the Website
Author: Steven Zeil
Date: @docModDate@

CoWeM uses [Gradle](https://gradle.org) to automate the process of building the website.

Gradle is a build manager for software projects. Although a websiote may not involvr programming
in the usual sense, it can certainly be regarded as a software system. If ypur only experience with
with build managers has been the ones built in to a programming IDE or the venerable Unix `make` utility,
you may be surprised at the range of features provided by Gradle.  Of particular relevance to CoWeM:

* Gradle allows the steps involved in a build, such as copying and and manipulation of files, to be described and performed in in a portable (not dependent on a specific operating system) manner.

* Gradle can fetch and incorporate software packages needed for the build from over the internet,
  and can check periodically for updates to those packages.
  
* The ability to fetch needed software packages extends to Gradle itself, so by including a few small
  files in each copy of a CoWeM course, the Gradle build system can be bootstrapped on to any Linux,
  Windows, or OS/X machine with a reasonably up-to-date Java environment (JRE).
  
* Gradle can be extended, teaching it new processes for different kinds of projects, via a _plugin_ system.
    * CoWeM is, in fact, implemented as a package of related Gradle plugins.


# Running Gradle


# The Build Targets

