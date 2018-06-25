package swpg3.main.cli;

import java.util.ArrayList;
import java.util.List;

public class CliParser {
	private List<CliOption> options;

	public CliParser()
	{
		options = new ArrayList<CliOption>();
		addOption('h', "help", false, CliOptionType.FLAG, "", "Prints help text");
	}

	public void addOption(CliOption opt)
	{
		options.add(opt);
	}

	public void addOption(char shortName, String longName, boolean mandatory, CliOptionType type, String defaultParam,
			String description)
	{
		options.add(new CliOption(shortName, longName, mandatory, type, defaultParam, description));
	}

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
