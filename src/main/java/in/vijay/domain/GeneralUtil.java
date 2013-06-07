package in.vijay.domain;

import java.util.Arrays;
import java.util.List;

public class GeneralUtil {
	
	private GeneralUtil() {
	}
	
	public static List<String> getCombinedNameValuePairs(List<String> original, String[] current) {

		if (original == null || original.size() == 0) {
			return Arrays.asList(current);
		}
		for (String s1 : current) {
			boolean insertCurrent = true;
			String currentName = s1.split("=")[0];
			String currentValue = s1.split("=")[1];
			for (String s2 : original) {
				String originalName = s2.split("=")[0];
				if (originalName.equals(currentName)) {
					original.set(original.indexOf(s2), currentName + "="
							+ currentValue);
					insertCurrent = false;
				}
			}
			if (insertCurrent) {
				original.add(s1);
			}
		}
		return original;
	}
}
