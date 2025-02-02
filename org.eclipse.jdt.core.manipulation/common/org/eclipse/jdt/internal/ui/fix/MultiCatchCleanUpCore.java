/*******************************************************************************
 * Copyright (c) 2020, 2025 Fabrice TIERCELIN and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Fabrice TIERCELIN - initial API and implementation
 *     Red Hat Inc. - refactored to jdt.core.manipulation from jdt.ui
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.fix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.text.edits.TextEditGroup;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.dom.ASTSemanticMatcher;
import org.eclipse.jdt.internal.corext.fix.CleanUpConstants;
import org.eclipse.jdt.internal.corext.fix.CompilationUnitRewriteOperationsFixCore;
import org.eclipse.jdt.internal.corext.fix.CompilationUnitRewriteOperationsFixCore.CompilationUnitRewriteOperation;
import org.eclipse.jdt.internal.corext.fix.LinkedProposalModelCore;
import org.eclipse.jdt.internal.corext.refactoring.structure.CompilationUnitRewrite;

import org.eclipse.jdt.ui.cleanup.CleanUpRequirements;
import org.eclipse.jdt.ui.cleanup.ICleanUpFix;
import org.eclipse.jdt.ui.text.java.IProblemLocation;

/**
 * A fix that refactors <code>catch</code> clauses with the same body to use Java 7's multi-catch:
 * <ul>
 * <li>The JVM must be Java 7 or higher,</li>
 * <li>The <code>catch</code> blocks should do the same thing,</li>
 * <li>The <code>catch</code> blocks must be able to move.</li>
 * </ul>
 */
public class MultiCatchCleanUpCore extends AbstractMultiFix {
	private enum MergeDirection {
		NONE, UP, DOWN;
	}

	public MultiCatchCleanUpCore() {
		this(Collections.emptyMap());
	}

	public MultiCatchCleanUpCore(final Map<String, String> options) {
		super(options);
	}

	@Override
	public CleanUpRequirements getRequirements() {
		boolean requireAST= isEnabled(CleanUpConstants.MULTI_CATCH);
		return new CleanUpRequirements(requireAST, false, false, null);
	}

	@Override
	public String[] getStepDescriptions() {
		if (isEnabled(CleanUpConstants.MULTI_CATCH)) {
			return new String[] { MultiFixMessages.MultiCatchCleanUp_description };
		}

		return new String[0];
	}

	@Override
	public String getPreview() {
		StringBuilder bld= new StringBuilder();
		bld.append("try {\n"); //$NON-NLS-1$
		bld.append("    obj.throwingMethod();\n"); //$NON-NLS-1$

		if (isEnabled(CleanUpConstants.MULTI_CATCH)) {
			bld.append("} catch (IllegalArgumentException | IOException ioe) {\n"); //$NON-NLS-1$
			bld.append("    ioe.printStackTrace();\n"); //$NON-NLS-1$
			bld.append("}\n\n\n"); //$NON-NLS-1$
		} else {
			bld.append("} catch (IllegalArgumentException iae) {\n"); //$NON-NLS-1$
			bld.append("    iae.printStackTrace();\n"); //$NON-NLS-1$
			bld.append("} catch (IOException ioe) {\n"); //$NON-NLS-1$
			bld.append("    ioe.printStackTrace();\n"); //$NON-NLS-1$
			bld.append("}\n"); //$NON-NLS-1$
		}

		return bld.toString();
	}

	@Override
	protected ICleanUpFix createFix(final CompilationUnit unit) throws CoreException {
		if (!isEnabled(CleanUpConstants.MULTI_CATCH)) {
			return null;
		}

		final List<CompilationUnitRewriteOperation> rewriteOperations= new ArrayList<>();

		unit.accept(new ASTVisitor() {
			abstract class AbstractBinding {
				protected abstract boolean isSubTypeCompatible(AbstractBinding type);
			}

			class SingleBinding extends AbstractBinding {
				private final ITypeBinding typeBinding;

				public SingleBinding(final ITypeBinding typeBinding) {
					this.typeBinding= typeBinding;
				}

				@Override
				protected boolean isSubTypeCompatible(final AbstractBinding other) {
					if (typeBinding == null) {
						return false;
					}

					if (other instanceof SingleBinding) {
						SingleBinding o= (SingleBinding) other;

						if (o.typeBinding == null) {
							return false;
						}

						return typeBinding.isSubTypeCompatible(o.typeBinding);
					}

					if (other instanceof MultiBinding) {
						MultiBinding o= (MultiBinding) other;

						for (ITypeBinding otherTypeBinding : o.typeBindings) {
							if (!isSubTypeCompatible(new SingleBinding(otherTypeBinding))) {
								return false;
							}
						}

						return true;
					}

					return false;
				}
			}

			class MultiBinding extends AbstractBinding {
				private final ITypeBinding[] typeBindings;

				public MultiBinding(final ITypeBinding[] typeBindings) {
					this.typeBindings= typeBindings;
				}

				@Override
				protected boolean isSubTypeCompatible(final AbstractBinding other) {
					if (other instanceof SingleBinding) {
						for (ITypeBinding selfTypeBinding : typeBindings) {
							if (new SingleBinding(selfTypeBinding).isSubTypeCompatible(other)) {
								return true;
							}
						}
					}

					if (other instanceof MultiBinding) {
						MultiBinding o= (MultiBinding) other;

						for (ITypeBinding otherTypeBinding : o.typeBindings) {
							if (!isSubTypeCompatible(new SingleBinding(otherTypeBinding))) {
								return false;
							}
						}

						return true;
					}

					return false;
				}
			}

			final class MultiCatchASTMatcher extends ASTSemanticMatcher {
				private final Map<ASTNode, ASTNode> matchingVariables= new HashMap<>();

				public MultiCatchASTMatcher(final CatchClause catchClause1, final CatchClause catchClause2) {
					matchingVariables.put(catchClause1.getException(), catchClause2.getException());
				}

				@Override
				public boolean match(final VariableDeclarationStatement node, final Object other) {
					return super.match(node, other) || matchVariableDeclarationsWithDifferentNames(node, other);
				}

				private boolean matchVariableDeclarationsWithDifferentNames(final VariableDeclarationStatement node, final Object other) {
					if (!(other instanceof VariableDeclarationStatement)) {
						return false;
					}

					List<VariableDeclarationFragment> fragments1= node.fragments();
					List<VariableDeclarationFragment> fragments2= ((VariableDeclarationStatement) other).fragments();

					if (fragments1.size() == fragments2.size()) {
						Iterator<VariableDeclarationFragment> it1= fragments1.iterator();
						Iterator<VariableDeclarationFragment> it2= fragments2.iterator();
						// Do not make all efforts to try to reconcile fragments declared in different
						// order
						while (it1.hasNext() && it2.hasNext()) {
							VariableDeclarationFragment f1= it1.next();
							VariableDeclarationFragment f2= it2.next();

							if (ASTNodes.resolveTypeBinding(f1) != null
									&& Objects.equals(ASTNodes.resolveTypeBinding(f1), ASTNodes.resolveTypeBinding(f2))
									&& ASTNodes.match(this, f1.getInitializer(), f2.getInitializer())) {
								this.matchingVariables.put(f1, f2);
								return true;
							}
						}
					}

					return false;
				}

				@Override
				public boolean match(final SimpleName node, final Object other) {
					return super.match(node, other) || areBothReferringToSameVariables(node, other);
				}

				@Override
				public boolean match(final MethodInvocation methodInvocation1, final Object other) {
					if (other instanceof MethodInvocation) {
						MethodInvocation methodInvocation2= (MethodInvocation) other;
						return super.match(methodInvocation1, methodInvocation2)
								&& isSameMethodBinding(methodInvocation1.resolveMethodBinding(), methodInvocation2.resolveMethodBinding());
					}

					return false;
				}

				@Override
				public boolean match(final SuperMethodInvocation superMethodInvocation1, final Object other) {
					if (other instanceof SuperMethodInvocation) {
						SuperMethodInvocation superMethodInvocation2= (SuperMethodInvocation) other;
						return super.match(superMethodInvocation1, superMethodInvocation2)
								&& isSameMethodBinding(superMethodInvocation1.resolveMethodBinding(), superMethodInvocation2.resolveMethodBinding());
					}

					return false;
				}

				@Override
				public boolean match(final ClassInstanceCreation classInstanceCreation1, final Object other) {
					if (other instanceof ClassInstanceCreation) {
						ClassInstanceCreation classInstanceCreation2= (ClassInstanceCreation) other;
						return super.match(classInstanceCreation1, classInstanceCreation2)
								&& isSameMethodBinding(classInstanceCreation1.resolveConstructorBinding(), classInstanceCreation2.resolveConstructorBinding());
					}

					return false;
				}

				private boolean isSameMethodBinding(final IMethodBinding binding1, final IMethodBinding binding2) {
					return binding1 != null && binding2 != null
							&& (binding1.equals(binding2) || binding1.overrides(binding2) || binding2.overrides(binding1)
							// This is a really expensive check. Do it at the very end
									|| areOverridingSameMethod(binding1, binding2));
				}

				private boolean areOverridingSameMethod(final IMethodBinding binding1, final IMethodBinding binding2) {
					Set<IMethodBinding> commonOverridenMethods= ASTNodes.getOverridenMethods(binding1);
					commonOverridenMethods.retainAll(ASTNodes.getOverridenMethods(binding2));
					return !commonOverridenMethods.isEmpty();
				}

				private boolean areBothReferringToSameVariables(final ASTNode node, final Object other) {
					for (Entry<ASTNode, ASTNode> pairedVariables : matchingVariables.entrySet()) {
						if (ASTNodes.isSameVariable(node, pairedVariables.getKey())) {
							return other instanceof ASTNode && ASTNodes.isSameVariable((ASTNode) other, pairedVariables.getValue());
						}
					}

					return false;
				}
			}

			@Override
			public boolean visit(final TryStatement visited) {
				List<CatchClause> catchClauses= visited.catchClauses();
				AbstractBinding[] typeBindings= resolveTypeBindings(catchClauses);

				for (int i= 0; i < catchClauses.size() - 1; i++) {
					List<CatchClause> mergeableCatchClauses= new ArrayList<>(catchClauses.size());
					mergeableCatchClauses.add(catchClauses.get(i));
					MergeDirection direction= null;

					for (int j= i + 1; j < catchClauses.size(); j++) {
						MergeDirection newDirection= mergeDirection(typeBindings, i, j);

						if (!MergeDirection.NONE.equals(newDirection)
								&& (direction == null || direction.equals(newDirection))
								&& matchMultiCatch(catchClauses.get(i), catchClauses.get(j))) {
							direction= newDirection;
							mergeableCatchClauses.add(catchClauses.get(j));
						}
					}

					if (mergeableCatchClauses.size() > 1) {
						rewriteOperations.add(new MultiCatchOperation(mergeableCatchClauses, direction));
						return false;
					}
				}

				return true;
			}

			private AbstractBinding[] resolveTypeBindings(final List<CatchClause> catchClauses) {
				AbstractBinding[] results= new AbstractBinding[catchClauses.size()];

				for (int i= 0; i < catchClauses.size(); i++) {
					results[i]= resolveBinding(catchClauses.get(i));
				}

				return results;
			}

			private AbstractBinding resolveBinding(final CatchClause catchClause) {
				SingleVariableDeclaration singleVariableDeclaration= catchClause.getException();
				Type type= singleVariableDeclaration.getType();

				switch (type.getNodeType()) {
				case ASTNode.SIMPLE_TYPE:
					return new SingleBinding(type.resolveBinding());

				case ASTNode.UNION_TYPE:
					List<Type> types= ((UnionType) type).types();
					ITypeBinding[] typeBindings= new ITypeBinding[types.size()];

					for (int i= 0; i < types.size(); i++) {
						typeBindings[i]= types.get(i).resolveBinding();
					}

					return new MultiBinding(typeBindings);

				default:
					return null;
				}
			}

			private MergeDirection mergeDirection(final AbstractBinding[] typeBindings, final int start, final int end) {
				if (canMergeTypesDown(typeBindings, start, end)) {
					return MergeDirection.DOWN;
				}

				if (canMergeTypesUp(typeBindings, start, end)) {
					return MergeDirection.UP;
				}

				return MergeDirection.NONE;
			}

			private boolean canMergeTypesDown(final AbstractBinding[] types, final int start, final int end) {
				AbstractBinding startType= types[start];

				for (int i= start + 1; i < end; i++) {
					AbstractBinding type= types[i];

					if (startType.isSubTypeCompatible(type)) {
						return false;
					}
				}

				return true;
			}

			private boolean canMergeTypesUp(final AbstractBinding[] types, final int start, final int end) {
				AbstractBinding endType= types[end];

				for (int i= start + 1; i < end; i++) {
					AbstractBinding type= types[i];

					if (type.isSubTypeCompatible(endType)) {
						return false;
					}
				}

				return true;
			}

			private boolean matchMultiCatch(final CatchClause firstCatchClause, final CatchClause secondCatchClause) {
				MultiCatchASTMatcher matcher= new MultiCatchASTMatcher(firstCatchClause, secondCatchClause);
				return ASTNodes.match(matcher, firstCatchClause.getBody(), secondCatchClause.getBody());
			}
		});

		if (rewriteOperations.isEmpty()) {
			return null;
		}

		return new CompilationUnitRewriteOperationsFixCore(MultiFixMessages.MultiCatchCleanUp_description, unit,
				rewriteOperations.toArray(new CompilationUnitRewriteOperation[0]));
	}

	@Override
	public boolean canFix(final ICompilationUnit compilationUnit, final IProblemLocation problem) {
		return false;
	}

	@Override
	protected ICleanUpFix createFix(final CompilationUnit unit, final IProblemLocation[] problems) throws CoreException {
		return null;
	}

	private static class MultiCatchOperation extends CompilationUnitRewriteOperation {
		private final List<CatchClause> mergeableCatchClauses;
		private final MergeDirection direction;

		public MultiCatchOperation(final List<CatchClause> mergeableCatchClauses, final MergeDirection direction) {
			this.mergeableCatchClauses= mergeableCatchClauses;
			this.direction= direction;
		}

		@Override
		public void rewriteAST(final CompilationUnitRewrite cuRewrite, final LinkedProposalModelCore linkedModel) throws CoreException {
			ASTRewrite rewrite= cuRewrite.getASTRewrite();
			AST ast= cuRewrite.getRoot().getAST();
			TextEditGroup group= createTextEditGroup(MultiFixMessages.MultiCatchCleanUp_description, cuRewrite);

			List<Type> typesByClause= new ArrayList<>();

			for (CatchClause mergeableCatchClause : mergeableCatchClauses) {
				typesByClause.add(mergeableCatchClause.getException().getType());
			}

			List<Type> allTypes= new ArrayList<>();
			collectAllUnionedTypes(typesByClause, allTypes);
			removeSupersededAlternatives(allTypes);

			UnionType newUnionType= ast.newUnionType();
			newUnionType.types().addAll(ASTNodes.createMoveTarget(rewrite, allTypes));
			List<CatchClause> removedClauses= new ArrayList<>(mergeableCatchClauses);

			CatchClause mergedClause;
			if (MergeDirection.UP.equals(direction)) {
				mergedClause= removedClauses.remove(0);
			} else {
				mergedClause= removedClauses.remove(removedClauses.size() - 1);
			}

			rewrite.set(mergedClause.getException(), SingleVariableDeclaration.TYPE_PROPERTY, newUnionType, group);

			for (CatchClause mergeableCatchClause : removedClauses) {
				rewrite.remove(mergeableCatchClause, group);
			}
		}

		private void collectAllUnionedTypes(final Collection<Type> inputTypes, final List<Type> outputTypes) {
			for (Type type : inputTypes) {
				if (type instanceof UnionType) {
					UnionType unionType= (UnionType) type;
					collectAllUnionedTypes(unionType.types(), outputTypes);
				} else {
					outputTypes.add(type);
				}
			}
		}

		private void removeSupersededAlternatives(final List<Type> allTypes) {
			for (ListIterator<Type> it1= allTypes.listIterator(); it1.hasNext();) {
				ITypeBinding binding1= it1.next().resolveBinding();

				for (ListIterator<Type> it2= allTypes.listIterator(it1.nextIndex()); it2.hasNext();) {
					ITypeBinding binding2= it2.next().resolveBinding();

					if (binding1 != null && binding1.isSubTypeCompatible(binding2)) {
						it1.remove();
						break;
					}
				}
			}
		}
	}
}
