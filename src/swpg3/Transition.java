/**
 * 
 */
package swpg3;

/**
 * A Class to represent Transitions with one endpoint and an output direction
 * reverse Transition has to be attached to Endpoint
 * 
 * @author eric
 *
 */
public class Transition {
	private Tile endpoint;
	private Vector2i outputDir;

	/**
	 * Simple Constructor initialising everything
	 * 
	 * @param endpoint
	 * @param inputDir
	 * @param outputDir
	 */
	public Transition(Tile endpoint, Vector2i outputDir)
	{
		this.endpoint = endpoint;
		this.outputDir = outputDir;
	}

	/**
	 * @return the endpoint
	 */
	public Tile getEndpoint()
	{
		return endpoint;
	}

	/**
	 * @return the outputDir
	 */
	public Vector2i getOutputDir()
	{
		return outputDir;
	}

}
