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


Installation
============

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
