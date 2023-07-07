# General info

TestRefactoringMiner is a library/API written in Java that can detect test-specific refactorings applied in the history 
of a Java project. 

TestRefactoringMiner is build on top of [RefactoringMiner](https://github.com/tsantalis/RefactoringMiner), one the most established refactoring mining 
instruments: first integrate the test detection mechanisms proposed in a [pull request](https://github.com/tsantalis/RefactoringMiner/pull/225) 
to the RefactoringMiner repository  in late 2021, secondly, we extended the set of changes reported by RefactoringMiner 
through the detection of additions and removals on test assertions; finally, we used the set of changes to implement
rules for detecting seven test refactoring operations.

# How to cite TestRefactoringMiner

If you are using TestRefactoringMiner in your research, please make reference to the following artifacts:

Victor Veloso, "[Fork of refactoringminer repository](https://github.com/victorgveloso/RefactoringMiner)," 2021, 
Accessed on 05.01.2023. [Online].

    @misc {Fork_RefactoringMiner,
    author = "Victor Veloso",
    title  = "Fork of RefactoringMiner",
    year   = "2021",
    url    ="https://github.com/victorgveloso/RefactoringMiner",
    note = {{A}ccessed on 05.01.2023}
    }

Nikolaos Tsantalis, Matin Mansouri, Laleh Eshkevari, Davood Mazinanian, and Danny Dig, 
"[Accurate and Efficient Refactoring Detection in Commit History](https://users.encs.concordia.ca/~nikolaos/publications/ICSE_2018.pdf)," 
*40th International Conference on Software Engineering* (ICSE 2018), Gothenburg, Sweden, May 27 - June 3, 2018.

    @inproceedings{Tsantalis:ICSE:2018:RefactoringMiner,
	author = {Tsantalis, Nikolaos and Mansouri, Matin and Eshkevari, Laleh M. and Mazinanian, Davood and Dig, Danny},
	title = {Accurate and Efficient Refactoring Detection in Commit History},
	booktitle = {Proceedings of the 40th International Conference on Software Engineering},
	series = {ICSE '18},
	year = {2018},
	isbn = {978-1-4503-5638-1},
	location = {Gothenburg, Sweden},
	pages = {483--494},
	numpages = {12},
	url = {http://doi.acm.org/10.1145/3180155.3180206},
	doi = {10.1145/3180155.3180206},
	acmid = {3180206},
	publisher = {ACM},
	address = {New York, NY, USA},
	keywords = {Git, Oracle, abstract syntax tree, accuracy, commit, refactoring},
    }

Nikolaos Tsantalis, Ameya Ketkar, and Danny Dig, "[RefactoringMiner 2.0](https://users.encs.concordia.ca/~nikolaos/publications/TSE_2020.pdf)," 
*IEEE Transactions on Software Engineering*, vol. 48, no. 3, pp. 930-950, March 2022.

    @article{Tsantalis:TSE:2020:RefactoringMiner2.0,
	author={Tsantalis, Nikolaos and Ketkar, Ameya and Dig, Danny},
	title={RefactoringMiner 2.0},
	journal={IEEE Transactions on Software Engineering},
	year={2022},
	volume={48},
	number={3},
	pages={930-950},
	doi={10.1109/TSE.2020.3007722}
    }


# Current precision and recall

We analyzed the precision and recall of the TestRefactoringMiner on a dataset containing 417 instances of the most 
frequent test-specific refactorings on 13 projects of Apache Foundation from 2019 to 2021.


| Test Refactoring Type |  TP |  FP |  FN | Precision | Recall |
|:----------------------|----:|----:|----:|----------:|-------:|
| **Total**             | 373 |   0 |  44 |      1.00 |   0.92 |
| Add explanation message |  63 |   0 |  18 |      1.00 |   0.78 |
| Replace reserved words |   9 |   0 |   0 |      1.00 |   1.00 |
| Split conditional parameters |   7 |   0 |   0 |      1.00 |   1.00 |
| Replace the not (!) operator  |   2 |   0 |   0 |      1.00 |   1.00 |
| Replace try/catch with assertThrows |  40 |   0 |   5 |      1.00 |   0.89 | 
| Replace @Rule annot. with assertThrows |  15 |   0 |   2 |      1.00 |   0.88 |
| Replace @Test annot. with assertThrows | 237 |   0 |  19 |      1.00 |   0.93 |


We did not analyze the precision and recall for the previous refactorings implemented by RefactoringMiner. 
We rely on the evaluation performed by the tool's authors on October 17, 2022. The authors calculated the precision and 
recall using an oracle with 12,073 refactorings from 187 open-source projects.

| Refactoring Type | TP | FP | FN | Precision | Recall |
|:-----------------------|-----------:|--------:|--------:|--------:|--------:|
|**Total**|11330  | 23  | 288  | 0.998  | 0.975|
|Extract Method|959  |  1  | 31  | 0.999  | 0.969|
|Rename Class|53  |  0  |  2  | 1.000  | 0.964|
|Move Attribute|242  |  4  | 10  | 0.984  | 0.960|
|Move And Rename Attribute|12  |  0  |  0  | 1.000  | 1.000|
|Replace Attribute|10  |  0  |  0  | 1.000  | 1.000|
|Rename Method|350  |  4  | 32  | 0.989  | 0.916|
|Inline Method|109  |  0  |  2  | 1.000  | 0.982|
|Move Method|352  |  3  |  9  | 0.992  | 0.975|
|Move And Rename Method|115  |  0  |  6  | 1.000  | 0.950|
|Pull Up Method|290  |  0  |  6  | 1.000  | 0.980|
|Move Class|1093  |  0  |  4  | 1.000  | 0.996|
|Move And Rename Class|34  |  0  |  1  | 1.000  | 0.971|
|Move Source Folder| 3  |  0  |  0  | 1.000  | 1.000|
|Pull Up Attribute|128  |  0  |  1  | 1.000  | 0.992|
|Push Down Attribute|33  |  0  |  0  | 1.000  | 1.000|
|Push Down Method|43  |  0  |  1  | 1.000  | 0.977|
|Extract Interface|22  |  0  |  0  | 1.000  | 1.000|
|Extract Superclass|73  |  0  |  0  | 1.000  | 1.000|
|Extract Subclass| 4  |  0  |  0  | 1.000  | 1.000|
|Extract Class|96  |  0  |  0  | 1.000  | 1.000|
|Extract And Move Method|99  |  0  | 69  | 1.000  | 0.589|
|Move And Inline Method|16  |  0  |  4  | 1.000  | 0.800|
|Rename Package|16  |  0  |  0  | 1.000  | 1.000|
|Move Package|10  |  0  |  0  | 1.000  | 1.000|
|Extract Variable|204  |  0  |  0  | 1.000  | 1.000|
|Extract Attribute|16  |  0  |  0  | 1.000  | 1.000|
|Inline Variable|75  |  0  |  0  | 1.000  | 1.000|
|Inline Attribute| 7  |  0  |  0  | 1.000  | 1.000|
|Rename Variable|296  |  3  | 13  | 0.990  | 0.958|
|Rename Parameter|471  |  2  | 28  | 0.996  | 0.944|
|Rename Attribute|128  |  0  | 16  | 1.000  | 0.889|
|Merge Variable| 5  |  0  |  0  | 1.000  | 1.000|
|Merge Parameter|28  |  0  |  0  | 1.000  | 1.000|
|Merge Attribute| 5  |  0  |  0  | 1.000  | 1.000|
|Split Variable| 1  |  0  |  0  | 1.000  | 1.000|
|Split Parameter| 8  |  0  |  0  | 1.000  | 1.000|
|Split Attribute| 2  |  0  |  0  | 1.000  | 1.000|
|Replace Variable With Attribute|20  |  0  |  0  | 1.000  | 1.000|
|Parameterize Variable|72  |  0  |  0  | 1.000  | 1.000|
|Localize Parameter|20  |  0  |  0  | 1.000  | 1.000|
|Parameterize Attribute|23  |  0  |  0  | 1.000  | 1.000|
|Change Return Type|419  |  0  | 12  | 1.000  | 0.972|
|Change Variable Type|763  |  2  | 11  | 0.997  | 0.986|
|Change Parameter Type|629  |  1  | 16  | 0.998  | 0.975|
|Change Attribute Type|223  |  0  |  8  | 1.000  | 0.965|
|Add Method Annotation|327  |  0  |  4  | 1.000  | 0.988|
|Remove Method Annotation|99  |  0  |  0  | 1.000  | 1.000|
|Modify Method Annotation|29  |  0  |  0  | 1.000  | 1.000|
|Add Attribute Annotation|62  |  0  |  1  | 1.000  | 0.984|
|Remove Attribute Annotation|18  |  0  |  0  | 1.000  | 1.000|
|Modify Attribute Annotation| 7  |  0  |  0  | 1.000  | 1.000|
|Add Class Annotation|52  |  0  |  0  | 1.000  | 1.000|
|Remove Class Annotation|20  |  0  |  0  | 1.000  | 1.000|
|Modify Class Annotation|32  |  0  |  0  | 1.000  | 1.000|
|Add Parameter Annotation|32  |  0  |  0  | 1.000  | 1.000|
|Remove Parameter Annotation| 3  |  0  |  0  | 1.000  | 1.000|
|Modify Parameter Annotation| 2  |  0  |  0  | 1.000  | 1.000|
|Add Parameter|937  |  2  |  1  | 0.998  | 0.999|
|Remove Parameter|334  |  0  |  0  | 1.000  | 1.000|
|Reorder Parameter| 9  |  0  |  0  | 1.000  | 1.000|
|Add Variable Annotation| 1  |  0  |  0  | 1.000  | 1.000|
|Remove Variable Annotation| 3  |  0  |  0  | 1.000  | 1.000|
|Add Thrown Exception Type|39  |  0  |  0  | 1.000  | 1.000|
|Remove Thrown Exception Type|246  |  0  |  0  | 1.000  | 1.000|
|Change Thrown Exception Type| 9  |  0  |  0  | 1.000  | 1.000|
|Change Method Access Modifier|319  |  0  |  0  | 1.000  | 1.000|
|Change Attribute Access Modifier|220  |  0  |  0  | 1.000  | 1.000|
|Encapsulate Attribute|48  |  0  |  0  | 1.000  | 1.000|
|Add Method Modifier|79  |  0  |  0  | 1.000  | 1.000|
|Remove Method Modifier|100  |  0  |  0  | 1.000  | 1.000|
|Add Attribute Modifier|134  |  0  |  0  | 1.000  | 1.000|
|Remove Attribute Modifier|141  |  1  |  0  | 0.993  | 1.000|
|Add Variable Modifier|128  |  0  |  0  | 1.000  | 1.000|
|Remove Variable Modifier|57  |  0  |  0  | 1.000  | 1.000|
|Change Class Access Modifier|77  |  0  |  0  | 1.000  | 1.000|
|Add Class Modifier|34  |  0  |  0  | 1.000  | 1.000|
|Remove Class Modifier|44  |  0  |  0  | 1.000  | 1.000|
|Split Package| 4  |  0  |  0  | 1.000  | 1.000|
|Merge Package| 2  |  0  |  0  | 1.000  | 1.000|
|Change Type Declaration Kind| 6  |  0  |  0  | 1.000  | 1.000|
|Collapse Hierarchy| 1  |  0  |  0  | 1.000  | 1.000|
|Replace Loop With Pipeline|35  |  0  |  0  | 1.000  | 1.000|
|Replace Pipeline With Loop| 2  |  0  |  0  | 1.000  | 1.000|
|Replace Anonymous With Lambda|45  |  0  |  0  | 1.000  | 1.000|
|Merge Class| 6  |  0  |  0  | 1.000  | 1.000|
|Split Class| 3  |  0  |  0  | 1.000  | 1.000|
|Split Conditional| 7  |  0  |  0  | 1.000  | 1.000|


# How to run TestRefactoringMiner

## Requirements

To run the TestRefactoringMiner, you need a runtime environment compatible with Java 17.0.7. 
1. Download the corresponding [Java 17.0.7](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) for your operational system.
2. Configure the environment variables by setting the ``jdk-17`` into ``JAVA_HOME`` and ``Path`` variables.
3. Open the CMD and check your Java version 
```
> java -version
java version "17.0.7" 2023-04-18 LTS
Java(TM) SE Runtime Environment (build 17.0.7+8-LTS-224) 
Java HotSpot(TM) 64-Bit Server VM (build 17.0.7+8-LTS-224, mixed mode, sharing)
```

Please, refer to the [Oracle documentation](https://docs.oracle.com/javase/7/docs/webnotes/install/index.html) for more information.

## Downloading and configuring the TestRefactoringMiner tool

To download and configure the distribution file of the RefactoringMiner tool’s extension, please follow the steps:

1. Download the distribution file of the [TestRefactoringMiner](https://figshare.com/s/6c260ea8ef1ca45b1f6d) tool’s extension
2. Extract the file RefactoringMiner-2.3.2 to the desired location
   (e.g., ``C:Users\..\RefactoringMiner-2.3.2``)
3. Configure the environment variables by setting the ``bin`` folder of TestRefactoringMiner into the ``Path`` variable
   (e.g., ``C:Users\..\RefactoringMiner-2.3.2\bin``)
4. Open the CMD and run the command to show TestRefactoringMiner usage:
```
> RefactoringMiner -h
```

## Running TestRefactoringMiner tool via command line


In order to mine the refactorings performed in a project, please follow the steps:

1. Clone the project from the GitHub repository
2. Run the tool via the command line using one of the commands
```
> RefactoringMiner -h
-h                                                                                      Show options
-a <git-repo-folder> <branch> -json <path-to-json-file>                                 Detect all refactorings at <branch> for <git-repo-folder>.
-am <git-repo-folder> <branch> <mode> -json <path-to-json-file>                         Detect all refactorings at <branch> for <git-repo-folder>. Specify the <mode> as T to detect refactorings in the test code and P in the production code.
-bc <git-repo-folder> <start-commit-sha1> <end-commit-sha1> -json <path-to-json-file>   Detect refactorings between <start-commit-sha1> and <end-commit-sha1> for project <git-repo-folder>
-bt <git-repo-folder> <start-tag> <end-tag> -json <path-to-json-file>                   Detect refactorings between <start-tag> and <end-tag> for project <git-repo-folder>
-btm <git-repo-folder> <start-tag> <end-tag> <mode> -json <path-to-json-file>           Detect refactorings between <start-tag> and <end-tag> for project <git-repo-folder>. Specify the <mode> as T to detect refactorings in the test code and P in the production code.
-c <git-repo-folder> <commit-sha1> -json <path-to-json-file>                            Detect refactorings at specified commit <commit-sha1> for project <git-repo-folder>
-gc <git-URL> <commit-sha1> <timeout> -json <path-to-json-file>                         Detect refactorings at specified commit <commit-sha1> for project <git-URL> within the given <timeout> in seconds. All required information is obtained directly from GitHub using the OAuth token in github-oauth.properties
-gp <git-URL> <pull-request> <timeout> -json <path-to-json-file>                        Detect refactorings at specified pull request <pull-request> for project <git-URL> within the given <timeout> in seconds for each commit in the pull request. All required information is obtained directly from GitHub using the OAuth token in github-oauth.properties
```

For example, run the commands to analyze a locally cloned repository:

    > git clone https://github.com/danilofes/refactoring-toy-example.git refactoring-toy-example
    > ./RefactoringMiner -c refactoring-toy-example 36287f7c3b09eff78395267a3ac0d7da067863fd


# Contributors

TestRefactoringMiner reuses:

The code in package **gr.uom.java.xmi.** is developed by [Nikolaos Tsantalis](https://github.com/tsantalis).

The code in package **org.refactoringminer.** was initially developed by [Danilo Ferreira e Silva](https://github.com/danilofes) and later extended by [Nikolaos Tsantalis](https://github.com/tsantalis).

The classes ``ExpectedAnnotationToAssertThrowsDetection``,
``ExpectedAnnotationToAssertThrowsRefactoring``, ``TryWithFailToExpectedExceptionRuleDetection``,
``TryWithFailToExpectedExceptionRuleRefactoring``, and ``TestOperationDiff`` in package **gr.uom.java.xmi.diff** 
were developed by [Victor Veloso](https://github.com/victorgveloso).
