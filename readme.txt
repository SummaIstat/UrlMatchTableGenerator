***************************************************
*****     UrlMatchTableGenerator readme       *****
***************************************************

======================================================================
What is UrlMatchTableGenerator and what is it intended to be used for
======================================================================

UrlMatchTableGenerator is a Java program that merges the CSV file obtained as output from the program UrlScorer 
with a list of correct enterprises' sites known in advance.

The CSV file obtained by UrlScorer program is the input for the machine learning program that will try to predict 
the correct URL for each firm; in order to be able to accomplish this task, just the first time, you have to instruct 
the learner providing it a train set (a similar CSV file containing the extra boolean column “is the found url correct"). 

The objective of this program is to create this train set, once the learner will be instructed it will be able to 
“recognize” the correct url for a firm without knowing the real official site in advance (as in the train phase); 
in other words you will be able to use directly the UrlScorer output file (and skip this step).

======================================================================
How is the project folder made
======================================================================

The UrlMatchTableGenerator folder is an Eclipse project ready to be run or modified in the IDE (you just have to import the project "as an existing project" and optionally change some configuration parameters in the code).

Ignoring the hidden directories and the hidden files, under the main directory you can find 4 subdirectories :
1) src => contains the source code of the program
2) bin => contains the compiled version of the source files
3) lib => contains the jar files (libraries) that the program needs
4) sandbox => contains the executable jar file that you have to use in order to launch the program and some test input files that you can modify on the basis of your needs

As you probably already know it is definitely not a good practice to put all this stuff into a downloadable project folder, but i decided to break the rules in order to facilitate your job. Having all the stuff that will be necessary in just one location and by following the instructions you should be able to test the whole environment in 5-10 minutes.

======================================================================
How to execute the program on your PC by using the executable jar file
======================================================================

If you have Java already installed on your PC you just have to apply the following instruction points:

1) create a folder on your filesystem (let's say "myDir")

2) copy the following files from sandbox directory to "myDir" :
	
	firmsInfo.txt
	linkScores.txt
	UrlMatchTableGenerator.jar
	umtgConf.properties 
	
3) customize the parameters inside the umtgConf.properties file :
	    
    Change the value of the parameters under the "paths section" according with the position of the files and folders on your filesystem.
    
4) customize the content of the firmsInfo.txt file :

	Each line in this file represents a firm, the format of each line must be the following:
	
	firm_id		TAB		firm_name	TAB		official_url
	
	eg:
	12345	MyFirmName	http://www.myfirm.com

5) customize the content of the linkScores.txt file :

	You should use the content of the UrlScorer output file, the format of each line must be the following:
	
	FIRM_ID				TAB	
	LINK_POSITION		TAB
	URL					TAB	
	SCORE_VECTOR		TAB
	SCORE
		
	eg:
	12345	1	http://www.firm1.com/	1190110	656
	54321	4	http://www.firm2.net/aboutus	1060000	0
	
6) open a terminal, go into the myDir directory, type and execute the following command:

        java -jar UrlMatchTableGenerator.jar umtgConf.properties

7) at the end of the program execution you should find inside the directory myDir a file called matchTable_[dateTime].csv

	The format of the csv file (actually it will be a TSV file) will be the following :

	FIRM_ID						TAB		the id of the firm
	POS_LINK					TAB		the position of the link within the result list provided by the search engine in the UrlSearcher step
	FIRM_NAME					TAB		the name of the firm
	URL_WE_HAD					TAB		the official URL of the firm that we already know
	URL_WE_FOUND				TAB		one of the URLs associated to the FIRM_NAME by the search engine in the UrlSearcher step
	SCORE_VECTOR				TAB		the score vector computed for the link by UrlScorer 
	SCORE						TAB		the score computed for the link by UrlScorer
	CALC_DOMAIN					TAB		calculated domain from URL_WE_HAD
	MATCH_STRICT				TAB     1 if the CALC_DOMAIN string matches exactly the domain part of the URL_WE_FOUND string, 0 otherwise
	MATCH_DOMAIN				TAB		1 if the CALC_DOMAIN string is contained in the URL_WE_FOUND string, 0 otherwise	
	MATCH_DOMAIN_NO_EXT					1 if the CALC_DOMAIN string without the extension (eg. .it, .com, .net) is contained in the URL_WE_FOUND string, 0 otherwise
	


======================================================================
LINUX			
======================================================================

If you are using a Linux based operating system open a terminal and type on a single line :

java -jar 
-Xmx_AmountOfRamMemoryInMB_m
/path_of_the_directory_containing_the_jar/UrlMatchTableGenerator.jar 
/path_of_the_directory_containing_the_conf_file/umtgConf.properties

eg:

java -jar -Xmx2048m UrlMatchTableGenerator.jar umtgConf.properties

java -jar -Xmx1024m /hourlScorerConfme/summa/workspace/UrlMatchTableGenerator/sandbox/UrlMatchTableGenerator.jar /home/summa/workspace/UrlMatchTableGenerator/sandbox/umtgConf.properties


======================================================================
WINDOWS			
======================================================================

If you are using a Windows based operating system you just have to do exactly the same, the only difference is that you have to substitute the slashes "/" with the backslashes "\" in the filepaths.

eg:

java -jar -Xmx1536m C:\workspace2\UrlMatchTableGenerator\sandbox\UrlMatchTableGenerator.jar C:\workspace2\UrlMatchTableGenerator\sandbox\umtgConf.properties

======================================================================
LICENSING
======================================================================

This software is released under the European Union Public License v. 1.2
A copy of the license is included in the project folder.

======================================================================
Considerations
======================================================================

This program is still a work in progress so be patient if it is not completely fault tolerant; in any case feel
free to contact me (donato.summa@istat.it) if you have any questions or comments.
