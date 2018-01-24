Requirements
============

All programs are written in Java using JDK 8

Gaussian blur
=============

Perform a Gaussian blur on an image.

Build
-----

```
javac Gauss.java
```

Run
---

```
java Gauss <image.png> <sigma>
```

* sigma - a coefficient for Gauss function (blur amount)

Histogram normalization
=======================

Apply histogram normalization on an image.

Build
-----

```
javac Histogram.java
```

Run
---

```
java Histogram <image.png>
```

Bicubic and Bilinear scaling
============================

Perform a bicubic or bilinear scaling of an image.

Build
-----

```
javac Bicubic.java
javac Bilinear.java
```

Run
---

```
java Bilinear <image.png> [<scale>]
java Bilinear <image.png> [<scale>]
```

* scale - a scale fraction (default: 4)

Fourier transform
=================

Perform Fourier transform high frequency content filtering on an image.

Build
-----

```
javac Fourier.java
```

Run
---

```
java Fourier <image.png>
```

Edge detection
==============

Run different edge detection algorithms on an image.

Build
-----

```
javac Edge.java
```

Run
---

```
java Edge <image.png> [<method>]
```

* method - one of these methods:
  * laplace - using Laplacian operator
  * sobel - using Sobel operator
  * prewit - using Prewit operator

SUSAN filter
============

Apply SUSAN filter (blur noise, sharpen edges):

Build
-----

```
javac SUSAN.java
```

Run
---

```
java SUSAN <image.png> [<sigma> [<tau>]]
```

* sigma - coefficient of Gaussian blur
* tau - coefficient of edge sharpening

Hue rotation
============

Convert image into HSI color space and rotate Hue component by some degree.

Build
-----

```
javac Hue.java
```

Run
---

```
java Hue <image.png> [<angle_in_degrees>]
```

* angle_in_degrees - 0-360 (default: 0)

Image compression
=================

Implementation of Paeth predictor and Huffman coding to do a lossless image compression.

Build
-----

```
javac Compress.java
```

Run
---

To perform the compression of any image:

```
java Compress <image.png> compress
```

Decompress the resulting .cim file into .png:

```
java Compress <image.png> decompress
```  
