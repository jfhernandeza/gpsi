# gpsi

Java library for Genetic-Programming-based spectral index learning from data at pixel level. Runs correctly on Java 8.

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

+ **JGAP:** Java Genetic Algorithms Package [:link:](http://jgap.sourceforge.net/)
+ **args4j:** Java command line arguments parser [:link:](http://args4j.kohsuke.org/)
+ **BeanShell:** Lightweight Scripting for Java [:link:](http://www.beanshell.org/)
+ **Apache Log4j** [:link:](https://logging.apache.org/log4j/)
+ **Commons Math:** The Apache Commons Mathematics Library [:link:](http://commons.apache.org/proper/commons-math/)
+ **JMatIO:** Matlab's MAT-file I/O in JAVA [:link:](https://sourceforge.net/projects/jmatio/)
+ **Weka:** Data Mining Software in Java [:link:](http://www.cs.waikato.ac.nz/ml/weka/)

Be sure of adding the corresponding versions of the packages to the project. You can get the JAR files [here](http://www.recod.ic.unicamp.br/~jalbarracin/gpsi/gpsi.zip).

## How to use

### Compile

If you want to compile the source code, the most straightforward way is to clone this repository from [Netbeans](https://netbeans.org/kb/docs/ide/git.html#clone). The last version of Netbeans in which this code was tested was the 8.2. The dependency problems can be solved by associating the JAR files provided above.

### Execute

The execution of **gpsi.jar** must include some arguments to work. Here is a [StackOverflow entr0]y(https://stackoverflow.com/questions/9883918/how-change-build-options-in-netbeans) that quickly shows how to add arguments in execution time of the project.

If you go for the command-line option, make sure that you have the next file structure:

```
-root_folder
  |-lib
  |  |- <JAR files of the dependencies>
  |-gpsi.jar

```
The execution command is:

    $ java -jar gpsi.jar <list of arguments>

#### Arguments

With the arguments you can specify things such as the paths to the datasets and details about the GP algorithm. A list of the arguments is presented bellow:

| Flag | Description | Data type | Default value |
|-|-|-|-|
| -dataset | Path to the scene | String |  |
| -out | Path where the results must be saved | String |  |
| -popSize | Population size | int | 10 |
| -numGens | Number of generations | int | 50 |
| -maxInitDepth | Max initial depth of trees | int | 6 |
| -maxDepth | Max depth of trees | int | 100 |
| -maxNodes | Max number of nodes of the trees | int | 500 |
| -crossRate | Crossover probability | double | 0.9 |
| -mutRate | Mutation probability | double | 0.1 |
| -val | Number of individuals used for validation | int | 0 |
| -bootstrap | Number of boostrapped samples during evolution | double | 0 |
| -dumpGens | Whether to save the distribution of samples through generations | boolean |  |
| -score | Distance measure to be considered in the fitness function | String | "None" |
| -errorScore | Threshold of error score to consider a pixel. This value must be in the last spectral band. | double | 0.0 |
| -seed | Seed of the algorithm | long |  |

The flags **-dataset** and **-out** are mandatory, so you always have to specify the paths of the dataset and where you want the results to be stored.

Here are some comments of the non-self-explanatory flags:

+ **-val** asks whether to use the correction strategy with validation set proposed by [Torres et al. 2016]()