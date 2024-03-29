package fr.sorbonne_u.devs_simulation.examples.molene.utils;

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

// -----------------------------------------------------------------------------
/**
 * The class <code>DoublePiece</code> represents a piece in a piecewise
 * linear double function.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code first <= last}
 * </pre>
 * 
 * <p>Created on : 2018-07-18</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			DoublePiece
{
	public final double		first;
	public final double		last;
	public final double		firstValue;
	public final double		lastValue;

	/**
	 * create a piece.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code first <= last}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param first			beginning of the interval.
	 * @param firstValue	value at the beginning of the interval.
	 * @param last			end of the interval.
	 * @param lastValue		value at the end of the interval.
	 */
	public				DoublePiece(
		double first,
		double firstValue,
		double last,
		double lastValue
		)
	{
		super();
		this.first = first;
		this.last = last;
		this.firstValue = firstValue;
		this.lastValue = lastValue;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String	toString()
	{
		return "[(" + this.first + ", " + this.firstValue + "), " +
				"(" + this.last + ", " + this.lastValue + ")]";
	}
}
// -----------------------------------------------------------------------------
