package net.brenig.pixelescape.test;

import net.brenig.pixelescape.lib.CycleArray;
import net.brenig.pixelescape.lib.LogHelper;

/**
 * Created by Jonas Brenig on 14.10.2015.
 */
public class CycleArrayTest {

	public static void runTest() {
		CycleArray<String> a = new CycleArray<String>(8);
		LogHelper.newLine();
		LogHelper.debug("Test", "Empty:", null);
		printArray(a);
		for(int i = 0; i < a.size(); i++) {
			a.add("T: " + i);
		}
		LogHelper.newLine();
		LogHelper.debug("Test", "Filled:", null);
		printArray(a);
		a.cycleForward();
		LogHelper.newLine();
		LogHelper.debug("Test", "C1:", null);
		printArray(a);
		a.add("NEWEST");
		LogHelper.newLine();
		LogHelper.debug("Test", "A1:", null);
		printArray(a);
		a.set(a.size() - 1, "SET_NEWEST");
		LogHelper.newLine();
		LogHelper.debug("Test", "S1:", null);
		printArray(a);
		a.resize(a.size());
		LogHelper.newLine();
		LogHelper.debug("Test", "Same:", null);
		printArray(a);

		LogHelper.newLine();
		LogHelper.debug("Test", "Newest: " + a.getNewest(), null);

		LogHelper.newLine();
		LogHelper.debug("Test", "Oldest: " + a.getOldest(), null);

		LogHelper.newLine();
		LogHelper.debug("Test", "OldestNonNull: " + a.getOldestNonNull(), null);
	}

	private static void printArray(CycleArray c) {
		for(int i = 0; i < c.size(); i++) {
			LogHelper.debug("Test", "" + c.get(i), null);
		}
	}
}
