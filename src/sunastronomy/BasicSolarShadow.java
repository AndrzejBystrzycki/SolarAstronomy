package sunastronomy;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

import net.e175.klaus.solarpositioning.SPA;
import shadow.HMS;
import shadow.TimeFormatter;
import toolsdebug.*;
//import tools.*;

/**
 * 
 * A class for calculating times of a day when the sun
 * is at the given position in the sky.
 * It contans 2 methods:
 * 1) a method that calculates the times when sun's elevation equals
 *    the given value
 * 2) a method that calculates the time when sun's azimuth equals
 *    the given value
 * 
 * @author Andrzej Bystrzycki
 *
 */
public class BasicSolarShadow
{
	final private static double MIN_PER_DEG = 4.0;
	final private static double DEG_PER_HOUR = 360.0 / 24.0;


	public static void main(String[] args)
	{
		double SunElevation = 0.0;
		
		// El Pianista photo 2
		// Determine Sun's elevation from a shadow length
		// the object's height = 162
		// shadow length = 51
		SunElevation = Math.atan(162.0/51.0);
		
		System.out.println("SunElevation="+Math.toDegrees(SunElevation));
		calculateTimeUsingElevation(2014, 4, 1, 8.812407, -82.4258966, -5, 0, SunElevation);

		// El Pianista photo 1 (1)
		// Determine Sun's elevation from a shadow length
		// the object's height = 420
		// shadow length = 20
		SunElevation = Math.atan(420.0/20.0);
		
		//System.out.println("SunElevation="+Math.toDegrees(SunElevation));
		//calculateTimeUsingElevation(2014, 4, 1, 8.815315, -82.425166, -5, 0, SunElevation);

	}

	
	/**
	 * Calculates the time of the day when sun's elevation equals
	 * the given value _ElevRad.
	 * 
	 * @param _Year
	 * @param _Month
	 * @param _Day
	 * @param _LatDeg
	 * @param _LongDeg
	 * @param _TZ
	 * @param _DST
	 * @param _ElevRad
	 */
	public static void calculateTimeUsingElevation(int _Year, int _Month, int _Day, 
			double _LatDeg, double _LongDeg, double _TZ, double _DST, double _ElevRad)
	{
		Calendar Date = Calendar.getInstance(); 
		Date.set(_Year, _Month -1, _Day);

		GregorianCalendar time = new GregorianCalendar(
				new SimpleTimeZone(((int) (_TZ + _DST)) * 60 * 60 * 1000, "example"));
		time.set(_Year, _Month - 1, _Day, 12, 0, 0);
		GregorianCalendar[] ss = SPA.calculateSunriseTransitSet(time, _LatDeg, _LongDeg, 67);
		//Assert.msg("sunrise=" + ss[0].getTime());
		//Assert.msg("solar noon=" + ss[1].getTime());
		//Assert.msg("sunset=" + ss[2].getTime());

		double SolarNoonHours = TimeFormatter.convertHMStoDH(ss[1].get(Calendar.HOUR_OF_DAY),
				ss[1].get(Calendar.MINUTE), ss[1].get(Calendar.SECOND));
		Assert.msg("solar noon hours=" + SolarNoonHours);

		double TSunriseHours = TimeFormatter.convertHMStoDH(ss[0].get(Calendar.HOUR_OF_DAY), ss[0].get(Calendar.MINUTE),
				ss[0].get(Calendar.SECOND));
		System.out.println("sunrise hours=" + TSunriseHours);

		double TSunsetHours = TimeFormatter.convertHMStoDH(ss[2].get(Calendar.HOUR_OF_DAY), ss[2].get(Calendar.MINUTE),
				ss[2].get(Calendar.SECOND));
		System.out.println("sunset hours=" + TSunsetHours);

	    double ElevDiff = 360.0;
		
	    HMS hms = new HMS();
		TimeFormatter.convertDHToHMS(SolarNoonHours);
		
		HMS FoundHour = null;
	    double Hour = SolarNoonHours;
		double AzFound = 0.0;
		double ElevFound = 0.0;
	    
	    // Find Suns's position for minimum difference between Sun's elevations.
		// Hour passes from the solar noon to sunrise.
	    while(Hour >= TSunriseHours)
	    {
		    hms = TimeFormatter.convertDHToHMS(Hour);
			Date.set(Calendar.HOUR_OF_DAY,hms.hours);
			Date.set(Calendar.MINUTE, hms.minutes);
			Date.set(Calendar.SECOND, hms.seconds);

			BasicSolarCalculator.calculateSunPosition(Date, _LatDeg, _LongDeg, _TZ, _DST);
		    double Alt = BasicSolarCalculator.getSunElevationRad();
		    double Az = BasicSolarCalculator.getSunAzimuthRad();
		    double AltDiffLoop = Math.abs(Alt - _ElevRad);
		    if(AltDiffLoop < ElevDiff)
		    {
		    	ElevDiff = AltDiffLoop;
		    	AzFound = Az;
		    	ElevFound = Alt;
		    	FoundHour = hms;
		    }
		    
		    // Decrease Hour by 1 second
		    Hour = Hour - 1.0/3600.0;		    		    	    	
	    }
		System.out.println("found1 "+String.format("%02d:%02d:%02d",FoundHour.hours,FoundHour.minutes,FoundHour.seconds));
		System.out.println("ElevFound="+Math.toDegrees(ElevFound)+" AzFound="+BasicSolarCalculator.Modulo(Math.toDegrees(AzFound), 360.0));		

	    ElevDiff = 360.0;
		
	    hms = new HMS();
		TimeFormatter.convertDHToHMS(SolarNoonHours);
		
		FoundHour = null;
	    Hour = SolarNoonHours;
		AzFound = 0.0;
		ElevFound = 0.0;
		
	    // Find Suns's position for minimum difference between Sun's altitudes.
		// Hour passes from the solar noon to sunset.
	    while(Hour <= TSunsetHours)
	    {
		    hms = TimeFormatter.convertDHToHMS(Hour);
			Date.set(Calendar.HOUR_OF_DAY,hms.hours);
			Date.set(Calendar.MINUTE, hms.minutes);
			Date.set(Calendar.SECOND, hms.seconds);
		    	    	
		    BasicSolarCalculator.calculateSunPosition(Date, _LatDeg, _LongDeg, _TZ, _DST);
		    double Elev = BasicSolarCalculator.getSunElevationRad();
		    double Az = BasicSolarCalculator.getSunAzimuthRad();
		    double ElevDiffLoop = Math.abs(Elev - _ElevRad);
		    if(ElevDiffLoop < ElevDiff)
		    {
		    	ElevDiff = ElevDiffLoop;
		    	AzFound = Az;
		    	ElevFound = Elev;
		    	FoundHour = hms;
		    }
		    
		    // Increase Hour by 1 second
		    Hour = Hour + 1.0/3600.0;		    
	    }
		System.out.println("found2 "+String.format("%02d:%02d:%02d",FoundHour.hours,FoundHour.minutes,FoundHour.seconds));
		System.out.println("ElevFound="+Math.toDegrees(ElevFound)+" AzFound="+BasicSolarCalculator.Modulo(Math.toDegrees(AzFound),360.0));		
	}


	/**
	 * Calculates the time of the day when sun's elevation equals
	 * the given value _ElevRad.
	 * 
	 * @param _Year
	 * @param _Month
	 * @param _Day
	 * @param _LatDeg
	 * @param _LongDeg
	 * @param _TZ
	 * @param _DST
	 * @param _ElevRad
	 */
	public static void calculateTimeUsingElevationOld(int _Year, int _Month, int _Day, 
			double _LatDeg, double _LongDeg, double _TZ, double _DST, double _ElevRad)
	{
		Calendar Date = Calendar.getInstance(); 
		Date.set(_Year, _Month -1, _Day);
		Date.set(Calendar.HOUR_OF_DAY,12);
		Date.set(Calendar.MINUTE, 0);
		Date.set(Calendar.SECOND, 0);      
		Date.set(Calendar.MILLISECOND, 0);

	    // Sun's position at 12:00
		BasicSolarCalculator.calculateSunPosition(Date, _LatDeg, _LongDeg, _TZ, _DST);
	    
		double DeltaGMT = _TZ + _DST;

		// see 10.5923.j.ijee.20150503.03.pdf chapter 4.1
		double TC1 = MIN_PER_DEG *(_LongDeg - DEG_PER_HOUR * DeltaGMT);
		Assert.msg("TC1="+TC1);
				
		double TC = TC1 + BasicSolarCalculator.getEquationOfTimeMin();
		Assert.msg("TC="+TimeFormatter.formatHoursHHMM(TC));
	    
	    // see 10.5923.j.ijee.20150503.03.pdf chapter 4.2
		// the time of the solar noon by the clock
	    double SolarNoonHours = 12.0 - TC / 60.0;
	    System.out.println("solar noon="+TimeFormatter.formatHoursHHMMSS(SolarNoonHours));
	    
	    double TSunriseHours = BasicSolarCalculator.getTimeOfSunrise();
	    
	    double TSunsetHours = BasicSolarCalculator.getTimeOfSunset();
	    
	    double AltDiff = 360.0;
		
	    HMS hms = new HMS();
		TimeFormatter.convertDHToHMS(SolarNoonHours);
		
		HMS FoundHour = null;
	    double Hour = SolarNoonHours;
		double AzFound = 0.0;
		double AltFound = 0.0;
	    
	    // Find Suns's position for minimum difference between Sun's altitudes.
		// Hour passes from the solar noon to sunrise.
	    while(Hour >= TSunriseHours)
	    {
		    hms = TimeFormatter.convertDHToHMS(Hour);
			Date.set(Calendar.HOUR_OF_DAY,hms.hours);
			Date.set(Calendar.MINUTE, hms.minutes);
			Date.set(Calendar.SECOND, hms.seconds);

			BasicSolarCalculator.calculateSunPosition(Date, _LatDeg, _LongDeg, _TZ, _DST);
		    double Alt = BasicSolarCalculator.getSunElevationRad();
		    double Az = BasicSolarCalculator.getSunAzimuthRad();
		    double AltDiffLoop = Math.abs(Alt - _ElevRad);
		    if(AltDiffLoop < AltDiff)
		    {
		    	AltDiff = AltDiffLoop;
		    	AzFound = Az;
		    	AltFound = Alt;
		    	FoundHour = hms;
		    }
		    
		    // Decrease Hour by 1 second
		    Hour = Hour - 1.0/3600.0;		    		    	    	
	    }
		System.out.println("found1 "+String.format("%02d:%02d:%02d",FoundHour.hours,FoundHour.minutes,FoundHour.seconds));
		System.out.println("AltFound="+Math.toDegrees(AltFound)+" AzFound="+BasicSolarCalculator.Modulo(Math.toDegrees(AzFound), 360.0));		

	    AltDiff = 360.0;
		
	    hms = new HMS();
		TimeFormatter.convertDHToHMS(SolarNoonHours);
		
		FoundHour = null;
	    Hour = SolarNoonHours;
		AzFound = 0.0;
		AltFound = 0.0;
		
	    // Find Suns's position for minimum difference between Sun's altitudes.
		// Hour passes from the solar noon to sunset.
	    while(Hour <= TSunsetHours)
	    {
		    hms = TimeFormatter.convertDHToHMS(Hour);
			Date.set(Calendar.HOUR_OF_DAY,hms.hours);
			Date.set(Calendar.MINUTE, hms.minutes);
			Date.set(Calendar.SECOND, hms.seconds);
		    	    	
		    BasicSolarCalculator.calculateSunPosition(Date, _LatDeg, _LongDeg, _TZ, _DST);
		    double Alt = BasicSolarCalculator.getSunElevationRad();
		    double Az = BasicSolarCalculator.getSunAzimuthRad();
		    double AltDiffLoop = Math.abs(Alt - _ElevRad);
		    if(AltDiffLoop < AltDiff)
		    {
		    	AltDiff = AltDiffLoop;
		    	AzFound = Az;
		    	AltFound = Alt;
		    	FoundHour = hms;
		    }
		    
		    // Increase Hour by 1 second
		    Hour = Hour + 1.0/3600.0;		    
	    }
		System.out.println("found2 "+String.format("%02d:%02d:%02d",FoundHour.hours,FoundHour.minutes,FoundHour.seconds));
		System.out.println("AltFound="+Math.toDegrees(AltFound)+" AzFound="+BasicSolarCalculator.Modulo(Math.toDegrees(AzFound),360.0));		
	}


	public static double calculateTimeUsingAzimuthOld(int _Year, int _Month, int _Day, double _LatDeg, double _LongDeg, double _TZ,
			double _DST, double _AzRad)
	{
		
		Assert.msg("_AzRad="+_AzRad);
		
		Calendar Date = Calendar.getInstance();
		Date.set(_Year, _Month - 1, _Day);
		Date.set(Calendar.HOUR_OF_DAY, 0);
		Date.set(Calendar.MINUTE, 0);
		Date.set(Calendar.SECOND, 0);
		Date.set(Calendar.MILLISECOND, 0);

		GregorianCalendar time = new GregorianCalendar(
				new SimpleTimeZone(((int) (_TZ + _DST)) * 60 * 60 * 1000, "example"));
		time.set(_Year, _Month - 1, _Day, 12, 0, 0);
		GregorianCalendar[] ss = SPA.calculateSunriseTransitSet(time, _LatDeg, _LongDeg, 67);
		Assert.msg("sunrise=" + ss[0].getTime());
		Assert.msg("solar noon=" + ss[1].getTime());
		Assert.msg("sunset=" + ss[2].getTime());

		double SolarNoonHours = TimeFormatter.convertHMStoDH(ss[1].get(Calendar.HOUR_OF_DAY),
				ss[1].get(Calendar.MINUTE), ss[1].get(Calendar.SECOND));
		Assert.msg("sloar noon hours=" + SolarNoonHours);

		double TSunriseHours = TimeFormatter.convertHMStoDH(ss[0].get(Calendar.HOUR_OF_DAY), ss[0].get(Calendar.MINUTE),
				ss[0].get(Calendar.SECOND));
		System.out.println("sunrise hours=" + TSunriseHours);

		double TSunsetHours = TimeFormatter.convertHMStoDH(ss[2].get(Calendar.HOUR_OF_DAY), ss[2].get(Calendar.MINUTE),
				ss[2].get(Calendar.SECOND));
		System.out.println("sunset hours=" + TSunsetHours);

		double AzDiff = Double.MAX_VALUE;

		HMS hms = new HMS();

		double FoundHour = 0.0;
		HMS FoundHMS = null;
		double Hour = TSunriseHours;
		double AzFound = 0.0;
		double ElevFound = 0.0;

		// Find Suns's position for minimum difference between azimuths.
		// Hour passes from sunrise to sunset.
		int Counter = 0;
		while (Hour <= TSunsetHours)
		{
			hms = TimeFormatter.convertDHToHMS(Hour);
			Date.set(Calendar.HOUR_OF_DAY, hms.hours);
			Date.set(Calendar.MINUTE, hms.minutes);
			Date.set(Calendar.SECOND, hms.seconds);

			BasicSolarCalculator.calculateSunPosition(Date, _LatDeg, _LongDeg, _TZ, _DST);
			double Elev = BasicSolarCalculator.getSunElevationRad();
			double Az = BasicSolarCalculator.getSunAzimuthRad();

			double AzDiffLoop = Math.abs(Az - _AzRad);
			Counter++;
			if (Counter < 10)
			{
				//Assert.msg("Az=" + Az + " _AzRad=" + _AzRad + " AzDiffLoop=" + AzDiffLoop);
			}

			if (AzDiffLoop < AzDiff)
			{
				AzDiff = AzDiffLoop;
				AzFound = Az;
				ElevFound = Elev;
				FoundHMS = hms;
				FoundHour = Hour;
			}

			// Inrease Hour by 1 second
			Hour = Hour + 1.0 / 3600.0;
		}
		Assert.msg(
				"found time for azimuth " + String.format("%02d:%02d:%02d", FoundHMS.hours, FoundHMS.minutes, FoundHMS.seconds));
		Assert.msg("AzFound rad=" + AzFound);
		double az1 = BasicSolarCalculator.Modulo(Math.toDegrees(AzFound), 360.0);
		Assert.msg("ElevFound=" + Math.toDegrees(ElevFound) + " AzFound deg=" + az1);

		return FoundHour;
	}

	public static double calculateTimeUsingAzimuth(int _Year, int _Month, int _Day, double _LatDeg, double _LongDeg, double _TZ,
			double _DST, double _AzDeg)
	{
		
		Assert.msg("_AzDeg="+_AzDeg);
		
		Calendar Date = Calendar.getInstance();
		Date.set(_Year, _Month - 1, _Day);
		Date.set(Calendar.HOUR_OF_DAY, 0);
		Date.set(Calendar.MINUTE, 0);
		Date.set(Calendar.SECOND, 0);
		Date.set(Calendar.MILLISECOND, 0);

		GregorianCalendar time = new GregorianCalendar(
				new SimpleTimeZone(((int) (_TZ + _DST)) * 60 * 60 * 1000, "example"));
		time.set(_Year, _Month - 1, _Day, 12, 0, 0);
		GregorianCalendar[] ss = SPA.calculateSunriseTransitSet(time, _LatDeg, _LongDeg, 67);
		Assert.msg("basic sunrise=" + ss[0].getTime());
		Assert.msg("basic solar noon=" + ss[1].getTime());
		Assert.msg("basic sunset=" + ss[2].getTime());

		double SolarNoonHours = TimeFormatter.convertHMStoDH(ss[1].get(Calendar.HOUR_OF_DAY),
				ss[1].get(Calendar.MINUTE), ss[1].get(Calendar.SECOND));
		Assert.msg("solar noon hours=" + SolarNoonHours);

		double TSunriseHours = TimeFormatter.convertHMStoDH(ss[0].get(Calendar.HOUR_OF_DAY), ss[0].get(Calendar.MINUTE),
				ss[0].get(Calendar.SECOND));
		System.out.println("sunrise hours=" + TSunriseHours);

		double TSunsetHours = TimeFormatter.convertHMStoDH(ss[2].get(Calendar.HOUR_OF_DAY), ss[2].get(Calendar.MINUTE),
				ss[2].get(Calendar.SECOND));
		System.out.println("sunset hours=" + TSunsetHours);

		double AzDiff = Double.MAX_VALUE;

		HMS hms = new HMS();

		double FoundHour = 0.0;
		HMS FoundHMS = null;
		double Hour = TSunriseHours;
		double AzFound = 0.0;
		double ElevFound = 0.0;

		// Find Suns's position for minimum difference between azimuths.
		// Hour passes from sunrise to sunset.
		int Counter = 0;
		while (Hour <= TSunsetHours)
		{
			hms = TimeFormatter.convertDHToHMS(Hour);
			Date.set(Calendar.HOUR_OF_DAY, hms.hours);
			Date.set(Calendar.MINUTE, hms.minutes);
			Date.set(Calendar.SECOND, hms.seconds);

			BasicSolarCalculator.calculateSunPosition(Date, _LatDeg, _LongDeg, _TZ, _DST);
			double Elev = BasicSolarCalculator.getSunElevationRad();
			double Az = BasicSolarCalculator.getSunAzimuthDeg();

			double AzDiffLoop = Math.abs(Az - _AzDeg);
			Counter++;
			if (Counter < 10)
			{
				//Assert.msg("Az=" + Az + " _AzRad=" + _AzRad + " AzDiffLoop=" + AzDiffLoop);
			}

			if (AzDiffLoop < AzDiff)
			{
				AzDiff = AzDiffLoop;
				AzFound = Az;
				ElevFound = Elev;
				FoundHMS = hms;
				FoundHour = Hour;
			}

			// Increase Hour by 1 second
			Hour = Hour + 1.0 / 3600.0;
		}
		
		Assert.msg(
				"found time for azimuth " + String.format("%02d:%02d:%02d", FoundHMS.hours, FoundHMS.minutes, FoundHMS.seconds));
		Assert.msg("ElevFound=" + Math.toDegrees(ElevFound) + " AzFound=" + AzFound);

		return FoundHour;
	}


	/**
	 * Calculates the time when the sun has a given azimuth.
	 * The time from sunrise to sunset is checked.
	 * 
	 * @param _Year
	 * @param _Month
	 * @param _Day
	 * @param _LatDeg latitude in degrees
	 * @param _LongDeg longitude in degrees
	 * @param _TZ time zone
	 * @param _DST Daylight Saving Time
	 * @param _AzDeg sun's azimuth
	 * @param _Sunriseset  a table of times: 0th element - time of sunrise, 1st element - time of solar noon, 2nd element = time of sunset
	 * @return the time (decimal hour) when the sun has a given azimuth
	 */
	public static double calculateTimeUsingAzimuthSunriseset(int _Year, int _Month, int _Day, double _LatDeg, double _LongDeg, double _TZ,
			double _DST, double _AzDeg, GregorianCalendar[] _Sunriseset)
	{
		
		Assert.msg("_AzDeg="+_AzDeg);
		
		Calendar Date = Calendar.getInstance();
		Date.set(_Year, _Month - 1, _Day);
		Date.set(Calendar.HOUR_OF_DAY, 0);
		Date.set(Calendar.MINUTE, 0);
		Date.set(Calendar.SECOND, 0);
		Date.set(Calendar.MILLISECOND, 0);

		double SolarNoonHours = TimeFormatter.convertHMStoDH(_Sunriseset[1].get(Calendar.HOUR_OF_DAY),
				_Sunriseset[1].get(Calendar.MINUTE), _Sunriseset[1].get(Calendar.SECOND));
		Assert.msg("solar noon hours=" + SolarNoonHours);

		double TSunriseHours = TimeFormatter.convertHMStoDH(_Sunriseset[0].get(Calendar.HOUR_OF_DAY), _Sunriseset[0].get(Calendar.MINUTE),
				_Sunriseset[0].get(Calendar.SECOND));
		System.out.println("sunrise hours=" + TSunriseHours);

		double TSunsetHours = TimeFormatter.convertHMStoDH(_Sunriseset[2].get(Calendar.HOUR_OF_DAY), _Sunriseset[2].get(Calendar.MINUTE),
				_Sunriseset[2].get(Calendar.SECOND));
		System.out.println("sunset hours=" + TSunsetHours);

		double AzDiff = Double.MAX_VALUE;

		HMS hms = new HMS();

		double FoundHour = 0.0;
		HMS FoundHMS = null;
		double Hour = TSunriseHours;
		double AzFound = 0.0;
		double ElevFound = 0.0;

		// Find Suns's position for minimum difference between the given azimuth
		// and a calculated azimuth.
		// Hour passes from sunrise to sunset.
		int Counter = 0;
		while (Hour <= TSunsetHours)
		{
			hms = TimeFormatter.convertDHToHMS(Hour);
			Date.set(Calendar.HOUR_OF_DAY, hms.hours);
			Date.set(Calendar.MINUTE, hms.minutes);
			Date.set(Calendar.SECOND, hms.seconds);

			BasicSolarCalculator.calculateSunPosition(Date, _LatDeg, _LongDeg, _TZ, _DST);
			double Elev = BasicSolarCalculator.getSunElevationRad();
			double Az = BasicSolarCalculator.getSunAzimuthDeg();

			double AzDiffLoop = Math.abs(Az - _AzDeg);
			Counter++;
			if (Counter < 10)
			{
				//Assert.msg("_AzDeg=" + _AzDeg + " AzDiffLoop=" + AzDiffLoop);
			}

			if (AzDiffLoop < AzDiff)
			{
				AzDiff = AzDiffLoop;
				AzFound = Az;
				ElevFound = Elev;
				FoundHMS = hms;
				FoundHour = Hour;
			}

			// Increase Hour by 1 second
			Hour = Hour + 1.0 / 3600.0;
		}
		Assert.msg(
				"found time for azimuth " + String.format("%02d:%02d:%02d", FoundHMS.hours, FoundHMS.minutes, FoundHMS.seconds));
		Assert.msg("ElevFound=" + Math.toDegrees(ElevFound) + " AzFound=" + AzFound);

		return FoundHour;
	}
}
