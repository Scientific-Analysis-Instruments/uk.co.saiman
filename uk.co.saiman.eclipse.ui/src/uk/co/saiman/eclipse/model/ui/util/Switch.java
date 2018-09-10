/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.eclipse.ui.
 *
 * uk.co.saiman.eclipse.ui is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.ui is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 */
package uk.co.saiman.eclipse.model.ui.util;

import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.MContribution;

import org.eclipse.e4.ui.model.application.commands.MHandlerContainer;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MLocalizable;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.MUILabel;

import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;
import org.eclipse.e4.ui.model.application.ui.menu.MItem;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import uk.co.saiman.eclipse.model.ui.*;
import uk.co.saiman.eclipse.model.ui.Package;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see uk.co.saiman.eclipse.model.ui.Package
 * @generated
 */
public class Switch<T1> extends org.eclipse.emf.ecore.util.Switch<T1> {
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static Package modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Switch() {
    if (modelPackage == null) {
      modelPackage = Package.eINSTANCE;
    }
  }

  /**
   * Checks whether this is a switch for the given package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param ePackage the package in question.
   * @return whether this is a switch for the given package.
   * @generated
   */
  @Override
  protected boolean isSwitchFor(EPackage ePackage) {
    return ePackage == modelPackage;
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  @Override
  protected T1 doSwitch(int classifierID, EObject theEObject) {
    switch (classifierID) {
      case Package.CELL: {
        Cell cell = (Cell)theEObject;
        T1 result = caseCell(cell);
        if (result == null) result = caseUILabel(cell);
        if (result == null) result = caseContext(cell);
        if (result == null) result = caseContribution(cell);
        if (result == null) result = caseElementContainer(cell);
        if (result == null) result = caseHandlerContainer(cell);
        if (result == null) result = caseUIElement(cell);
        if (result == null) result = caseLocalizable(cell);
        if (result == null) result = caseApplicationElement(cell);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case Package.TREE: {
        Tree tree = (Tree)theEObject;
        T1 result = caseTree(tree);
        if (result == null) result = caseContext(tree);
        if (result == null) result = caseContribution(tree);
        if (result == null) result = caseElementContainer(tree);
        if (result == null) result = caseHandlerContainer(tree);
        if (result == null) result = caseUIElement(tree);
        if (result == null) result = caseApplicationElement(tree);
        if (result == null) result = caseLocalizable(tree);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case Package.CELL_CONTRIBUTION: {
        CellContribution cellContribution = (CellContribution)theEObject;
        T1 result = caseCellContribution(cellContribution);
        if (result == null) result = caseContext(cellContribution);
        if (result == null) result = caseContribution(cellContribution);
        if (result == null) result = caseApplicationElement(cellContribution);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case Package.HANDLED_CELL: {
        HandledCell handledCell = (HandledCell)theEObject;
        T1 result = caseHandledCell(handledCell);
        if (result == null) result = caseCell(handledCell);
        if (result == null) result = caseHandledItem(handledCell);
        if (result == null) result = caseContext(handledCell);
        if (result == null) result = caseContribution(handledCell);
        if (result == null) result = caseElementContainer(handledCell);
        if (result == null) result = caseHandlerContainer(handledCell);
        if (result == null) result = caseItem(handledCell);
        if (result == null) result = caseUILabel(handledCell);
        if (result == null) result = caseUIElement(handledCell);
        if (result == null) result = caseLocalizable(handledCell);
        if (result == null) result = caseApplicationElement(handledCell);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      default: return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Cell</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Cell</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T1 caseCell(Cell object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Tree</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Tree</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T1 caseTree(Tree object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Cell Contribution</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Cell Contribution</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T1 caseCellContribution(CellContribution object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Handled Cell</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Handled Cell</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T1 caseHandledCell(HandledCell object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Localizable</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Localizable</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @since 1.1
   * @generated
   */
  public T1 caseLocalizable(MLocalizable object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>UI Label</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>UI Label</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @since 1.0
   * @generated
   */
  public T1 caseUILabel(MUILabel object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Element</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @since 1.0
   * @generated
   */
  public T1 caseApplicationElement(MApplicationElement object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Contribution</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Contribution</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @since 1.0
   * @generated
   */
  public T1 caseContribution(MContribution object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Context</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Context</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @since 1.0
   * @generated
   */
  public T1 caseContext(MContext object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>UI Element</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>UI Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @since 1.0
   * @generated
   */
  public T1 caseUIElement(MUIElement object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Element Container</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Element Container</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @since 1.0
   * @generated
   */
  public <T extends MUIElement> T1 caseElementContainer(MElementContainer<T> object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Handler Container</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Handler Container</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @since 1.0
   * @generated
   */
  public T1 caseHandlerContainer(MHandlerContainer object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Item</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Item</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @since 1.0
   * @generated
   */
  public T1 caseItem(MItem object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Handled Item</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Handled Item</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @since 1.0
   * @generated
   */
  public T1 caseHandledItem(MHandledItem object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch, but this is the last case anyway.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  @Override
  public T1 defaultCase(EObject object) {
    return null;
  }

} //Switch
