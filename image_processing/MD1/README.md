Requirements
============

JDK 8

Build
=====

```
javac Gauss.java
javac Histogram.java
```

Run
===

Apply histogram normalization:


```
java Histogram <image.png>
```

Perform a Gaussian blur:

```
java Gauss <image.png> <sigma>
```

sigma - a coefficient for Gauss function (blur amount)
