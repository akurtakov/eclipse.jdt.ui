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

package org.eclipse.jdt.text.tests.performance;


import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.test.performance.PerformanceMeter;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.source.ISourceViewer;

import org.eclipse.ui.internal.texteditor.quickdiff.compare.rangedifferencer.RangeDifference;
import org.eclipse.ui.texteditor.AbstractTextEditor;


/**
 * Measures the number of created {@link RangeDifference}
 * while QuickDiff is initializing with lots of changes in a large file.
 * 
 * @since 3.1
 */
public class QuickDiffInvocationCountTest extends TextPerformanceTestCase {

	private static final Class THIS= QuickDiffInvocationCountTest.class;

	private static final String FILE= "/" + PerformanceTestSetup.PROJECT + "/Eclipse SWT Custom Widgets/common/org/eclipse/swt/custom/TableTree.java";

	private AbstractTextEditor fEditor;
	
	public static Test suite() {
		return new PerformanceTestSetup(new TestSuite(THIS));
	}
	
	/*
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		fEditor= (AbstractTextEditor) EditorTestHelper.openInEditor(ResourceTestHelper.findFile(FILE), EditorTestHelper.TEXT_EDITOR_ID, true);
		fEditor.showChangeInformation(false);
		ISourceViewer viewer= EditorTestHelper.getSourceViewer(fEditor);
		IDocument document= EditorTestHelper.getDocument(fEditor);
		try {
			if (viewer instanceof ITextViewerExtension)
				((ITextViewerExtension) viewer).getRewriteTarget().beginCompoundChange();
			for (int i= 0; i < document.getNumberOfLines(); i += 2) {
				document.replace(document.getLineOffset(i), 0, " ");
			}
		} finally {
			if (viewer instanceof ITextViewerExtension)
				((ITextViewerExtension) viewer).getRewriteTarget().endCompoundChange();
		}
		EditorTestHelper.joinBackgroundActivities(fEditor);
	}

	/**
	 * Measures the number of created {@link RangeDifference}
	 * while QuickDiff is initializing with lots of changes in a large file.
	 * 
	 * @throws Exception
	 */
	public void test() throws Exception {
		PerformanceMeter performanceMeter= createInvocationCountPerformanceMeter(RangeDifference.class.getConstructors());
		performanceMeter.start();
		fEditor.showChangeInformation(true);
		EditorTestHelper.runEventQueue(5000); // ensure QuickDiff job started
		EditorTestHelper.joinBackgroundActivities(fEditor);
		performanceMeter.stop();
		commitAllMeasurements();
		assertAllPerformance();
	}
	
	/*
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		EditorTestHelper.closeAllEditors();
		
	}
}
