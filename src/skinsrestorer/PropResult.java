package skinsrestorer;

import java.util.List;

public class PropResult {

	@SuppressWarnings("unused")
	private String id;
	@SuppressWarnings("unused")
	private String name;
	public List<Prop> properties;

	public static class Prop {
		public String name;
		public String value;
		public String signature;
	}

}
