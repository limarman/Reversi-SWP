/**
 * 
 */
package swpg3.main.cli;

/**
 * @author eric
 *
 */
public class CliOption {
	private char			shortName;
	private String			longName;
	private boolean			mandatory;
	private CliOptionType	type;
	private String description;
	private String			defaultParam;

	private boolean	given;
	private boolean	isSet;
	private String	parameter;


	public CliOption(char shortName, String longName, boolean mandatory, CliOptionType type, String defaultParam, String description)
	{
		super();
		this.shortName = shortName;
		this.longName = longName;
		this.mandatory = mandatory;
		this.type = type;
		this.defaultParam = defaultParam;
		this.description = description;
	}

	
	
	public char getShortName()
	{
		return shortName;
	}



	public String getLongName()
	{
		return longName;
	}



	public boolean isMandatory()
	{
		return mandatory;
	}



	public CliOptionType getType()
	{
		return type;
	}



	public boolean isSet()
	{
		return isSet;
	}
	public boolean isGiven()
	{
		return given;
	}

	public String getString()
	{
		return given ? parameter : defaultParam;
	}

	public int getInt()
	{
		return Integer.parseInt(given ? parameter : defaultParam);
	}

	public void set()
	{
		if (type == CliOptionType.FLAG)
		{
			isSet = true;
		}
	}
	
	public boolean setParam(String param)
	{
		if(type == CliOptionType.FLAG)
		{
			return false;
		}
		else if(type == CliOptionType.INTPARAM)
		{
			try{
				Integer.parseInt(param);
				parameter = param;
				given = true;
				return true;
			}catch(Exception e)
			{
				return false;
			}
		}
		else if(type == CliOptionType.STRINGPARAM)
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
		String retString = String.format("%-6s %-17s %-8s %s", displayShort(), displayLong(), displayArg(), description);
		
		if(!mandatory) {retString = "(" + retString + ")";}
		else {retString = " " + retString + " ";}
				
		return retString;
	}
	
	private String displayShort()
	{
		if(shortName != ' ')
		{
			return "-" + shortName;
		}
		else return "";
	}
	private String displayLong()
	{
		if(longName != "")
		{
			return "--" + longName;
		}
		else{return "";}
	}
	private String displayArg()
	{
		if(type == CliOptionType.INTPARAM)
		{
			return "number";
		}
		else if(type == CliOptionType.STRINGPARAM)
		{
			return "param";
		}
		else
		{
			return "";
		}
	}



	public String shortString()
	{
		String shrt = displayShort();
		String lng = displayLong();
		
		String ret = "(";
		
		if(!shrt.equals(""))
		{
			ret += shrt + "/";
		}
		ret += lng + ")";
		
		return ret;
	}
	
}
