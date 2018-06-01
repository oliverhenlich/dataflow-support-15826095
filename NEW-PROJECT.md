# Reproduce in new GCP project

* Create new project
```
gcloud projects create unimarket-gcp-support
gcloud config set project=unimarket-gcp-support
gcloud alpha billing projects link unimarket-gcp-support --billing-account=005EF9-1C0F8F-475D81
``` 

* Enable required services/apis
```
gcloud compute project-info describe
gcloud services list --enabled
gcloud services list --available  --filter='NAME:dataflow*'
gcloud services enable dataflow.googleapis.com 
```

* Create gcs bucket
```
gsutil mb gs://unimarket-gcp-support
```

* Ensure required files and locations exist
```
gsutil cp src/main/java/org/apache/beam/examples/colours.txt gs://unimarket-gcp-support/input/
gsutil cp src/main/java/org/apache/beam/examples/names.txt gs://unimarket-gcp-support/input/

gsutil ls gs://unimarket-gcp-support/input/
gs://unimarket-gcp-support/input/colours.txt
gs://unimarket-gcp-support/input/names.txt
```


* Create template WITH inputFile
This produces NO exception.
```
mvn compile exec:java \
     -Dexec.mainClass=org.apache.beam.examples.MyMinimalWordCount \
     -Dexec.args="--runner=DataflowRunner \
                  --project=unimarket-gcp-support \
                  --stagingLocation=gs://unimarket-gcp-support/staging \
                  --templateLocation=gs://unimarket-gcp-support/templates/MyMinimalWordCountTemplate \
                  --inputFile=gs://unimarket-gcp-support/input/names.txt \
                  " 
```

* Verify template was created
```
gsutil ls gs://unimarket-gcp-support/templates
gs://unimarket-gcp-support/templates/MyMinimalWordCountTemplate
```


* Launch with a DIFFERENT inputFile
Output files contain names and NOT colours as expected
```
gcloud beta dataflow jobs run testjob-1 \
    --project=unimarket-gcp-support \
    --gcs-location gs://unimarket-gcp-support/templates/MyMinimalWordCountTemplate \
    --parameters inputFile=gs://unimarket-gcp-support/input/colours.txt,outputFile=gs://unimarket-gcp-support/output/testjob-1-output.txt

createTime: '2018-05-29T02:47:13.792596Z'
currentStateTime: '1970-01-01T00:00:00Z'
id: 2018-05-28_19_47_12-15038788296384834399
location: us-central1
name: testjob-1
projectId: unimarket-gcp-support
type: JOB_TYPE_BATCH
```


* Check on the job progress 
```
gcloud beta dataflow jobs show 2018-05-28_19_47_12-15038788296384834399
creationTime: '2018-05-29 02:47:13'
id: 2018-05-28_19_47_12-15038788296384834399
location: us-central1
name: testjob-1
state: Done
stateTime: '2018-05-29 02:50:14'
type: Batch
```

* Check the output
```
gsutil ls gs://unimarket-gcp-support/output
gs://unimarket-gcp-support/output/testjob-1-output.txt-00000-of-00003
gs://unimarket-gcp-support/output/testjob-1-output.txt-00001-of-00003
gs://unimarket-gcp-support/output/testjob-1-output.txt-00002-of-00003

gsutil cp gs://unimarket-gcp-support/output/testjob-1-output.txt-00000-of-00003 tmp.txt
cat tmp.txt
$ cat tmp.txt
alex: 1
oliver: 1
harris: 1
rolf: 1
alon: 1
```

* Conclusion
The launched job has not used the supplied input.