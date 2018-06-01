# Simple Beam/Dataflow tests for Google Support

Case: 15826095
Starting point for code: https://beam.apache.org/get-started/quickstart-java/

## Overview
We have encountered two separate but related issues:

### ISSUE 1
 * When trying to deploy a Dataflow template _without_ specifying a value for the input parameter we get an exception.
 * We observe that the template is still deployed however.
 * Launching the template with input also works. 
 * Example output showing exception [deploy-template-without-inputFile.txt](deploy-template-without-inputFile.txt).

### ISSUE 2
 * Attempting to get rid of the exception produced in **ISSUE 1** by supplying a value for the input parameter.
 * No exception is produced during template creation.
 * Launching the template with different input values has no effect. It always uses the input value used during template creation.
 
The steps below reproduce the two problems described above.

Other notes:
 * The GCS urls used for input are public.
 * We have reproduced **ISSUE 2** in a completely new project. Please see [NEW-PROJECT.md](NEW-PROJECT.md)


# Running locally
* Just to check that the pipeline works.
* Providing inputFile value - **WORKS**
```
mvn compile exec:java \
     -Pdirect-runner \
     -Dexec.mainClass=org.apache.beam.examples.MyMinimalWordCount \
     -Dexec.args="--runner=DirectRunner \
                  --inputFile=gs://catalogue.unimarket.com/TESTING/input/names.txt \
                  --outputFile=c:/temp/output.txt" 
```

* Omitting inputFile value - **DOES NOT WORK**
```
mvn compile exec:java \
     -Pdirect-runner \
     -Dexec.mainClass=org.apache.beam.examples.MyMinimalWordCount \
     -Dexec.args="--runner=DirectRunner \
                  --outputFile=c:/temp/output.txt" 
```


# Running on Dataflow directly (no template)
* Providing inputFile value - **WORKS**
```
mvn compile exec:java \
     -Pdataflow-runner \
     -Dexec.mainClass=org.apache.beam.examples.MyMinimalWordCount \
     -Dexec.args="--runner=DataflowRunner \
                  --project=unimarket-catalogue \
                  --inputFile=gs://catalogue.unimarket.com/TESTING/input/names.txt \
                  " 
```

# Create and launch dataflow template (without input at template creation)
 * Create template WITHOUT inputFile - **CAUSES EXCEPTION**
 * Example output showing exception [deploy-template-without-inputFile.txt](deploy-template-without-inputFile.txt).
```
mvn compile exec:java \
     -Dexec.mainClass=org.apache.beam.examples.MyMinimalWordCount \
     -Dexec.args="--runner=DataflowRunner \
                  --project=unimarket-catalogue \
                  --stagingLocation=gs://catalogue.unimarket.com/TESTING/staging \
                  --templateLocation=gs://catalogue.unimarket.com/TESTING/templates/MyMinimalWordCountTemplate \
                  " 
```

* Launch with input file - **WORKS**
```
 gcloud beta dataflow jobs run testjob1 \
        --gcs-location gs://catalogue.unimarket.com/TESTING/templates/MyMinimalWordCountTemplate \
        --parameters inputFile=gs://catalogue.unimarket.com/TESTING/input/colours.txt,outputFile=gs://catalogue.unimarket.com/TESTING/output/output4.txt
```

# Create and launch dataflow template (with input at template creation)

* Create template WITH inputFile - **WORKS no exception**
```
mvn compile exec:java \
     -Dexec.mainClass=org.apache.beam.examples.MyMinimalWordCount \
     -Dexec.args="--runner=DataflowRunner \
                  --project=unimarket-catalogue \
                  --stagingLocation=gs://catalogue.unimarket.com/TESTING/staging \
                  --templateLocation=gs://catalogue.unimarket.com/TESTING/templates/MyMinimalWordCountTemplate \
                  --inputFile=gs://catalogue.unimarket.com/TESTING/input/names.txt \
                  " 
```

* Launch - **DOES NOT USE PROVIDED INPUT**
Output files contain names and NOT colours as expected
```
 gcloud beta dataflow jobs run testjob2 \
        --gcs-location gs://catalogue.unimarket.com/TESTING/templates/MyMinimalWordCountTemplate \
        --parameters inputFile=gs://catalogue.unimarket.com/TESTING/input/colours.txt,outputFile=gs://catalogue.unimarket.com/TESTING/output/output3.txt
```



