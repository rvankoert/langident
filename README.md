Langident
=========

This is a language guesser for historical text. It distinguishes between
German, English, French, Latin and Dutch (that is, the seventeenth-century
variants of all those).

There are three models: Cavnar-Trenkle, cumulative frequency and Naive Bayes.
Accuracy scores on a held-out set of "hard" (very short) examples are:

* cavnartrenkle: 224/2969 errors = 92.46% accuracy
* cumfreq: 103/2969 errors = 96.53% accuracy
* naivebayes: 142/2969 errors = 95.22% accuracy


Use as a library
================

To use as a library from Maven, put the following in your pom.xml:

    <repository>
      <id>huygens</id>
      <url>http://maven.huygens.knaw.nl/repository/</url>
      <releases>
        <enabled>true</enabled>
        <updatePolicy>always</updatePolicy>
        <checksumPolicy>warn</checksumPolicy>
      </releases>
      <snapshots>
        <enabled>true</enabled>
        <updatePolicy>always</updatePolicy>
        <checksumPolicy>fail</checksumPolicy>
      </snapshots>
    </repository>

    <dependency>
      <groupId>nl.knaw.huygens.pergamon</groupId>
      <artifactId>langident</artifactId>
      <version>${langident.version}</version>
    </dependency>

Then make a LanguageGuesser object, train it on the built-in training set,
and use it:

    TrainingSet builtin = TrainingSet.getBuiltin();
    Model guesser = new NaiveBayes().train(builtin);
    System.out.println(guesser.predictBest("Welke taal is dit?"));

This should produce "nl" on the standard output stream. More detailed
information is available from the predictScores method.


Web service
===========

To use langident as a web service, build it:

    mvn package

Start the server:

    target/appassembler/bin/langident server

Then try it out, in another terminal window:

    curl http://localhost:8080/ident -d text="C'est quel langue?" | jq .

The [jq](https://stedolan.github.io/jq/) command does pretty-printing of JSON.
It can also give you the highest-scoring language, which is always the first
one listed:

    curl http://localhost:8080/ident -d text="C'est quel langue?" |
        jq .prediction[0].label

Langident has several built-in models, which are available from

    curl http://localhost:8080/ident/models

To use a different model, pass it as a GET parameter:

    curl http://localhost:8080/ident?model=naivebayes -d text="che lingua è?"

The list of known languages is available from the /ident/languages endpoint:

    curl http://localhost:8080/ident/languages?model=cavnartrenkle
