---
layout: post
title: Installation
permalink: /install/
---

``Par4Sem`` can be installed in three different ways. All options are accomplished using docker

1. Use resources and database dumps on local Machine.

      All the resource files such as phrase2vec emmedings, OpenNLP models for parts-of-speech tagging, tokenizations, and sentence splitting, Wikipedia frequency countts are available [here](http://ltdata1.informatik.uni-hamburg.de/par4sem/resources/). Database dumps for the different paraphrase resources such as PPDB 2.0, JoBimText DT, WordNet, SimplePPDB and CWI datasets are available [here](http://ltdata1.informatik.uni-hamburg.de/par4sem/database/). The learning to rank dataset (akk usage data) collected using the ``Par4Sem`` are available [here](http://ltdata1.informatik.uni-hamburg.de/par4sem/datasets/).
      To start ``Par4Sem``:
      *  Download the ``docker-compose`` file from [here]
      * Download the database dump, unzip and put them under ``dumps`` folder
      * download the resources file, unzip and put them under ``data`` folder
      * start the docker as ``docker-compose up -d``

2. Only frontend parts


3. From sources
