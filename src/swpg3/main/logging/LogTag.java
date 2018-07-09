package swpg3.main.logging;

/**
 * Tags that mark messages so its easy to relate them to a specific thing like performance measurement.
 * @author eric
 *
 */
public enum LogTag {
	NONE        ("   "),
	MAP         ("{M}"),
	PERFORMANCE ("{P}"),
	DEBUG       ("{D}");

	public final String msg;

	private LogTag(String str)
	{
		this.msg = str;
	}
}
