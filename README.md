# Simple Beam/Dataflow tests for Google Support

Starting point was:
https://beam.apache.org/get-started/quickstart-java/


# Running locally
* Providing inputFile value - WORKS
```
mvn compile exec:java \
     -Pdirect-runner \
     -Dexec.mainClass=org.apache.beam.examples.MyMinimalWordCount \
     -Dexec.args="--runner=DirectRunner \
                  --inputFile=gs://catalogue.unimarket.com/TESTING/input/names.txt \
                  --outputFile=c:/temp/output.txt" 
```

* Omitting inputFile value - DOES NOT WORK
```
mvn compile exec:java \
     -Pdirect-runner \
     -Dexec.mainClass=org.apache.beam.examples.MyMinimalWordCount \
     -Dexec.args="--runner=DirectRunner \
                  --outputFile=c:/temp/output.txt" 
```


# Running on Dataflow
* Providing inputFile value - WORKS
```
mvn compile exec:java \
     -Pdataflow-runner \
     -Dexec.mainClass=org.apache.beam.examples.MyMinimalWordCount \
     -Dexec.args="--runner=DataflowRunner \
                  --project=unimarket-catalogue \
                  --inputFile=gs://catalogue.unimarket.com/TESTING/input/names.txt \
                  " 
```

# Create and launch dataflow template and test A 

* Create template WITHOUT inputFile - CAUSES EXCEPTION
```
mvn compile exec:java \
     -Dexec.mainClass=org.apache.beam.examples.MyMinimalWordCount \
     -Dexec.args="--runner=DataflowRunner \
                  --project=unimarket-catalogue \
                  --stagingLocation=gs://catalogue.unimarket.com/TESTING/staging \
                  --templateLocation=gs://catalogue.unimarket.com/TESTING/templates/MyMinimalWordCountTemplate \
                  " 
```

* Launch with input file - WORKS
```
 gcloud beta dataflow jobs run testjob1 \
        --gcs-location gs://catalogue.unimarket.com/TESTING/templates/MyMinimalWordCountTemplate \
        --parameters inputFile=gs://catalogue.unimarket.com/TESTING/input/colours.txt,outputFile=gs://catalogue.unimarket.com/TESTING/output/output4.txt
```

# Create and launch dataflow template and test B

* Create template WITH inputFile - WORKS no exception
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

* Launch - DOES NOT USE PROVIDED INPUT
Output files contain names and NOT colours as expected
```
 gcloud beta dataflow jobs run testjob2 \
        --gcs-location gs://catalogue.unimarket.com/TESTING/templates/MyMinimalWordCountTemplate \
        --parameters inputFile=gs://catalogue.unimarket.com/TESTING/input/colours.txt,outputFile=gs://catalogue.unimarket.com/TESTING/output/output3.txt
```



