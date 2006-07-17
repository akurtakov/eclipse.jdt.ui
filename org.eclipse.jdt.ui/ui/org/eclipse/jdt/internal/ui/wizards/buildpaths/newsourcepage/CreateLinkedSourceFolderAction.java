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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import org.eclipse.core.resources.IProject;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;

import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;

import org.eclipse.jdt.internal.corext.buildpath.ClasspathModifier.IClasspathModifierListener;

import org.eclipse.jdt.ui.PreferenceConstants;

import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.internal.ui.util.PixelConverter;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.AddSourceFolderWizard;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.CPListElement;

//SelectedElements iff enabled: IJavaProject && size == 1
public class CreateLinkedSourceFolderAction extends BuildpathModifierAction {
	
	private final IClasspathModifierListener fListener;

	public CreateLinkedSourceFolderAction(IWorkbenchSite site) {
		this(site, PlatformUI.getWorkbench().getProgressService(), null);
	}
	
	public CreateLinkedSourceFolderAction(IWorkbenchSite site, IRunnableContext context, IClasspathModifierListener listener) {
		super(site);
		
		fListener= listener;
		
		setText(NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Link_label); 
		setToolTipText(NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Link_tooltip);
		setImageDescriptor(JavaPluginImages.DESC_ELCL_ADD_LINKED_SOURCE_TO_BUILDPATH);
		setDescription(NewWizardMessages.PackageExplorerActionGroup_FormText_createLinkedFolder);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getDetailedDescription() {
		return NewWizardMessages.PackageExplorerActionGroup_FormText_createLinkedFolder;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		Shell shell= getShell();
	
		try {
			IJavaProject javaProject= (IJavaProject)getSelectedElements().get(0);
            
			CPListElement newEntrie= new CPListElement(javaProject, IClasspathEntry.CPE_SOURCE);
            CPListElement[] existing= CPListElement.createFromExisting(javaProject);
            boolean isProjectSrcFolder= CPListElement.isProjectSourceFolder(existing, javaProject);
            
			AddSourceFolderWizard wizard= new AddSourceFolderWizard(existing, newEntrie, getOutputLocation(javaProject), true, false, false, isProjectSrcFolder, isProjectSrcFolder);
			wizard.init(PlatformUI.getWorkbench(), new StructuredSelection(javaProject));
			
			WizardDialog dialog= new WizardDialog(shell, wizard);
			if (shell != null) {
				PixelConverter converter= new PixelConverter(shell);
				dialog.setMinimumPageSize(converter.convertWidthInCharsToPixels(70), converter.convertHeightInCharsToPixels(20));
			}
			dialog.create();
			int res= dialog.open();
			if (res == Window.OK) {
				if (fListener != null)
					fListener.classpathEntryChanged(wizard.getExistingEntries());

				selectAndReveal(new StructuredSelection(wizard.getCreatedElement()));
			}
			
			notifyResult(res == Window.OK);
		} catch (CoreException e) {
			String title= NewWizardMessages.AbstractOpenWizardAction_createerror_title; 
			String message= NewWizardMessages.AbstractOpenWizardAction_createerror_message; 
			ExceptionHandler.handle(e, shell, title, message);
		}
	}
	
    private IPath getOutputLocation(IJavaProject javaProject) {
    	try {
			return javaProject.getOutputLocation();		
		} catch (CoreException e) {
			IProject project= javaProject.getProject();
			IPath projPath= project.getFullPath();
			return projPath.append(PreferenceConstants.getPreferenceStore().getString(PreferenceConstants.SRCBIN_BINNAME));
		}
    }

    protected boolean canHandle(IStructuredSelection selection) {
    	if (selection.size() != 1)
    		return false;
    	
    	if (!(selection.getFirstElement() instanceof IJavaProject))
    		return false;
    	
    	return true;
    }
}