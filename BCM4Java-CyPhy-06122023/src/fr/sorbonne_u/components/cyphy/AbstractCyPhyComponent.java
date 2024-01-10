package fr.sorbonne_u.components.cyphy;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide an extension
// of the BCM component model that aims to define a components tailored for
// cyber-physical control systems (CPCS) for Java.
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

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.PluginI;
import fr.sorbonne_u.components.cyphy.connections.CyPhyReflectionInboundPort;
import fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI;
import fr.sorbonne_u.components.cyphy.interfaces.CyPhyReflectionCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.CoordinatorPlugin;
import fr.sorbonne_u.components.reflection.interfaces.ReflectionCI;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

// -----------------------------------------------------------------------------
/**
 * The class <code>AbstractCyPhyComponent</code> add the necessary properties
 * and methods required to turn a standard BCM component into a cyber-physical
 * one.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * Local simulation architectures in {@code simulationArchitectures} are plain
 * architectures producing simulators that can run in isolation within the
 * component.
 * </p>
 * <p>
 * Local simulation architectures can also be part of global inter-component
 * simulation architectures where they appear as atomic models (thanks to the
 * closure of model composition in DEVS).
 * </p>
 * <p>
 * <i>Work in progress...</i>
 * </p>
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
 * <p>Created on : 2019-06-18</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	AbstractCyPhyComponent
extends		AbstractComponent
implements	CyPhyComponentI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** map from local architectures URIs to local architecture
	 *  descriptions.														*/
	protected final Map<String,ArchitectureI>	localSimulatorsArchitectures;
	/** map from root model URIs to local simulation architecture URIs.		*/
	protected final Map<String,String>	architecturesURIs2localSimulatorURIS;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a cyber-physical component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param nbThreads				number of standard threads.
	 * @param nbSchedulableThreads	number of schedulable threads.
	 */
	protected			AbstractCyPhyComponent(
		int nbThreads,
		int nbSchedulableThreads
		)
	{
		super(nbThreads, nbSchedulableThreads);

		this.localSimulatorsArchitectures = new HashMap<>();
		this.architecturesURIs2localSimulatorURIS = new HashMap<>();
	}

	/**
	 * create a cyber-physical component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of the created component.
	 * @param nbThreads					number of standard threads.
	 * @param nbSchedulableThreads		number of schedulable threads.
	 */
	protected			AbstractCyPhyComponent(
		String reflectionInboundPortURI,
		int nbThreads,
		int nbSchedulableThreads
		)
	{
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		this.localSimulatorsArchitectures = new HashMap<>();
		this.architecturesURIs2localSimulatorURIS = new HashMap<>();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#configureReflection(java.lang.String)
	 */
	@Override
	protected void		configureReflection(String reflectionInboundPortURI)
	throws Exception
	{
		this.addOfferedInterface(CyPhyReflectionCI.class);
		try {
			CyPhyReflectionInboundPort rip =
				new CyPhyReflectionInboundPort(reflectionInboundPortURI, this);
			rip.publishPort();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		assert	isOfferedInterface(CyPhyReflectionCI.class) :
				new PostconditionException(
					"isOfferedInterface(CyPhyReflectionCI.class)");
		assert	findInboundPortURIsFromInterface(CyPhyReflectionCI.class)
																	!= null :
				new PostconditionException(
					"findInboundPortURIsFromInterface(CyPhyReflectionCI.class)"
					+ " != null");
		assert	findInboundPortURIsFromInterface(CyPhyReflectionCI.class).length
																		== 1 :
				new PostconditionException(
					"findInboundPortURIsFromInterface(CyPhyReflectionCI.class)"
					+ ".length == 1");
		assert	findInboundPortURIsFromInterface(ReflectionCI.class)[0].
											equals(reflectionInboundPortURI) :
				new PostconditionException(
					"findInboundPortURIsFromInterface(ReflectionCI.class)[0]."
					+ "equals(reflectionInboundPortURI)");
	}

	// -------------------------------------------------------------------------
	// Internal methods
	// -------------------------------------------------------------------------

	/**
	 * return the scheduled executor service of this component with the given
	 * index.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code index >= 0}
	 * pre	{@code isSchedulable(index)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param index	index of a scheduled executor service of this component.
	 * @return		the scheduled executor service of this component with the given index.
	 */
	protected ScheduledExecutorService	getScheduledExecutorService(int index)
	{
		assert	isSchedulable(index) :
				new PreconditionException("isSchedulable(index)");
		return (ScheduledExecutorService) super.getExecutorService(index);
	}

	/**
	 * add a local simulator to this cyber-physical component to describe its
	 * behaviour.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code architecture != null}
	 * pre	{@code !this.isLocalSimulator(architecture.getRootModelURI())}
	 * pre	{@code !isRootModel(architecture.getRootModelURI())}
	 * pre	{@code !this.isArchitectureInstalledLocally(architecture.getRootModelURI())}
	 * post	{@code simulationArchitectureExists(architecture.getArchitectureURI())}
	 * post	{@code isRootModel(architecture.getRootModelURI())}
	 * post	{@code !isArchitectureInstalledLocally(architecture.getRootModelURI())}
	 * </pre>
	 *
	 * @param architecture	local simulation architecture to be added to the component.
	 * @throws Exception 	<i>to do</i>.
	 */
	protected void		addLocalSimulatorArchitecture(
		ArchitectureI architecture
		) throws Exception
	{
		assert	architecture != null :
				new PreconditionException("architecture != null");
		assert	!this.isLocalSimulator(architecture.getRootModelURI()) :
				new PreconditionException(
						"!isRootModel(architecture.getRootModelURI())");

		this.localSimulatorsArchitectures.put(architecture.getRootModelURI(),
											  architecture);
	}

	/**
	 * return a set of the URIs of global architectures in which the local
	 * simulator takes part.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code localSimulatorURI != null && !localSimulatorURI.isEmpty()}
	 * pre	{@code isLocalSimulator(localSimulatorURI)}
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @param localSimulatorURI	URI of a local simulator.
	 * @return					a set of the URIs of global architectures in which the local simulator takes part.
	 */
	protected Set<String>	getArchitectureURIs(String localSimulatorURI)
	{
		Set<String> ret = new HashSet<String>();
		for (Entry<String, String> e :
						this.architecturesURIs2localSimulatorURIS.entrySet()) {
			if (e.getValue().equals(localSimulatorURI)) {
				ret.add(e.getKey());
			}
		}
		return ret;
	}

	// -------------------------------------------------------------------------
	// Component services
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#isLocalSimulator(java.lang.String)
	 */
	@Override
	public boolean		isLocalSimulator(String uri)
	throws Exception
	{
		return this.localSimulatorsArchitectures.containsKey(uri);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#isLocalSimulatorInstalled(java.lang.String)
	 */
	@Override
	public boolean		isLocalSimulatorInstalled(String uri)
	throws Exception
	{
		assert	this.isLocalSimulator(uri) :
				new PreconditionException("simulationArchitectureExists(uri)");

		return this.isInstalled(this.localSimulatorsArchitectures.get(uri).
															getRootModelURI());
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#isSimulationArchitecture(java.lang.String)
	 */
	@Override
	public boolean		isSimulationArchitecture(String architectureURI)
	throws Exception
	{
		return this.architecturesURIs2localSimulatorURIS.
												containsKey(architectureURI);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#isSimulationArchitectureInstalled(java.lang.String)
	 */
	@Override
	public boolean		isSimulationArchitectureInstalled(String architectureURI)
	throws Exception
	{
		return this.isLocalSimulatorInstalled(
								this.architecturesURIs2localSimulatorURIS.
														get(architectureURI));
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#addSimulationArchitecture(java.lang.String, java.lang.String)
	 */
	@Override
	public void		addSimulationArchitecture(
		String architectureURI,
		String localSimulatorURI
		) throws Exception
	{
		assert	architectureURI != null && !architectureURI.isEmpty() :
				new PreconditionException(
						"globalArchitectureURI != null && "
						+ "!globalArchitectureURI.isEmpty()");
		assert	localSimulatorURI != null && !localSimulatorURI.isEmpty() :
				new PreconditionException(
						"localSimulatorURI != null && "
						+ "!localSimulatorURI.isEmpty()");
		assert	!this.isSimulationArchitecture(architectureURI) :
				new PreconditionException(
						"!isGlobalArchitecture(globalArchitectureURI)");
		assert	this.isLocalSimulator(localSimulatorURI) :
				new PreconditionException(
						"isLocalSimulator(localSimulatorURI)");

		this.architecturesURIs2localSimulatorURIS.
								put(architectureURI, localSimulatorURI);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#removeSimulationArchitecture(java.lang.String)
	 */
	@Override
	public void		removeSimulationArchitecture(
		String globalArchitectureURI
		) throws Exception
	{
		assert	globalArchitectureURI != null && !globalArchitectureURI.isEmpty() :
				new PreconditionException(
						"globalArchitectureURI != null && "
						+ "!globalArchitectureURI.isEmpty()");
		assert	!this.isSimulationArchitecture(globalArchitectureURI) :
				new PreconditionException(
						"!isGlobalArchitecture(globalArchitectureURI)");

		this.architecturesURIs2localSimulatorURIS.
											remove(globalArchitectureURI);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#isCoordinatorComponent(java.lang.String)
	 */
	@Override
	public boolean		isCoordinatorComponent(String architectureURI)
	throws Exception
	{
		for (PluginI p : this.installedPlugins.get().values()) {
			if (p instanceof CoordinatorPlugin &&
					((CoordinatorPlugin)p).isArchitecture(architectureURI)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#isAtomicSimulatorComponent(java.lang.String)
	 */
	@Override
	public boolean		isAtomicSimulatorComponent(String architectureURI)
	throws Exception
	{
		String localSimulatorURI =
				this.architecturesURIs2localSimulatorURIS.get(architectureURI);
		return this.isLocalSimulator(localSimulatorURI);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#isSupervisorComponent(java.lang.String)
	 */
	@Override
	public boolean		isSupervisorComponent(String architectureURI)
	throws Exception
	{
		// the URI of the supervisor plug-in must be the URI of the simulation
		// architecture they supervises.
		return this.isInstalled(architectureURI);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#getSimulationManagementInboundPortURI(java.lang.String)
	 */
	@Override
	public String		getSimulationManagementInboundPortURI(
		String modelURI
		) throws Exception
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new PreconditionException(
						"modelURI != null && !modelURI.isEmpty()");
		assert	isInstalled(modelURI) :
				new PreconditionException("isInstalled(modelURI)");

		return ((AbstractSimulatorPlugin)this.getPlugin(modelURI)).
										getSimulationManagementInboundPortURI();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#getModelInboundPortURI(java.lang.String)
	 */
	@Override
	public String		getModelInboundPortURI(String modelURI)
	throws Exception
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new PreconditionException(
						"modelURI != null && !modelURI.isEmpty()");
		assert	isInstalled(modelURI) :
				new PreconditionException("isInstalled(modelURI)");

		return ((AbstractSimulatorPlugin)this.getPlugin(modelURI)).
													getModelInboundPortURI();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#getSimulatorInboundPortURI(java.lang.String)
	 */
	@Override
	public String		getSimulatorInboundPortURI(String modelURI)
	throws Exception
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new PreconditionException(
						"modelURI != null && !modelURI.isEmpty()");
		assert	isInstalled(modelURI) :
				new PreconditionException("isInstalled(modelURI)");

		return ((AbstractSimulatorPlugin)this.getPlugin(modelURI)).
												getSimulatorInboundPortURI();
	}
 }
// -----------------------------------------------------------------------------
