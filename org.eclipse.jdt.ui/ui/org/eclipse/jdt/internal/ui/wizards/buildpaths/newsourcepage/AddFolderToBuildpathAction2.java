/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.wizards.buildpaths.newsourcepage;

import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

import org.eclipse.jdt.internal.corext.buildpath.IClasspathInformationProvider;
import org.eclipse.jdt.internal.corext.buildpath.ClasspathModifier.IClasspathModifierListener;

public class AddFolderToBuildpathAction2 extends AddFolderToBuildpathAction implements IClasspathModifierAction {

	private final HintTextGroup fInformationProvider;

	public AddFolderToBuildpathAction2(IClasspathModifierListener listener, HintTextGroup provider, IRunnableContext context) {
		super(null, context, listener);
				
		fInformationProvider= provider;
    }
	
	/**
	 * {@inheritDoc}
	 */
	protected void selectAndReveal(ISelection selection) {
		fInformationProvider.handleAddToCP(((StructuredSelection)selection).toList());
	}
     
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#getId()
     */
    public String getId() {
        return Integer.toString(getTypeId());
    }

    /**
     * {@inheritDoc}
     */
    public int getTypeId() {
        return IClasspathInformationProvider.ADD_SEL_SF_TO_BP;
    }
}