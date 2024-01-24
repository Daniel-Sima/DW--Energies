package equipments.Fridge.measures;

import fr.sorbonne_u.components.hem2023e3.utils.MeasureI;

/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/
/**
 * The class <code>FridgeMesureI</code> defines a common type and common
 * operations for the Fridge measurements.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2024-10-11</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 */
public interface FridgeMesureI 
extends	MeasureI {
	default boolean		isStateMeasure()		{ return false; }
	default boolean		isTemperatureMeasures()	{ return false; }
}
/***********************************************************************************/
/***********************************************************************************/
/***********************************************************************************/

