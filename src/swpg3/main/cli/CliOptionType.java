package swpg3.main.cli;

/**
 * Simple Enum containing all the possible CliOption types.
 * 
 * FLAG is a simple flag that does not need any further parameter.
 * INTPARAM needs an integer parameter.
 * STRINGPARAM needs a String.
 * @author eric
 *
 */
public enum CliOptionType {
	FLAG, INTPARAM, STRINGPARAM;
}
