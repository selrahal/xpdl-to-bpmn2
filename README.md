# XPDL to BPMN2 Migration

This project can be used to kick start migrations for business process management projects to Red Hat Process Automation Manager. It currently supports various bpmn2 elements:

* BPMNEdge, BPMNShape
* Gateways, Sequence Flows
* StartNode, EndNode, IntermediateEvents, Signal, Timer
* OnEntry, OnExit scripts
* Pool, Swimlane, Subprocesses
* And more...

And quite a few source file formats:

* Tibco xpdl 2.1
* BEA Aqualogic BPM 2.0
* Software AG 2.2
* Blueworks Live xpdl 2.1
* Fallback XML processor




## TODO
* Write better README
* Continue to break out the mega xsl (XPDLtoBPMN file) into smaller xsl
* Handle multiple incoming/outgoing sequence flows to non-gateway nodes correctly


### Instructions (Windup)

_Coming Soon_

### Instructions (Java)

#### Using the UberJar
In order to execute the transformation on an XPDL 2.1 file name `in.xpdl` and output the result processes to a new directory labeled `generated` , navigate to the `xpdl-to-bpmn2` project and build the uberjar:

```bash
[selrahal@localhost xpdl-to-bmn2]$ mvn clean package
``` 

Then run the uberjar and pass in the XPDL file location and desired output folder:

```bash
[selrahal@localhost xpdl-to-bmn2]$ java -jar target/xpdl-to-bpmn2-0.15-SNAPSHOT-jar-with-dependencies.jar in.xpdl generated
```

#### Using Maven
In order to execute the transformation on an XPDL 2.1 file name `in.xpdl` and output the result processes to a new directory labeled `generated` , navigate to the `xpdl-to-bpmn2` project and build the project:

```bash
[selrahal@localhost xpdl-to-bmn2]$ mvn clean package
``` 

Then you can use maven to run the migration, you must pass in the file location and the desired output folder as `exec.args` like so:

```bash
[selrahal@localhost xpdl-to-bpmn2]$ mvn exec:java -Dexec.mainClass="org.jbpm.migration.main.JbpmMigration" -Dexec.args="in.xpdl generated"
```
