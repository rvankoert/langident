This is a language guesser for historical text. It distinguishes between
German, English, French, Latin and Dutch (that is, the seventeenth-century
variants of all those).

There are three models: Cavnar-Trenkle, cumulative frequency and Naive Bayes.
Accuracy scores on a held-out set of "hard" (very short) examples are:

cavnartrenkle: 224/2969 errors = 92.46% accuracy
cumfreq: 103/2969 errors = 96.53% accuracy
naivebayes: 142/2969 errors = 95.22% accuracy
