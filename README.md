# gpsi

Java library for automatic spectral index learning from data. Runs correctly on Java 8.

The package can be either compiled as a NetBeans project, or run directly from the JAR files. In any case, you might need to download the JAR files provided in this [link](http://www.recod.ic.unicamp.br/~jalbarracin/gpsi/gpsi.zip).

If you use this code for your research, you may cite our paper:

```
@INPROCEEDINGS{albarracin2016,
	author    = {Juan Albarracín and Jefersson {dos Santos} and Ricardo Torres},
	booktitle = {2016 29th SIBGRAPI Conference on Graphics, Patterns and Images (SIBGRAPI)},
	title     = {Learning to Combine Spectral Indices with Genetic Programming},
	year      = {2016},
	pages     = {408-415},
	doi       = {10.1109/SIBGRAPI.2016.063},
	month     = oct,
	url       = {http://ieeexplore.ieee.org/document/7813062/}
}
```

## Dependencies

The library depends on the next packages:

+ **JGAP:** Java Genetic Algorithms Package <a href="http://jgap.sourceforge.net/" target="_blank">[:link:]</a>
+ **args4j:** Java command line arguments parser <a href="http://args4j.kohsuke.org/" target="_blank">[:link:]</a>
+ **BeanShell:** Lightweight Scripting for Java <a href="http://www.beanshell.org/" target="_blank">[:link:]</a>
+ **Apache Log4j** <a href="https://logging.apache.org/log4j/" target="_blank">[:link:]</a>
+ **Commons Math:** The Apache Commons Mathematics Library <a href="http://commons.apache.org/proper/commons-math/" target="_blank">[:link:]</a>
+ **JMatIO:** Matlab's MAT-file I/O in JAVA <a href="https://sourceforge.net/projects/jmatio/" target="_blank">[:link:]</a>
+ **Weka:** Data Mining Software in Java <a href="http://www.cs.waikato.ac.nz/ml/weka/" target="_blank">[:link:]</a>

Be sure of adding the corresponding versions of the packages to the project. You can get the JAR files [here](http://www.recod.ic.unicamp.br/~jalbarracin/gpsi/gpsi.zip).

## How to use