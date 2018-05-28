# Software Praktikum Group 3
## How to compile our Project
To compile our project simply go into the basedirectory and run:  
1. `ant`

This implicitly runs:
1. `ant clean`
2. `ant compile`
3. `ant deploy`
which clears previous `.class` files, compiles the project and creates an executable `.jar` file in the basedirectory.

Ant should use the buildfile `built.xml` by default

## How to run our project
In the basedirectory call:

`java -jar Phteven.jar -s serveraddress -p portnumber`

where `serveraddress` is the address of the server and `portnumber` is the port number.

### All Commandline Parameters:
This is a list of all commandline arguments:

| shortname | longname       | option | is mandatory | default value | description                               |
| --------- | -------------- | ------ | ------------ | ------------- | -----------                               |
| `-s`      | `--server`   | STRING | yes          |               | The server address you want to connect to |
| `-p`      | `--port`     | INT    | yes          |               | The serverport you want to connect to     |
| `-h`      | `--help`     | FLAG   | no           |               | Prints a hopefully helping text |
| `-l`      | `--loglevel` | INT    | no           | 3             | The maximum LogLevel that should be printed. 0:NONE 1:ERROR 2:WARNING 3:INFO 4:DETAIL 5:DEBUG |
| `-o`      | `--log-file` | STRING | no          | sysout         | Specify an output FIle for Logging. Default is sysout. |
|            | `--log-performance`|FLAG|no        |                | Enables performance Logging. Logging the performance can have an Impact on the performance and will generate more output.|
|            | `--log-ext-perf`| FLAG| no          |               | Enables extended performance Logging, which offers more information.|
|            | `--ab-pruning`|FLAG   |no           |               | Disables Alpha-Beta-Pruning               |
    
Arguments with either a STRING or INT option require an additional option after them.

STRING can be anything.

INT has to be a number

Arguments with a FLAG option do not require an extra option.
## Our maps
Our map files can be found in the folder `maps`
