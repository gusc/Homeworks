Requirements
============

JDK 8

Build
=====

```
javac Hue.java
javac Bicubic.java
javac Bilinear.java
```

Run
===

Hue rotation
------------

Rotate image HSI color space Hue component:

```
java Hue <image.png> [<hue_angle_in_degrees>]
```

hue_angle_in_degrees - the rotation amount (default: 0)

Edge detection
--------------

Find edges in the image:

```
java Edge <image.png> [<method>]
```

method - one of these methods:

* laplace - using Laplacian operator
* sobel - using Sobel operator
* prewit - using Prewit operator

(default: laplace)

SUSAN filter
------------

Apply SUSAN filter (blur noise, sharpen edges):

```
java SUSAN <image.png> [<sigma> [<tau>]]
```

sigma - coefficient of Gausian blur
tau - coefficient of edge sharpening

Image compression
-----------------

Compress image using Paeth predictor and Huffman coding:

```
java Compress <image.png> compress
```

Decompress the resulting .cim file into .png:

```
java Compress <image.png> decompress
```

