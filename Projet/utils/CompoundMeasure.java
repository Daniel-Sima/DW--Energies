package utils;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;

import utils.TimedEntity;
import utils.Measure;
import utils.MeasurementUnit;
import utils.MeasureI;

public class CompoundMeasure 
extends 	TimedEntity
implements 	MeasureI
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** array of measurement data.											*/
	protected final	MeasureI[]		measures;
	
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a composite measure.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code compoundData != null}
	 * pre	{@code measurementUnits != null}
	 * pre	{@code Stream.of(measurementUnits).allMatch(u -> u != null)}
	 * pre	{@code compoundData.length == measurementUnits.length}
	 * post	{@code !isScalar()}
	 * </pre>
	 *
	 * @param compoundData			array of measurement data.
	 * @param measurementUnits		array of the the measurement units used to expressed {@code compositeData}.
	 */
	public				CompoundMeasure(
		Serializable[] compoundData,
		MeasurementUnit[] measurementUnits
		)
	{
		super();

		assert	compoundData != null :
				new PreconditionException("compoundData != null");
		assert	measurementUnits != null :
				new PreconditionException("measurementUnits != null");
		assert	Stream.of(measurementUnits).allMatch(u -> u != null) :
				new PreconditionException(
						"Stream.of(measurementUnits).allMatch(u -> u != null)");
		assert	compoundData.length == measurementUnits.length :
				new PreconditionException(
						"compoundData.length == measurementUnits.length");

		this.measures = new MeasureI[compoundData.length];
		for (int i = 0 ; i < compoundData.length ; i++) {
			this.measures[i] =
					new Measure<>(compoundData[i], measurementUnits[i]);
		}
	}
	
	/**
	 * create a composite measure from an array of scalar measures.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code measures != null}
	 * pre	{@code Stream.of(measures).allMatch(u -> u != null && u.isScalar())}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param measures				array of scalar measures to be put in this composite.
	 */
	public				CompoundMeasure(MeasureI[] measures)
	{
		super(Stream.of(measures).
				map(m -> m.getTimestamp()).
	  					reduce(Instant.ofEpochMilli(0L),
	  						   (acc, t) -> t.compareTo(acc) > 0 ? t : acc));

		assert	measures != null :
				new PreconditionException("measures != null");
		assert	Stream.of(measures).allMatch(u -> u != null && u.isScalar()) :
				new PreconditionException(
						"Stream.of(measures).allMatch("
						+ "u -> u != null && u.isScalar())");

		this.measures = new MeasureI[measures.length];
		for (int i = 0 ; i < measures.length ; i++) {
			this.measures[i] = measures[i];
		}
	}
	
	/**
	 * create a composite measure.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code compoundData != null}
	 * pre	{@code measurementUnits != null}
	 * pre	{@code Stream.of(measurementUnits).allMatch(u -> u != null)}
	 * pre	{@code compoundData.length == measurementUnits.length}
	 * post	{@code !isScalar()}
	 * </pre>
	 *
	 * @param ac					an accelerated clock giving the time reference.
	 * @param compoundData			array of measurement data.
	 * @param measurementUnits		array of the the measurement units used to expressed {@code compositeData}.
	 */
	public				CompoundMeasure(
		AcceleratedClock ac,
		Serializable[] compoundData,
		MeasurementUnit[] measurementUnits
		)
	{
		super(ac);

		assert	compoundData != null :
				new PreconditionException("compoundData != null");
		assert	measurementUnits != null :
				new PreconditionException("measurementUnits != null");
		assert	Stream.of(measurementUnits).allMatch(u -> u != null) :
				new PreconditionException(
						"Stream.of(measurementUnits).allMatch(u -> u != null)");
		assert	compoundData.length == measurementUnits.length :
				new PreconditionException(
						"compoundData.length == measurementUnits.length");

		this.measures = new MeasureI[compoundData.length];
		for (int i = 0 ; i < compoundData.length ; i++) {
			this.measures[i] =
					new Measure<>(ac, compoundData[i], measurementUnits[i]);
		}
	}
	
	/**
	 * create a composite measure from an array of scalar measures.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code measures != null}
	 * pre	{@code Stream.of(measures).allMatch(u -> u != null && u.isScalar())}
	 * pre	{@code Stream.of(measures).allMatch(u -> u.getTimeReference().equals(ac.getClockURI()))}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param ac					an accelerated clock giving the time reference.
	 * @param measures				array of scalar measures to be put in this composite.
	 */
	public				CompoundMeasure(
		AcceleratedClock ac,
		MeasureI[] measures
		)
	{
		super(ac,
			  Stream.of(measures).
			  		map(m -> m.getTimestamp()).
			  				reduce(measures[0].getTimestamp(),
			  					   (acc, t) -> t.compareTo(acc) > 0 ? t : acc));

		assert	measures != null :
				new PreconditionException("measures != null");
		assert	Stream.of(measures).allMatch(u -> u != null && u.isScalar()) :
				new PreconditionException(
						"Stream.of(measures).allMatch("
						+ "u -> u != null && u.isScalar())");
		assert	Stream.of(measures).allMatch(u -> u.getTimeReference().
													equals(ac.getClockURI())) :
				new PreconditionException(
						"Stream.of(measures).allMatch("
						+ "u -> u.getTimeReference().equals(ac.getClockURI()))");

		this.measures = new MeasureI[measures.length];
		for (int i = 0 ; i < measures.length ; i++) {
			this.measures[i] = measures[i];
		}
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see utils.MeasureI#isScalar()
	 */
	@Override
	public boolean		isScalar()
	{
		return false;
	}

	/**
	 * return the number of data in this composite measure.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isScalar()}
	 * post	{@code return > 0}
	 * </pre>
	 *
	 * @return	the number of data in this composite measure.
	 */
	public int			numberOfData()
	{
		return this.measures.length;
	}

	/**
	 * get the data at index {@code i} from this composite measure.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code i >= 0 && i < numberOfData()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param i	index of the sought data.
	 * @return	the data at index {@code i} in this composite measure.
	 */
	public Serializable	getMeasure(int i)
	{
		assert	i >= 0 && i < this.numberOfData() :
				new PreconditionException("i >= 0 && i < numberOfData()");

		return this.measures[i];
	}

	/**
	 * @see utils.TimedEntity#freshness(java.time.Instant)
	 */
	@Override
	protected Duration	freshness(Instant current)
	{
		Duration ret = Duration.ZERO;
		for (int i = 0 ; i < this.measures.length ; i++) {
			Duration temp = ((TimedEntity)this.measures[i]).freshness(current);
			if (temp.compareTo(ret) > 0) {
				ret = temp;
			}
		}
		return ret;
	}

	/**
	 * @see utils.TimedEntity#timeCoherence()
	 */
	@Override
	public Duration		timeCoherence()
	{
		Set<Instant> s = new HashSet<Instant>();
		this.collectTimestamps(s);
		Duration ret = Duration.ZERO;
		for (Instant i1 : s) {
			for (Instant i2 : s) {
				Duration temp = Duration.between(i1, i2);
				if (temp.compareTo(ret) > 0) {
					ret = temp;
				}
			}
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2023e3.utils.TimedEntity#collectTimestamps(java.util.Set)
	 */
	@Override
	protected void		collectTimestamps(Set<Instant> s)
	{
		for (int i = 0 ; i < this.measures.length ; i++) {
			((TimedEntity)this.measures[i]).collectTimestamps(s);
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String		toString()
	{
		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
		sb.append('[');
		this.contentAsString(sb);
		sb.append(']');
		return sb.toString();
	}

	/**
	 * @see utils.TimedEntity#contentAsString(java.lang.StringBuffer)
	 */
	@Override
	protected String	contentAsString(StringBuffer sb)
	{
		StringBuffer local = new StringBuffer();
		for (int i = 0 ; i < this.numberOfData() ; i++) {
			local.append(this.measures[i]);
			if (i < this.numberOfData() - 1) {
				local.append(", ");
			}
		}
		super.contentAsString(local);
		sb.append(local);
		return local.toString();		
	}

}
