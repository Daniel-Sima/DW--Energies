package fr.sorbonne_u.devs_simulation.architectures;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a new
// implementation of the DEVS simulation <i>de facto</i> standard for Java.
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

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTCoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTCoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.simulators.AtomicRTEngine;

// -----------------------------------------------------------------------------
/**
 * The class <code>RTArchitecture</code> extends an DEVS simulation architecture
 * by precising its real time simulation property.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>White-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code accelerationFactor > 0.0}
 * </pre>
 * 
 * <p><strong>Black-box Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2020-12-16</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			RTArchitecture
extends		Architecture
{
	// -------------------------------------------------------------------------
	// Variables and constants
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** the acceleration/deceleration factor that adjusts the pace of
	 *  the simulation upon the real physical time; a value greater than
	 *  1 will force the simulation to run faster in real time than the
	 *  simulated time, while a value under 1 forces it to run slower.		*/
	protected double			accelerationFactor;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a real time model architectural description given the parameters.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition
	 * post	{@code !isComplete() || RTArchitecture.checkInvariant(this)}
	 * </pre>
	 *
	 * @param rootModelURI				URI of the root model in this architecture.
	 * @param atomicModelDescriptors	map from atomic model URIs to their respective descriptor.
	 * @param coupledModelDescriptors	map from coupled model URIs to their respective descriptor.
	 * @param simulationTimeUnit		time unit used by all simulation clocks in the architecture.
	 */
	public				RTArchitecture(
		String rootModelURI,
		Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors,
		Map<String, CoupledModelDescriptor> coupledModelDescriptors,
		TimeUnit simulationTimeUnit
		)
	{
		// see http://www.asciiarmor.com/post/33736615/java-util-uuid-mini-faq
		this(java.util.UUID.randomUUID().toString(), rootModelURI,
			 atomicModelDescriptors, coupledModelDescriptors,
			 simulationTimeUnit,
			 AtomicRTEngine.DEFAULT_ACCELERATION_FACTOR);		
	}

	/**
	 * create a real time model architectural description given the parameters.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code accelerationFactor > 0.0}
	 * post	{@code !isComplete() || RTArchitecture.checkInvariant(this)}
	 * </pre>
	 *
	 * @param architectureURI			URI of this simulation architecture.
	 * @param rootModelURI				URI of the root model in this architecture.
	 * @param atomicModelDescriptors	map from atomic model URIs to their respective descriptor.
	 * @param coupledModelDescriptors	map from coupled model URIs to their respective descriptor.
	 * @param simulationTimeUnit		time unit used by all simulation clocks in the architecture.
	 * @param accelerationFactor		the acceleration factor between the simulation clock and the real time.
	 */
	public				RTArchitecture(
		String architectureURI,
		String rootModelURI,
		Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors,
		Map<String, CoupledModelDescriptor> coupledModelDescriptors,
		TimeUnit simulationTimeUnit,
		double accelerationFactor
		)
	{
		super(architectureURI, rootModelURI, atomicModelDescriptors,
			  coupledModelDescriptors, simulationTimeUnit);

		assert	accelerationFactor > 0.0 :
				new AssertionError("Precondition violation: "
											+ "accelerationFactor > 0.0");

		this.accelerationFactor = accelerationFactor;

		assert	!isComplete() || RTArchitecture.checkInvariant(this) :
				new AssertionError("Postcondition violation: "
								+ "!isComplete() || "
								+ "RTArchitecture.checkInvariant(this)");
	}

	/**
	 * check the invariant of the given real time simulation architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code a != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param a	real time simulation architecture to be checked.
	 * @return	true if the architecture satisfies its invariant.
	 */
	protected static boolean	checkInvariant(RTArchitecture a)
	{
		assert	a != null :
				new AssertionError("Precondition violation: a != null");

		boolean invariant = true;
		invariant &= a.accelerationFactor > 0.0;
		assert	invariant;
		for (Entry<String, AbstractAtomicModelDescriptor> e :
									a.atomicModelDescriptors.entrySet()) {
			invariant &= e.getValue() instanceof RTAtomicModelDescriptor ||
						 e.getValue() instanceof RTAtomicHIOA_Descriptor;
			assert	invariant;
		}
		for (Entry<String, CoupledModelDescriptor> e :
									a.coupledModelDescriptors.entrySet()) {
			invariant &= e.getValue() instanceof RTCoupledModelDescriptor ||
						 e.getValue() instanceof RTCoupledHIOA_Descriptor;
			assert	invariant;
		}

		return invariant;
	}

	// -------------------------------------------------------------------------
	// Instance methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.Architecture#isRealTime()
	 */
	@Override
	public boolean		isRealTime()
	{
		return true;
	}
}
// -----------------------------------------------------------------------------
