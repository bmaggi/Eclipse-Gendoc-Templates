# Gendoc2
A forked version of the open source project [Gendoc][2] used by the [Papyrus][1] team.

### Repository structure ###

This repository is organized around logical software components:

* `core`: The core software components for Gendoc2.
* `p2`: The Eclipse update site specification for all the above components.
* `robotml`: The RobotML-related software component for Gendoc2. They depend on the core components.
* `sysml`: The SysML-related software component for Gendoc2. They depend on the core components.


### How to build ###

Components in this project are built using Maven and its Tycho plugin for the build of Eclipse artifacts.
To build locally, simply execute the command line:

```
mvn clean install
```

## Travis CI 
[![Build Status](https://travis-ci.org/bmaggi/Gendoc2.svg?branch=master)](https://travis-ci.org/bmaggi/Gendoc2)

[1]:http://www.eclipse.org/papyrus/
[2]:http://www.eclipse.org/gendoc/
