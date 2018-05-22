package swpg3.main.logging;

/**
 * @author eric
 *
 */
public enum LogTag {
	NONE        ("   "),
	MAP         ("{M}"),
	PERFORMANCE ("{P}");

	public final String msg;

	private LogTag(String str)
	{
		this.msg = str;
	}
}
