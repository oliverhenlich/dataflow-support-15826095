/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.examples;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.*;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class MyMinimalWordCount {
    private static final Logger LOG = LoggerFactory.getLogger(MyMinimalWordCount.class);

    public interface WordCountOptions extends PipelineOptions {
        @Description("Path of the file to read from")
        @Default.String("gs://catalogue.unimarket.com/TESTING/input/names.txt")
        ValueProvider<String> getInputFile();

        void setInputFile(ValueProvider<String> value);

        @Description("Path of the file to write to")
        ValueProvider<String> getOutputFile();

        void setOutputFile(ValueProvider<String> value);
    }

    public static void main(String[] args) {

        WordCountOptions options = PipelineOptionsFactory.fromArgs(args).withValidation()
                .as(WordCountOptions.class);

        Pipeline p = Pipeline.create(options);

        p.apply(TextIO.read().from(options.getInputFile()))

                // NOTE: Only added this to log the contents of the inputFile value provider
                .apply(ParDo.of(new DoFn<String, String>() {
                    @StartBundle
                    public void startBundle(StartBundleContext c) {
                        WordCountOptions pipelineOptions = c.getPipelineOptions().as((WordCountOptions.class));
                        LOG.info("inputFile={}", pipelineOptions.getInputFile().get());
                    }

                    @ProcessElement
                    public void processElement(ProcessContext c) {
                        c.output(c.element());
                    }
                }))
                .apply(FlatMapElements
                        .into(TypeDescriptors.strings())
                        .via((String word) -> Arrays.asList(word.split("[^\\p{L}]+"))))
                .apply(Filter.by((String word) -> !word.isEmpty()))
                .apply(Count.perElement())
                .apply(MapElements
                        .into(TypeDescriptors.strings())
                        .via((KV<String, Long> wordCount) -> wordCount.getKey() + ": " + wordCount.getValue()))
                .apply(TextIO.write().to(options.getOutputFile()));

        p.run();
    }
}
