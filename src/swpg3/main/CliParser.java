package swpg3.main;

import java.util.ArrayList;
import java.util.List;

public class CliParser {
	private List<CliOption> options;

	public CliParser()
	{
		options = new ArrayList<CliOption>();
		addOption('h', "help", false, CliOptionType.FLAG, "", "Print help text");
	}

	public void addOption(CliOption opt)
	{
		options.add(opt);
	}

	public void addOption(char shortName, String longName, boolean mandatory, CliOptionType type, String defaultParam, String description)
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

			if (curCli.charAt(1) != '-') // shortoption
			{
				if(curCli.length() != 2)
				{
					System.out.println("Invalid Syntax in: " + curCli + "\nExpected only one option!");
					printHelp();
					return false;
				}
				char sname = curCli.charAt(1);
				boolean set = false;
				for(CliOption opt : options)
				{
					if(opt.getShortName() == sname)
					{
						switch(opt.getType())
						{
							case FLAG:
								set = true;
								opt.set();
								break;
							case STRINGPARAM:
								if(args.length <= i+1)
								{
									System.out.println("Expected parameter after: " + curCli);
									printHelp();
									return false;
								}
								set = true;
								opt.setParam(args[i+1]);
								i++;
								break;
							case INTPARAM:
								if(args.length <= i+1)
								{
									System.out.println("Expected parameter after: " + curCli);
									printHelp();
									return false;
								}
								set = true;
								if(!opt.setParam(args[i+1]))
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
				if(!set)
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
					if(opt.getLongName().equals(lname))
					{
						switch(opt.getType())
						{
							case FLAG:
								set = true;
								opt.set();
								break;
							case STRINGPARAM:
								if(args.length <= i+1)
								{
									System.out.println("Expected parameter after: " + curCli);
									printHelp();
									return false;
								}
								set = true;
								opt.setParam(args[i+1]);
								i++;
								break;
							case INTPARAM:
								if(args.length <= i+1)
								{
									System.out.println("Expected parameter after: " + curCli);
									printHelp();
									return false;
								}
								set = true;
								if(!opt.setParam(args[i+1]))
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
				if(!set)
				{
					System.out.println("Unknown Parameter: " + curCli);
					printHelp();
					return false;
				}
			}
			i++;
		}
		boolean allMand = true;
		for(CliOption opt : options)
		{
			if(opt.isMandatory() && !(opt.isSet() || opt.isGiven()))
			{
				System.out.println("" + opt + "\nis missing!");
				allMand = false;
			}
		}
		if(!allMand)
		{
			printHelp();
		}
		if(getOption('h').isSet())
		{
			printHelp();
		}
		return allMand;
	}

	private void printHelp()
	{
		System.out.println("\nCall with these mandatory Arguements:");
		for(CliOption opt: options)
		{
			if(opt.isMandatory())
			{
				System.out.println(opt);
			}
		}
		
		System.out.println("\nAnd these optional Arguements:");
		for(CliOption opt: options)
		{
			if(!opt.isMandatory())
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
