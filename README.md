# Gendoc2
A forked version of the open source project org.eclipse.gendoc used by the Papyrus team.

### Repository structure ###

This repository is organized around logical software components:

* `core`: The core software components for Gendoc2.
* `p2`: The Eclipse update site specification for all the above components.

### How to build ###

Components in this project are built using Maven and its Tycho plugin for the build of Eclipse artifacts.
To build locally, simply execute the command line:

```
mvn clean install
```
