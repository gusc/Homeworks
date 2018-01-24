Requirements
============

Xcode 8+

Hamming code
=============

Test Hamming code (7,4) encoding, error detection, error correction and decoding. Simply run the "hamming" target in the project.

CRC32
=======================

Test CRC32 encoding and error detection. Simply run the "crc" target in the project.

Message signing using RSA
============================

Perform a message signing using RSA and MD5 algorithms. MD5 is used to generate checksum, while RSA is used to validate the checksum.

Build
-----

```
cd signature
make clean && make all
```

Run
---

First you need to generate keys:

```
signature key-gen
```

This will create key pair (key.pub and key.priv) in your working directory. Send the key.pub to your friend and you're ready to sign the message:

```
signature sign <message.file> key.priv
```

This will generate a signature.dat file. When your friend receives the message and the signature.dat he/she can validate it using command:

```
signature validate <mesage.file> key.pub signature.dat
```
