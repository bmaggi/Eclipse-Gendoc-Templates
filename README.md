# Gendoc templates for Papyrus Eclipse
SysML and RobotML [Gendoc][2] templates used by the [Papyrus][1] team.


:warning: templates are coming from an old fork named Gendoc2 2.0.0

:warning: Built against Eclipse Luna

### Status ###

[ ![Codeship Status for bmaggi/Gendoc2](https://codeship.com/projects/179901f0-0625-0133-39c8-660a355d9d19/status?branch=master)](https://codeship.com/projects/89638)
[![License](https://img.shields.io/badge/license-EPL-blue.svg)](https://www.eclipse.org/legal/epl-v10.html)


### Repository structure ###

This repository is organized around logical software components:

* `p2`: The Eclipse update site specification for all the above components.
* `robotml`: The RobotML-related software component for Gendoc. 
* `sysml`: The SysML-related software component for Gendoc. 


### How to build ###

Components in this project are built using Maven and its Tycho plugin for the build of Eclipse artifacts.
To build locally, simply execute the command line:

```
mvn clean install
```

### How to release ###

A reminder for a standard release

* Set the fix version for the release
```
mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=X.Y.Z
```

* Tag the code with X.Y.Z
```
git tag -l "X.Y.Z"
```

* Set the new snapshot version
```
mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=X.Y.Z-SNAPSHOT
```

### How to use ###

Install the latest stable version from 
http://bmaggi.github.io/Eclipse-Gendoc-Templates/repository/


Or once the projects are build you need:
 - add the update site in "Available Software Site"  (it has been created here p2\target\repository\plugins\content.jar)
 - install the plugins

[1]:http://www.eclipse.org/papyrus/
[2]:http://www.eclipse.org/gendoc/
