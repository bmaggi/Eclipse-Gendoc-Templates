# README #

This component contains the core Eclipse plugins and features for Gendoc2.

### How to build ###

This component is built using Maven and its Tycho plugin for the build of Eclipse artifacts.
To build this component, run:

```
$ mvn clean install -Dpapyrus.repo.main=<uri1> -Dpapyrus.repo.extras=<uri2>
```

In this command the following must be replaced:

* `<uri1>`: The URL to the update site of the Papyrus main components. Inside CEA, this can be http://is148366.intra.cea.fr/sites/papyrus-master-mainTycho
* `<uri2>`: The URL to the update site of the Papyrus extras components. Inside CEA, this can be http://is148366.intra.cea.fr/sites/papyrus-master-extrasTycho