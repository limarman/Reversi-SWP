package swpg3.main;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CliParserTest {

	@Test
	void testAddOptionCliOption()
	{
		CliOption opt = new CliOption('s', "server", true, CliOptionType.STRINGPARAM, "localhost", "Serveraddress to connect to");
		CliParser parser = new CliParser();
		parser.addOption(opt);
		parser.addOption('p', "port", true, CliOptionType.INTPARAM, "12345", "Port on server to connect");
		
		
		assertNotNull(parser.getOption("server"));
		assertEquals(opt, parser.getOption("server"));
		assertNotNull(parser.getOption('s'));
		assertEquals(opt, parser.getOption('s'));
		
		assertNotNull(parser.getOption("port"));
		assertNotNull(parser.getOption('p'));
	}

	@Test
	void testParse()
	{
		CliOption opt1 = 
				new CliOption('s', "server", true, CliOptionType.STRINGPARAM, "localhost", "Server to connect to");
		CliOption opt2 = 
				new CliOption('p', "port", true, CliOptionType.INTPARAM, "123415", "Port on server to connect to");
		CliOption opt3 = 
				new CliOption('l', "loglevel", false, CliOptionType.INTPARAM, "3", "Loglevel to be used: 0-None 5-Debug");
		
		CliParser parse = new CliParser();
		parse.addOption(opt1);
		parse.addOption(opt2);
		parse.addOption(opt3);
		
		String[] args = {"--server", "127.0.0.1", "-p", "12345"};
		assertTrue(parse.parse(args));
		
		assertEquals("127.0.0.1", opt1.getString());
		assertEquals(12345, opt2.getInt());
		assertEquals(3, opt3.getInt());
	}

}
