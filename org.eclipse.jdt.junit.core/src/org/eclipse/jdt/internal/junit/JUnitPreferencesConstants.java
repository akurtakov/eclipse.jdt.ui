/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.junit.ui;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Defines constants which are used to refer to values in the plugin's preference store.
 */
public class JUnitPreferencesConstants {
	/**
	 * Boolean preference controlling whether the failure stack should be
	 * filtered.
	 */	
	public static String DO_FILTER_STACK= JUnitPlugin.PLUGIN_ID + ".do_filter_stack"; //$NON-NLS-1$

	/**
	 * Boolean preference controlling whether the JUnit view should be shown on
	 * errors only.
	 */	
	public static String SHOW_ON_ERROR_ONLY= JUnitPlugin.PLUGIN_ID + ".show_on_error"; //$NON-NLS-1$
	
	/**
	 * List of active stack filters. A String containing a comma separated list
	 * of fully qualified type names/patterns.
	 */			
	public static final String PREF_ACTIVE_FILTERS_LIST = JUnitPlugin.PLUGIN_ID + ".active_filters"; //$NON-NLS-1$
	
	/**
	 * List of inactive stack filters. A String containing a comma separated
	 * list of fully qualified type names/patterns.
	 */				
	public static final String PREF_INACTIVE_FILTERS_LIST = JUnitPlugin.PLUGIN_ID + ".inactive_filters"; //$NON-NLS-1$	

	private static String[] fgDefaultFilterPatterns= new String[] { "org.eclipse.jdt.internal.junit.runner.*", //$NON-NLS-1$
		"org.eclipse.jdt.internal.junit.ui.*", //$NON-NLS-1$
		"junit.framework.TestCase", //$NON-NLS-1$
		"junit.framework.TestResult", //$NON-NLS-1$
		"junit.framework.TestSuite", //$NON-NLS-1$
		"junit.framework.Assert", //$NON-NLS-1$
		"java.lang.reflect.Method.invoke", //$NON-NLS-1$
		"junit.framework.TestResult$1" //$NON-NLS-1$

	};
	
	private JUnitPreferencesConstants() {
		// no instance
	}
	
	public static void initializeDefaults(IPreferenceStore store) {
		store.setDefault(JUnitPreferencesConstants.DO_FILTER_STACK, true);
		store.setDefault(JUnitPreferencesConstants.SHOW_ON_ERROR_ONLY, true);

		String list= store.getString(JUnitPreferencesConstants.PREF_ACTIVE_FILTERS_LIST);

		if ("".equals(list)) { //$NON-NLS-1$
			String pref= serializeList(fgDefaultFilterPatterns);
			store.setValue(JUnitPreferencesConstants.PREF_ACTIVE_FILTERS_LIST, pref);
		}

		store.setValue(JUnitPreferencesConstants.PREF_INACTIVE_FILTERS_LIST, ""); //$NON-NLS-1$
	}
	
	/**
	 * Returns the default list of active stack filters.
	 * 
	 * @return list
	 */
	public static List createDefaultStackFiltersList() {
		return Arrays.asList(fgDefaultFilterPatterns);
	}

	/**
	 * Serializes the array of strings into one comma
	 * separated string.
	 * 
	 * @param list array of strings
	 * @return a single string composed of the given list
	 */
	public static String serializeList(String[] list) {
		if (list == null)
			return ""; //$NON-NLS-1$

		StringBuffer buffer= new StringBuffer();
		for (int i= 0; i < list.length; i++) {
			if (i > 0)
				buffer.append(',');

			buffer.append(list[i]);
		}
		return buffer.toString();
	}
}
