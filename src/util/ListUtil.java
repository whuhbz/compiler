package util;

import java.util.List;

public class ListUtil {
	public static int count(List<?> list, Object object) {
		int i = 0;
		for(Object element : list) {
			if(element.equals(object)) {
				i++;
			}
		}
		return i;
	}
}
