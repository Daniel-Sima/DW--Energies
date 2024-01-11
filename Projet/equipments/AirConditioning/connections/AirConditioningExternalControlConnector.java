package equipments.AirConditioning.connections;

import equipments.AirConditioning.AirConditioningExternalControlCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

// -----------------------------------------------------------------------------
/**
 * The class <code>AirConditioningExternalControlConnector</code> implements a
 * connector for the {@code AirConditioningExternalControlCI} component interface.
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
 * <p>Created on : 2023-10-10</p>
 * 
 * @author <a href="mailto:simadaniel@hotmail.com">Daniel SIMA</a>
 * @author <a href="mailto:walterbeles@gmail.com">Walter ABELES</a>
 */
public class			AirConditioningExternalControlConnector
extends		AbstractConnector
implements	AirConditioningExternalControlCI
{
	/**
	 * @see equipments.AirConditioning.AirConditioningUserAndControlI#getTargetTemperature()
	 */
	@Override
	public double		getTargetTemperature() throws Exception
	{
		return ((AirConditioningExternalControlCI)this.offering).getTargetTemperature();
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningUserAndControlI#getCurrentTemperature()
	 */
	@Override
	public double		getCurrentTemperature() throws Exception
	{
		return ((AirConditioningExternalControlCI)this.offering).getCurrentTemperature();
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningExternalControlCI#getMaxPowerLevel()
	 */
	@Override
	public double		getMaxPowerLevel() throws Exception
	{
		return ((AirConditioningExternalControlCI)this.offering).getMaxPowerLevel();
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningExternalControlCI#setCurrentPowerLevel(double)
	 */
	@Override
	public void			setCurrentPowerLevel(double powerLevel) throws Exception
	{
		((AirConditioningExternalControlCI)this.offering).
										setCurrentPowerLevel(powerLevel);
	}

	/**
	 * @see equipments.AirConditioning.AirConditioningExternalControlCI#getCurrentPowerLevel()
	 */
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return ((AirConditioningExternalControlCI)this.offering).getCurrentPowerLevel();
	}
}
// -----------------------------------------------------------------------------
