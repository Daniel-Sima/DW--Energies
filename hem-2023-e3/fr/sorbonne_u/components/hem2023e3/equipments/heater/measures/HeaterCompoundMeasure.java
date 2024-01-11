package fr.sorbonne_u.components.hem2023e3.equipments.heater.measures;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// real time distributed applications in the Java programming language.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import fr.sorbonne_u.components.hem2023e3.utils.CompoundMeasure;
import fr.sorbonne_u.components.hem2023e3.utils.Measure;
import fr.sorbonne_u.components.hem2023e3.utils.MeasureI;
import fr.sorbonne_u.components.hem2023e3.utils.MeasurementUnit;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>HeaterCompoundMeasure</code> implements the set of
 * measures to be sent when calling the heater logical sensors.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-11-28</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			HeaterCompoundMeasure
extends		CompoundMeasure
implements	HeaterMeasureI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	protected static final int	TARGET_TEMPERATURE_INDEX = 0;
	protected static final int	CURRENT_TEMPERATURE_INDEX = 1;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a heater compound measure from the two measures.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code targetTemperature.getData() == getTargetTemperature()}
	 * pre	{@code targetTemperature.getMeasurementUnit() == getTargetTemperatureMeasurementUnit()}
	 * pre	{@code currentTemperature.getData() == getCurrentTemperature()}
	 * pre	{@code currentTemperature.getMeasurementUnit() == getCurrentTemperatureMeasurementUnit()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param targetTemperature		the measure of the target temperature.
	 * @param currentTemperature	the measure of the current temperature.
	 */
	public			HeaterCompoundMeasure(
		Measure<Double> targetTemperature,
		Measure<Double> currentTemperature
		)
	{
		super(new MeasureI[]{targetTemperature, currentTemperature});

		assert	targetTemperature.getData() == this.getTargetTemperature() :
				new PreconditionException(
						"targetTemperature.getData() == "
						+ "getCurrentTemperature()");
		assert	targetTemperature.getMeasurementUnit() ==
								this.getTargetTemperatureMeasurementUnit() :
				new PreconditionException(
						"targetTemperature.getMeasurementUnit() == "
						+ "getTargetTemperatureMeasurementUnit()");
		assert	currentTemperature.getData() == this.getCurrentTemperature() :
				new PreconditionException(
						"currentTemperature.getData() == "
						+ "getCurrentTemperature()");
		assert	currentTemperature.getMeasurementUnit() ==
								this.getCurrentTemperatureMeasurementUnit() :
				new PreconditionException(
						"currentTemperature.getMeasurementUnit() == "
						+ "getCurrentTemperatureMeasurementUnit()");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.equipments.heater.measures.HeaterMeasureI#isTemperatureMeasures()
	 */
	@Override
	public boolean		isTemperatureMeasures()
	{
		return true;
	}

	@SuppressWarnings("unchecked")
	public double		getTargetTemperature()
	{
		return ((Measure<Double>)
						this.getMeasure(TARGET_TEMPERATURE_INDEX)).getData();
	}

	@SuppressWarnings("unchecked")
	public MeasurementUnit	getTargetTemperatureMeasurementUnit()
	{
		return ((Measure<Double>)
						this.getMeasure(TARGET_TEMPERATURE_INDEX)).
														getMeasurementUnit();
	}

	@SuppressWarnings("unchecked")
	public double		getCurrentTemperature()
	{
		return ((Measure<Double>)
						this.getMeasure(CURRENT_TEMPERATURE_INDEX)).getData();
	}

	@SuppressWarnings("unchecked")
	public MeasurementUnit	getCurrentTemperatureMeasurementUnit()
	{
		return ((Measure<Double>)
						this.getMeasure(CURRENT_TEMPERATURE_INDEX)).
														getMeasurementUnit();
	}
}
// -----------------------------------------------------------------------------
