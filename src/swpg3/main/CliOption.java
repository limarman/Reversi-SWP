/**
 * 
 */
package swpg3.main;

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


	public CliOption(char shortName, String longName, boolean mandatory, CliOptionType type, String defaultPa, String description)
	{
		super();
		this.shortName = shortName;
		this.longName = longName;
		this.mandatory = mandatory;
		this.type = type;
		this.defaultParam = defaultPa;
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
		String retString = "";
		
		if(!mandatory) {retString += "(";}
		else {retString += " ";}
		
		retString += "-" + shortName;
		
		retString += "\t--" + longName;
		
		if(type == CliOptionType.FLAG) { retString += "\t\t";}
		else if(type == CliOptionType.INTPARAM) { retString += "\t\tnumber";}
		else {retString += "\t\tparam";}
		
		retString += "\t\t" + description;
		
		if(!mandatory) {retString += ")";}
		else {retString += " ";}
		
		return retString;
	}
	
	
}
