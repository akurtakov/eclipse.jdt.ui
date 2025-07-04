/*******************************************************************************
 * Copyright (c) 2000, 2025 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann - Contributions for
 *								[quick fix] Add quick fixes for null annotations - https://bugs.eclipse.org/337977
 *								[quick fix] The fix change parameter type to @Nonnull generated a null change - https://bugs.eclipse.org/400668
 *								[null] "Annotate" proposals for adding external null annotations to library classes - https://bugs.eclipse.org/458200
 *     Red Hat Inc - refactored to jdt.core.manipulation
 *******************************************************************************/
package org.eclipse.jdt.internal.corext.fix;

import org.eclipse.osgi.util.NLS;

public final class FixMessages extends NLS {
	private static final String BUNDLE_NAME= "org.eclipse.jdt.internal.corext.fix.FixMessages"; //$NON-NLS-1$

	private FixMessages() {
	}

	public static String CleanUpPostSaveListener_name;
	public static String CleanUpPostSaveListener_SaveAction_ChangeName;
	public static String CleanUpPostSaveListener_SlowCleanUpDialog_link;
	public static String CleanUpPostSaveListener_SlowCleanUpDialog_title;
	public static String CleanUpPostSaveListener_SlowCleanUpWarningDialog_explain;
	public static String CleanUpPostSaveListener_unknown_profile_error_message;

	public static String CleanUpRefactoring_checkingPostConditions_message;
	public static String CleanUpRefactoring_clean_up_multi_chang_name;
	public static String CleanUpRefactoring_could_not_retrive_profile;
	public static String CleanUpRefactoring_Parser_Startup_message;
	public static String CleanUpRefactoring_Refactoring_name;
	public static String CleanUpRefactoring_ProcessingCompilationUnit_message;
	public static String CleanUpRefactoring_Initialize_message;
	public static String CleanUpRefactoring_exception;

	public static String CleanUpRegistry_ErrorTabPage_description;
	public static String CleanUpRegistry_ErrorTabPage_preview;
	public static String CleanUpRegistry_UnknownInitializerKind_errorMessage;
	public static String CleanUpRegistry_WrongKindForConfigurationUI_error;
	public static String CleanUpRegistry_cleanUpAlwaysEnabled_error;
	public static String CleanUpRegistry_cleanUpCreation_error;

	public static String CompilationUnitRewriteOperationsFix_nullChangeError;
	public static String CodeStyleFix_change_name;
	public static String ControlStatementsFix_change_name;

	public static String ConvertIterableLoopOperation_RemoveUpdateExpression_Warning;
	public static String ConvertIterableLoopOperation_RemoveUpdateExpressions_Warning;
	public static String ConvertIterableLoopOperation_semanticChangeWarning;
	public static String ExpressionsFix_add_parentheses_change_name;
	public static String ExpressionsFix_remove_parentheses_change_name;
	public static String PrimitiveComparisonFix_convert_compareTo_to_primitive_comparison;
	public static String PrimitiveRatherThanWrapperFix_description;
	public static String ImportsFix_OrganizeImports_Description;
	public static String Java50Fix_add_annotations_change_name;
	public static String Java50Fix_add_type_parameters_change_name;
	public static String PotentialProgrammingProblemsFix_add_id_change_name;

	public static String PotentialProgrammingProblemsFix_calculatingUIDFailed_exception;
	public static String PotentialProgrammingProblemsFix_calculatingUIDFailed_unknown;
	public static String SortMembersFix_Change_description;
	public static String SortMembersFix_Fix_description;
	public static String UnusedCodeFix_change_name;
	public static String UnusedCodeFix_RemoveFieldOrLocal_AlteredAssignments_preview_singular;
	public static String UnusedCodeFix_RemoveFieldOrLocal_AlteredAssignments_preview_plural;

	public static String UnusedCodeFix_RemoveFieldOrLocal_description;
	public static String UnusedCodeFix_RemoveFieldOrLocal_RemovedAssignments_preview_singular;
	public static String UnusedCodeFix_RemoveFieldOrLocal_RemovedAssignments_preview_plural;
	public static String UnusedCodeFix_RemoveFieldOrLocalWithInitializer_description;
	public static String UnusedCodeFix_RemoveMethod_description;
	public static String UnusedCodeFix_RemoveConstructor_description;
	public static String UnusedCodeFix_RemoveType_description;
	public static String UnusedCodeFix_RemoveImport_description;
	public static String UnusedCodeFix_RemoveParameter_description;
	public static String UnusedCodeFix_RemoveCast_description;
	public static String UnusedCodeFix_RemoveUnusedType_description;
	public static String UnusedCodeFix_RemoveUnusedTypeParameter_description;
	public static String UnusedCodeFix_RemoveUnusedConstructor_description;
	public static String UnusedCodeFix_RemoveUnusedMethodParameter_description;
	public static String UnusedCodeFix_RemoveUnusedPrivateMethod_description;
	public static String UnusedCodeFix_RemoveUnusedField_description;
	public static String UnusedCodeFix_RemoveUnusedVariabl_description;
	public static String UnusedCodeFix_RemoveUnnecessaryArrayCreation_description;
	public static String UnusedCodeFix_RenameToUnnamedVariable_description;

	public static String Java50Fix_AddMissingAnnotation_description;
	public static String Java50Fix_AddDeprecated_description;
	public static String Java50Fix_AddOverride_description;
	public static String Java50Fix_ConvertToEnhancedForLoop_description;
	public static String Java50Fix_AddTypeArguments_description;
	public static String Java50Fix_SerialVersion_default_description;
	public static String Java50Fix_SerialVersion_hash_description;
	public static String Java50Fix_InitializeSerialVersionId_subtask_description;
	public static String Java50Fix_SerialVersion_CalculateHierarchy_description;
	public static String Java50Fix_RemoveUnnecessaryArrayCreation_description;

	public static String StringFix_AddRemoveNonNls_description;
	public static String StringFix_AddNonNls_description;
	public static String StringFix_RemoveNonNls_description;

	public static String ValueOfRatherThanInstantiationFix_description;

	public static String CodeStyleFix_ChangeAccessToStatic_description;
	public static String CodeStyleFix_QualifyWithThis_description;
	public static String CodeStyleFix_ChangeAccessToStaticUsingInstanceType_description;
	public static String CodeStyleFix_ChangeStaticAccess_description;
	public static String CodeStyleFix_ChangeIfToBlock_desription;
	public static String CodeStyleFix_ChangeElseToBlock_description;
	public static String CodeStyleFix_ChangeControlToBlock_description;
	public static String CodeStyleFix_removeThis_groupDescription;
	public static String CodeStyleFix_ChangeAccessUsingDeclaring_description;
	public static String CodeStyleFix_QualifyMethodWithDeclClass_description;
	public static String CodeStyleFix_QualifyFieldWithDeclClass_description;

	public static String InvertEqualsFix_invert;
	public static String StandardComparisonFix_compare_to_zero;

	public static String SerialVersion_group_description;

	public static String ControlStatementsFix_removeIfBlock_proposalDescription;
	public static String ControlStatementsFix_removeElseBlock_proposalDescription;
	public static String ControlStatementsFix_removeIfElseBlock_proposalDescription;
	public static String ControlStatementsFix_removeBrackets_proposalDescription;

	public static String ExpressionsFix_addParanoiacParentheses_description;
	public static String ExpressionsFix_removeUnnecessaryParentheses_description;
	public static String VariableDeclarationFix_add_final_change_name;

	public static String VariableDeclarationFix_changeModifierOfUnknownToFinal_description;
	public static String VariableDeclarationFix_ChangeMidifiersToFinalWherPossible_description;

	public static String DoWhileRatherThanWhileFix_description;
	public static String NullAnnotationsFix_add_annotation_change_name;
	public static String NullAnnotationsRewriteOperations_change_method_parameter_nullness;
	public static String NullAnnotationsRewriteOperations_change_target_method_parameter_nullness;
	public static String NullAnnotationsRewriteOperations_change_method_return_nullness;
	public static String NullAnnotationsRewriteOperations_change_overridden_parameter_nullness;
	public static String NullAnnotationsRewriteOperations_change_overridden_return_nullness;
	public static String NullAnnotationsRewriteOperations_remove_redundant_nullness_annotation;
	public static String NullAnnotationsRewriteOperations_add_missing_default_nullness_annotation;

	public static String ExternalNullAnnotationChangeProposals_add_nullness_annotation;
	public static String ExternalNullAnnotationChangeProposals_add_nullness_array_annotation;
	public static String ExternalNullAnnotationChangeProposals_remove_nullness_annotation;

	public static String ExtractToNullCheckedLocalProposal_extractCheckedLocal_editName;
	public static String ExtractToNullCheckedLocalProposal_extractToCheckedLocal_proposalName;
	public static String ExtractToNullCheckedLocalProposal_todoHandleNullDescription;
	public static String PatternFix_convert_string_to_pattern_object;
	public static String LambdaExpressionsFix_convert_to_anonymous_class_creation;
	public static String LambdaExpressionsFix_convert_to_lambda_expression;
	public static String LambdaExpressionsFix_convert_to_lambda_expression_removes_annotations;
	public static String PatternInstanceof_convert_if_to_switch;
	public static String PatternMatchingForInstanceofFix_refactor;
	public static String SwitchExpressionsFix_convert_to_switch_expression;
	public static String SwitchFix_convert_if_to_switch;

	public static String TypeParametersFix_insert_inferred_type_arguments_description;
	public static String TypeParametersFix_insert_inferred_type_arguments_name;
	public static String TypeParametersFix_remove_redundant_type_arguments_description;
	public static String TypeParametersFix_remove_redundant_type_arguments_name;
	public static String BooleanValueRatherThanComparisonFix_description;
	public static String PlainReplacementFix_use_plain_text;
	public static String UseStringIsBlankCleanUp_description;
	public static String RedundantComparatorFix_remove_comparator;
	public static String ArrayWithCurlyFix_description;
	public static String ReturnExpressionFix_description;

	public static String OneIfRatherThanDuplicateBlocksThatFallThroughFix_description;
	public static String PullOutIfFromIfElseFix_description;

	public static String TypeAnnotationFix_move;
	public static String TypeAnnotationFix_remove;
	public static String ConstantsCleanUpFix_refactor;
	public static String StringBufferToStringBuilderFix_convert_msg;
	public static String StringConcatToTextBlockFix_convert_msg;
	public static String LambdaExpressionAndMethodRefFix_clean_up_expression_msg;
	public static String InlineDeprecatedMethod_msg;
	public static String ReplaceDeprecatedField_msg;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, FixMessages.class);
	}
}
