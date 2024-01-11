package equipments.Fridge.connections;

import equipments.Fridge.FridgeExternalControlCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

// -----------------------------------------------------------------------------
/**
 * The class <code>FridgeExternalControlConnector</code> implements a
 * connector for the {@code FridgeExternalControlCI} component interface.
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
public class			FridgeExternalControlConnector
extends		AbstractConnector
implements	FridgeExternalControlCI
{
	/**
	 * @see equipments.Fridge.FridgeUserAndControlI#setTargetCoolerTemperature(double)
	 */
	@Override
	public void setTargetCoolerTemperature(double targetCooler) throws Exception
	{
		((FridgeExternalControlCI)this.offering).setTargetCoolerTemperature(targetCooler);
	}
	
	/**
	 * @see equipments.Fridge.FridgeUserAndControlI#getTargetCoolerTemperature()
	 */
	@Override
	public double		getTargetCoolerTemperature() throws Exception
	{
		return ((FridgeExternalControlCI)this.offering).getTargetCoolerTemperature();
	}

	/**
	 * @see equipments.Fridge.FridgeUserAndControlI#getCurrentCoolerTemperature()
	 */
	@Override
	public double		getCurrentCoolerTemperature() throws Exception
	{
		return ((FridgeExternalControlCI)this.offering).getCurrentCoolerTemperature();
	}

	/**
	 * @see equipments.Fridge.FridgeUserAndControlI#setTargetFreezerTemperature(double)
	 */
	@Override
	public void setTargetFreezerTemperature(double targetFreezer) throws Exception
	{
		((FridgeExternalControlCI)this.offering).setTargetFreezerTemperature(targetFreezer);
	}
	
	/**
	 * @see equipments.Fridge.FridgeUserAndControlI#getTargetFreezerTemperature()
	 */
	@Override
	public double		getTargetFreezerTemperature() throws Exception
	{
		return ((FridgeExternalControlCI)this.offering).getTargetFreezerTemperature();
	}

	/**
	 * @see equipments.Fridge.FridgeUserAndControlI#getCurrentFreezerTemperature()
	 */
	@Override
	public double		getCurrentFreezerTemperature() throws Exception
	{
		return ((FridgeExternalControlCI)this.offering).getCurrentFreezerTemperature();
	}
	
	/**
	 * @see equipments.Fridge.FridgeExternalControlCI#getMaxPowerLevel()
	 */
	@Override
	public double		getMaxPowerLevel() throws Exception
	{
		return ((FridgeExternalControlCI)this.offering).getMaxPowerLevel();
	}

	/**
	 * @see equipments.Fridge.FridgeExternalControlCI#setCurrentPowerLevel(double)
	 */
	@Override
	public void			setCurrentPowerLevel(double powerLevel) throws Exception
	{
		((FridgeExternalControlCI)this.offering).
										setCurrentPowerLevel(powerLevel);
	}

	/**
	 * @see equipments.Fridge.FridgeExternalControlCI#getCurrentPowerLevel()
	 */
	@Override
	public double		getCurrentPowerLevel() throws Exception
	{
		return ((FridgeExternalControlCI)this.offering).getCurrentPowerLevel();
	}
}
// -----------------------------------------------------------------------------
