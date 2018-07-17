package swpg3.main.cli;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple Parser that parses the commandline arguments.
 * 
 * All available options must be supplied via addOption method. After that the
 * cmd args can be parsed with the parse method. All parsed information will be
 * stored in the cliOption class.
 * 
 * @author eric
 *
 */
public class CliParser {
	private List<CliOption> options;

	/**
	 * Simple constructor. Initializes the list of options and adds the -h (Help)
	 * option.
	 */
	public CliParser()
	{
		options = new ArrayList<CliOption>();
		addOption('h', "help", false, CliOptionType.FLAG, "", "Prints help text");
	}

	/**
	 * Add an already existing option to be parsed.
	 * 
	 * @param opt
	 *            Option that should be parsed.
	 */
	public void addOption(CliOption opt)
	{
		options.add(opt);
	}

	/**
	 * Creates a new CliOption. Data can be accessed by using getOption-method after
	 * parsing.
	 * 
	 * @param shortName
	 * @param longName
	 * @param mandatory
	 * @param type
	 * @param defaultParam
	 * @param description
	 */
	public void addOption(char shortName, String longName, boolean mandatory, CliOptionType type, String defaultParam,
			String description)
	{
		options.add(new CliOption(shortName, longName, mandatory, type, defaultParam, description));
	}

	/**
	 * Parses the commandline arguments and fills all options added to the parser.
	 * 
	 * If an error occurred while parsing that renders the program unusable, like a
	 * missing mandatory parameter, false will be returned. If parsing is completed
	 * successfully true will be returned.
	 * 
	 * @param args the args supplied via commandline.
	 * @return true, if parsing went correctly; false, if an error occurred.
	 */
	public boolean parse(String[] args)
	{
		int i = 0;
		while (i < args.length)
		{
			String curCli = args[i];
			if (curCli.charAt(0) != '-')
			{
				System.out.println("Expected - !");
				printHelp();
				return false;
			}
			if (curCli.length() == 1)
			{
				System.out.println("Expected more than: - !");
				printHelp();
				return false;
			}
			if (curCli.charAt(1) != '-') // shortoption
			{
				if (curCli.length() != 2)
				{
					System.out.println("Invalid Syntax in: " + curCli + "\nExpected only one option!");
					printHelp();
					return false;
				}
				char sname = curCli.charAt(1);
				boolean set = false;
				for (CliOption opt : options)
				{
					if (opt.getShortName() != ' ' && opt.getShortName() == sname)
					{
						switch (opt.getType())
						{
							case FLAG:
								set = true;
								opt.set();
								break;
							case STRINGPARAM:
								if (args.length <= i + 1)
								{
									System.out.println("Expected parameter after: " + curCli);
									printHelp();
									return false;
								}
								set = true;
								opt.setParam(args[i + 1]);
								i++;
								break;
							case INTPARAM:
								if (args.length <= i + 1)
								{
									System.out.println("Expected parameter after: " + curCli);
									printHelp();
									return false;
								}
								set = true;
								if (!opt.setParam(args[i + 1]))
								{
									System.out.println("Expected number after: " + curCli);
									printHelp();
									return false;
								}
								i++;
								break;
						}
					}
				}
				if (!set)
				{
					System.out.println("Unknown Parameter: " + curCli);
					printHelp();
					return false;
				}
			} else // longname
			{
				String lname = curCli.substring(2, curCli.length());
				boolean set = false;
				for (CliOption opt : options)
				{
					if (!opt.getLongName().equals("") && opt.getLongName().equals(lname))
					{
						switch (opt.getType())
						{
							case FLAG:
								set = true;
								opt.set();
								break;
							case STRINGPARAM:
								if (args.length <= i + 1)
								{
									System.out.println("Expected parameter after: " + curCli);
									printHelp();
									return false;
								}
								set = true;
								opt.setParam(args[i + 1]);
								i++;
								break;
							case INTPARAM:
								if (args.length <= i + 1)
								{
									System.out.println("Expected parameter after: " + curCli);
									printHelp();
									return false;
								}
								set = true;
								if (!opt.setParam(args[i + 1]))
								{
									System.out.println("Expected number after: " + curCli);
									printHelp();
									return false;
								}
								i++;
								break;
						}
					}
				}
				if (!set)
				{
					System.out.println("Unknown Parameter: " + curCli);
					printHelp();
					return false;
				}
			}
			i++;
		}
		boolean allMand = true;
		boolean helpSet = getOption('h').isSet();
		// If help - FLAG not specified, check if all mandatory arguments are given
		for (CliOption opt : options)
		{
			if (opt.isMandatory() && !(opt.isSet() || opt.isGiven()))
			{
				if (!helpSet)
				{
					System.out.println("PARSE-ERROR: Mandatory parameter " + opt.shortString() + " is missing!");
				}
				allMand = false;
			}
		}

		if (!allMand || helpSet)
		{
			printHelp();
		}
		return allMand;
	}

	/**
	 * Prints a help text to better use application.
	 * This text contains a list of all options and their description, that are listed in the parser.
	 */
	private void printHelp()
	{
		System.out.println("\nCall with these mandatory Arguements:");
		System.out.format(" %-6s %-17s %-8s %s\n", "short", "long", "prmType", "description");
		for (CliOption opt : options)
		{
			if (opt.isMandatory())
			{
				System.out.println(opt);
			}
		}

		System.out.println("\nAnd these optional Arguements:");
		System.out.format(" %-6s %-17s %-8s %s\n", "short", "long", "prmType", "description");
		for (CliOption opt : options)
		{
			if (!opt.isMandatory())
			{
				System.out.println(opt);
			}
		}

	}

	/**
	 * Retrieve an option listed by the parser by its short name.
	 * @param shortname
	 * @return
	 */
	public CliOption getOption(char shortname)
	{
		for (CliOption cliOption : options)
		{
			if (cliOption.getShortName() == shortname)
			{
				return cliOption;
			}
		}
		return null;
	}
	
	/**
	 * Retrieve an option listed by the parser by its long name.
	 * @param shortname
	 * @return
	 */
	public CliOption getOption(String longname)
	{
		for (CliOption cliOption : options)
		{
			if (cliOption.getLongName() == longname)
			{
				return cliOption;
			}
		}
		return null;
	}
}
