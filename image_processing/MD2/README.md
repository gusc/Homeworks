Requirements
============

JDK 8

Build
=====

```
javac Fourier.java
javac Bicubic.java
javac Bilinear.java
```

Run
===

Fourier transform
-----------------

Perform Fourier transform high frequency content filtering:

```
java Fourier <image.png>
```

Image scaling
-------------

Perform a Bilinear or Bicubic scaling:

```
java Bilinear <image.png> [<scale>]
java Bilinear <image.png> [<scale>]
```

scale - a scale fraction (default: 4)
