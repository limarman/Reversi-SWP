/**
 * 
 */
package swpg3.main.cli;

/**
 * A class representing a commandline option. Can be used with the CliParser to
 * parse commandline arguments.
 * 
 * This class has a short and long name, representing the cmd argument. An
 * Option can be mandatory which means it has to be supplied for the program to
 * run correctly. The type describes if the argument needs extra parameters like
 * a number or a string. Furthermore there is a description and a default
 * Parameter value, which can be used to display this Option in the help text
 * and supply a default value for the parameter, if this option is not supplied
 * by the user.
 * 
 * @author eric
 *
 */
public class CliOption {
	private char			shortName;
	private String			longName;
	private boolean			mandatory;
	private CliOptionType	type;
	private String			description;
	private String			defaultParam;

	private boolean	given;
	private boolean	isSet;
	private String	parameter;

	/**
	 * Constructor initializing all variables.
	 * 
	 * @param shortName
	 *            '-' must not be supplied. Will be prepended automatically.
	 * @param longName
	 *            "--" must not be supplied. Will be prepended automatically.
	 * @param mandatory
	 *            Is this a mandatory Option?
	 * @param type
	 *            Type of further parameter of the option.
	 * @param defaultParam
	 *            String containing the default value. Will be ignored, if option is
	 *            of type FLAG.
	 * @param description
	 *            A short description of the Parameter.
	 */
	public CliOption(char shortName, String longName, boolean mandatory, CliOptionType type, String defaultParam,
			String description)
	{
		super();
		this.shortName = shortName;
		this.longName = longName;
		this.mandatory = mandatory;
		this.type = type;
		this.defaultParam = defaultParam;
		this.description = description;
	}

	/**
	 * @return Shortname without '-'
	 */
	public char getShortName()
	{
		return shortName;
	}

	/**
	 * @return Longmane without "--"
	 */
	public String getLongName()
	{
		return longName;
	}

	/**
	 * @return true, if option is mandatory; false, otherwise.
	 */
	public boolean isMandatory()
	{
		return mandatory;
	}

	/**
	 * @return Type of option.
	 */
	public CliOptionType getType()
	{
		return type;
	}

	/**
	 * Will be set by CliParser. Only useful after CliParser parsed the arguments!
	 * 
	 * @return true, if option is a FLAG and was supplied by the user; false,
	 *         otherwise.
	 */
	public boolean isSet()
	{
		return isSet;
	}

	/**
	 * Will be set by CliParser. Only useful after CliParser parsed the arguments!
	 * 
	 * @return true, if user supplied this option; false, otherwise.
	 */
	public boolean isGiven()
	{
		return given;
	}

	/**
	 * If this option is needs a string parameter it can be read with this method
	 * after the CliParser finished its parsing.
	 * 
	 * @return String parameter if this option was set by user; default parameter
	 *         otherwise.
	 */
	public String getString()
	{
		return given ? parameter : defaultParam;
	}

	/**
	 * If this option needs an integer parameter it can be read with this method
	 * after the CliParser did its magic!
	 * 
	 * @return Int parameter, if this option was supplied by user; default
	 *         parameter, otherwise.
	 */
	public int getInt()
	{
		return Integer.parseInt(given ? parameter : defaultParam);
	}

	/**
	 * If this option is a FLAG it can be set by this method. Usually this will be
	 * called by
	 */
	void set()
	{
		if (type == CliOptionType.FLAG)
		{
			isSet = true;
			given = true;
		}
	}

	/**
	 * If this is not a FLAG the additional parameter will be set by this method.
	 * and the given variable will be set to true.
	 * 
	 * @param param value of parameter as a String.
	 * @return true, if parameter was set successfully; false, if an error occurred.
	 */
	boolean setParam(String param)
	{
		if (type == CliOptionType.FLAG)
		{
			return false;
		} else if (type == CliOptionType.INTPARAM)
		{
			try
			{
				Integer.parseInt(param);
				parameter = param;
				given = true;
				return true;
			} catch (Exception e)
			{
				return false;
			}
		} else if (type == CliOptionType.STRINGPARAM)
		{
			parameter = param;
			given = true;
			return true;
		}
		return false;
	}

	@Override
	public String toString()
	{
		String retString = String.format("%-6s %-17s %-8s %s", displayShort(), displayLong(), displayArg(),
				description);

		if (!mandatory)
		{
			retString = "(" + retString + ")";
		} else
		{
			retString = " " + retString + " ";
		}

		return retString;
	}

	private String displayShort()
	{
		if (shortName != ' ')
		{
			return "-" + shortName;
		} else
			return "";
	}

	private String displayLong()
	{
		if (longName != "")
		{
			return "--" + longName;
		} else
		{
			return "";
		}
	}

	private String displayArg()
	{
		if (type == CliOptionType.INTPARAM)
		{
			return "number";
		} else if (type == CliOptionType.STRINGPARAM)
		{
			return "param";
		} else
		{
			return "";
		}
	}

	/**
	 * @return a short representation of the option like "(-shortname/--longname)"
	 */
	public String shortString()
	{
		String shrt = displayShort();
		String lng = displayLong();

		String ret = "(";

		if (!shrt.equals(""))
		{
			ret += shrt + "/";
		}
		ret += lng + ")";

		return ret;
	}

}
