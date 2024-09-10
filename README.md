# Software Praktikum Group 3
This is a project that we have worked on in a team of three during my Bachelor's degree (2018). The project was about building an AI to compete against other groups in a board game that is similar to Reversi (Othello), but with additional complicating rules and multiple players.

The tasks included building a framework that:
- can simulate the rules of the game
- can communicate with the server 
- implements an aritificial intelligence logic to choose the moves

This AI is "traditional" in the sense that it uses a handcrafted evaluation function together with alpha-beta tree search to choose the next moves. Our resulting agent was able to beat and replace the annual course's all-time best AI, which was reigning for several years before that.

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
|            | `--disable-ab`|FLAG   |no           |               | Disables Alpha-Beta-Pruning               |
|            | `--disable-sort`|FLAg|no            |               | Disables move-sorting                    |
|            | `--disable-itDeep`|FLAg|no            |               | Disables iterative deepening            |
|            | `--asp-window`  | INT  | no          | 15            | Set the aspiration Window size. 0 disables Aspiration Windows |
    
Arguments with either a STRING or INT option require an additional option after them.

STRING can be anything.

INT has to be a number

Arguments with a FLAG option do not require an extra option.
## Our maps
Our map files can be found in the folder `maps`
