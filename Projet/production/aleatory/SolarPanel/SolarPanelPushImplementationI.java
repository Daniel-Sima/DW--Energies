package production.aleatory.SolarPanel;

import fr.sorbonne_u.components.interfaces.DataRequiredCI;

// -----------------------------------------------------------------------------
/**
 * The class <code>HeaterPushImplementationI</code>
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
 * <p>Created on : 2023-11-30</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		SolarPanelPushImplementationI
{
	/**
	 * receive and process the state data coming from the solar panel component,
	 * starting the control loop if the state has changed from {@code OFF} to
	 * {@code ON}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code sd != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param sd		state data received from the heater component.
	 */
	public void			receiveDataFromSolarPanel(DataRequiredCI.DataI sd);
}
// -----------------------------------------------------------------------------
