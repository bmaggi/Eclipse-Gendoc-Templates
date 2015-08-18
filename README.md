# Gendoc2 2.0.0
SysML and RobotML [Gendoc][2] templates used by the [Papyrus][1] team.

### Status ###

Codeship [ ![Codeship Status for bmaggi/Gendoc2](https://codeship.com/projects/179901f0-0625-0133-39c8-660a355d9d19/status?branch=master)](https://codeship.com/projects/89638)

License [![License](https://img.shields.io/badge/license-EPL-blue.svg)](https://www.eclipse.org/legal/epl-v10.html)


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

[1]:http://www.eclipse.org/papyrus/
[2]:http://www.eclipse.org/gendoc/
