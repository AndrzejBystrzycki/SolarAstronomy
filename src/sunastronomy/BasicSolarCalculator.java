package sunastronomy;

import java.util.Calendar;

//import toolsdebug.*;
import tools.*;

/**
 * A class for calculating sun's position.
 * Calculations are based on "Basic Solar Positional Astronomy"
 * by Kevin Karney.
 * http://www.precisedirections.co.uk/Sundials/Basic%20Solar%20Positional%20Astronomy.pdf
 * 
 * @author Andrzej Bystrzycki
 *
 */
public class BasicSolarCalculator
{
	private static double SunDeclinationDeg;
	private static double SunDeclinationRad;
	private static double EquationOfTimeMin;
	private static double SunElevationDeg;
	private static double SunElevationRad;
	private static double SunAzimuthDeg;
	private static double SunAzimuthRad;
	private static double TimeOfSunrise;
	private static double TimeOfSunset;
	private static double SunAzimuthAtSunrise;
	private static double SunAzimuthAtSunset;	
	
	
	public static void main(String[] args)
	{
		Calendar c = Calendar.getInstance();
		c.set(2013, Calendar.FEBRUARY, 2);
	    c.set(Calendar.HOUR_OF_DAY,11);
	    c.set(Calendar.MINUTE, 30);
	    c.set(Calendar.SECOND, 0);      
	    c.set(Calendar.MILLISECOND, 0);
	    
		/*c = Calendar.getInstance();
		c.set(2012, Calendar.DECEMBER, 22);
	    c.set(Calendar.HOUR_OF_DAY,12);
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);      
	    c.set(Calendar.MILLISECOND, 0);*/

	    double Latitude = 37.96667;
	    double Longitude = 23.71667;
	    double TZ = 2.0;
	    double DST = 0.0;

	    //Latitude = -41.0;
	    //Longitude = 0.0;

	    // test example, see Basic Astronomy for the Gnomonist.pdf
	    calculateSunPosition(c, Latitude, Longitude, TZ, DST);
	    System.out.println("el="+getSunElevationDeg()+ " az="+getSunAzimuthDeg());

		c = Calendar.getInstance();
		c.set(2014, Calendar.APRIL, 1);
	    c.set(Calendar.HOUR_OF_DAY,12);
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);      
	    c.set(Calendar.MILLISECOND, 0);
	    
	    Latitude = 8.821247;
	    Longitude = -82.421156;
	    TZ = -5;
	    DST = 0;

	    //calculateSunPosition(c, Latitude, Longitude, TZ, DST);
	    //System.out.println("el="+Math.toDegrees(sp.Elevation)+ " az="+Math.toDegrees(sp.Azimuth));

	    c = Calendar.getInstance();
		c.set(2012, Calendar.DECEMBER, 22);
	    c.set(Calendar.HOUR_OF_DAY,12);
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);      
	    c.set(Calendar.MILLISECOND, 0);
	    
	    Latitude = -41.0;
	    Longitude = 0.0;

	    //calculateSunPosition(c, Latitude, Longitude);
	    //System.out.println("el="+Math.toDegrees(sp.Elevation)+ " az="+Math.toDegrees(sp.Azimuth));
	    
		c = Calendar.getInstance();
		c.set(2013, Calendar.FEBRUARY, 2);
	    c.set(Calendar.HOUR_OF_DAY,11);
	    c.set(Calendar.MINUTE, 30);
	    c.set(Calendar.SECOND, 0);      
	    c.set(Calendar.MILLISECOND, 0);

	    Latitude = 37.96667;
	    Longitude = 23.71667;
	    TZ = 2.0;
	    DST = 0.0;
	    
	    //calculateSunPosition(c, Latitude, Longitude, TZ, DST);

		c = Calendar.getInstance();
		c.set(2014, Calendar.APRIL, 1);
	    c.set(Calendar.HOUR_OF_DAY,13);
	    c.set(Calendar.MINUTE, 45);
	    c.set(Calendar.SECOND, 0);      
	    c.set(Calendar.MILLISECOND, 0);

	    Latitude = 8.859028;
	    Longitude = -82.417978;
	    TZ = -5.0;
	    DST = 0.0;
	    
	    //calculateSunPosotion(c, Latitude, Longitude, TZ, DST);

		c = Calendar.getInstance();
		c.set(2012, Calendar.DECEMBER, 22);
	    c.set(Calendar.HOUR_OF_DAY,12);
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);      
	    c.set(Calendar.MILLISECOND, 0);

	    Latitude = -41;
	    Longitude = 0;
	    TZ = 0.0;
	    DST = 0.0;
	    
	    //calculateSunPosition(c, Latitude, Longitude, TZ, DST);

	    Latitude = -3;
	    Longitude = 0;
	    TZ = 0.0;
	    DST = 0.0;
	    
	    //calculateSunPosotion(c, Latitude, Longitude, TZ, DST);

	    Latitude = 3;
	    Longitude = 0;
	    TZ = 0.0;
	    DST = 0.0;
	    
	    //calculateSunPosition(c, Latitude, Longitude, TZ, DST);

	    Latitude = 41;
	    Longitude = 0;
	    TZ = 0.0;
	    DST = 0.0;
	    
	    calculateSunPosition(c, Latitude, Longitude, TZ, DST);
	}
	
	
	public static double Modulo(double _Number, double _Quotient)
	{
		while (_Number > _Quotient) {
			_Number -= _Quotient;
		}
		while (_Number < 0.0) {
			_Number += _Quotient;
		}
		
		return _Number;		
	}

	
	public static void calculateSunPosition(Calendar _Date, double _LatDeg, double _LongDeg, double _TZ, double _DST)
	{
		// 5 Year
		double YYYY = _Date.get(Calendar.YEAR);

		// 6 Month
		double MM = _Date.get(Calendar.MONTH) + 1;
		
		// 7 Day
		double DD = _Date.get(Calendar.DAY_OF_MONTH);
		
		// str 15 brak wzoru na ST
		// 10 UTC Uncorrected
		double UTCuhrs = _Date.get(Calendar.HOUR_OF_DAY) + _Date.get(Calendar.MINUTE) / 60.0 + _Date.get(Calendar.SECOND) / 3600.0 - _TZ - _DST;
		
		Assert.msg("UTCuhrs="+UTCuhrs);
		
		// 11 UTC Corrected hrs
		double UTChrs = Modulo(UTCuhrs, 24.0);
		
		Assert.msg("UTChrs="+UTChrs);
		
		// 12 UTC Corrected deg
		double UTCDeg = 15.0 * UTChrs;

		Assert.msg("UTCDeg="+UTCDeg);
		
		// 13,14,15 temporary value aaa is the correction to be made 
		// if the local date differs from the date at Greenwich
		int aaa = 0;
		if(UTCuhrs < 0) aaa = -1;
		if(UTCuhrs > 24) aaa = 1;
		
		Assert.msg("aaa="+aaa);
		
		// 16 temporary value
		double bbb = 367.0 * YYYY - 730531.5;

		Assert.msg("bbb="+bbb);
		
		// 17 temporary value
		double ccc = Math.floor((7.0 * Math.floor(YYYY + (MM + 9) / 12)) / 4);
		
		Assert.msg("ccc="+ccc);
		
		/*double x = 275.0 * MM / 9.0;
		System.out.println("MM="+MM+"x="+x);
		x = Math.floor(x);
		System.out.println("x="+x);*/
		
		// 17 temporary value
		double ddd = Math.floor(275.0 * MM / 9.0) + DD;

		Assert.msg("ddd="+ddd);
		
		// 19 Days since midnight
		double Dtodaydays = UTChrs / 24.0;
		
		Assert.msg("Dtodaydays="+Dtodaydays);
		
		// 20 Days to 0:00am since Epoch
		double J2000days = aaa + bbb - ccc + ddd;
		
		Assert.msg("J2000days="+J2000days);
		
		// 22 Days to now since Epoch
		double D2000days = J2000days + Dtodaydays;
		
		Assert.msg("D2000days="+D2000days);

		// 21 Julian Centuries 2000
		double TJulCent = D2000days / 36525;

		Assert.msg("TJulCent="+TJulCent);
		
		// 23 Greenwich Mean Sideral Time hrs
		double GMSThrs = 6.697374558 + 0.06570982441908 * J2000days
				+ 1.00273790935 * UTChrs
				+ 0.000026 * TJulCent * TJulCent;
		GMSThrs = Modulo(GMSThrs, 24.0);

		Assert.msg("GMSThrs="+GMSThrs);
		
		// 24 Greenwich Mean Sideral Time deg
		double GMSTDeg = GMSThrs * 15.0;

		Assert.msg("GMSTdeg="+GMSTDeg);
		
		// 25 Sun's Mean Longitude deg
		double MoDeg = Modulo(GMSTDeg - 180.0 - UTCDeg, 360.0 );

		Assert.msg("MoDeg="+MoDeg);
		
		// 26 Sun's Mean Longitude rad
		double MoRad = Math.toRadians(MoDeg);
		
		Assert.msg("MoRad="+MoRad);

		// 27 Perihelion Longitude deg
		double OmegaDeg = 248.54536 + 0.017196 * YYYY;
		
		Assert.msg("OmegaDeg="+OmegaDeg); // ksi¹¿ka 283.16070 !!!!!! moje obl. 283.160908

		// 28 Perihelion Longitude rad
		double OmegaRad = Math.toRadians(OmegaDeg);

		Assert.msg("OmegaRad="+OmegaRad);
		
		// 29 Eccentricity
		double e =0.017585 - 0.438 * YYYY / 1000000;

		Assert.msg("e="+e);
		
		// 30 Obliguity deg
		double EpsilonDeg = 23.6993 - 0.00013 * YYYY;
		
		Assert.msg("EpsilonDeg="+EpsilonDeg);
		
		// 31 Obliguity rad
		double EpsilonRad = Math.toRadians(EpsilonDeg);
		
		Assert.msg("EpsilonRad="+EpsilonRad);
		
		// 32 Mean anomaly rad
		double MRad = MoRad - OmegaRad;
		
		Assert.msg("Mrad="+MRad);
		
		// an error in the formula in the table, see page 12 formula 2.7
		// MRad instead of MoRad
		// 33 Eccentric anomaly
		double ERad = MRad - Math.sin(MRad) / ( Math.cos(MRad) - 1 / e); 
		
		Assert.msg("ERad="+ERad);

		// This is the Greek letter nu (Larrys_speakeasy.pdf)
		// 35 True anomaly
		double NuRad = 2 * Math.atan(Math.tan(ERad / 2) * Math.sqrt ((1 + e) / (1 - e)));

		Assert.msg("NuRad="+NuRad);

		// an error in the formula in the table, see page 12 formula 2.5
		// NuRad instead of MoRad
		// 36 Sun's true Longitude
		double LambdaRad = NuRad + OmegaRad; 
		
		Assert.msg("LambdaRad="+LambdaRad);
		
		// 37 Sun's true Longitude deg
		double LambdaDeg = Math.toDegrees(LambdaRad);
		
		Assert.msg("LambdaDeg="+LambdaDeg);
		
		// 38 Sun's declination rad
		double DeltaRad = Math.asin(Math.sin(EpsilonRad) * Math.sin(LambdaRad));
		SunDeclinationRad = DeltaRad;
		
		Assert.msg("DeltaRad="+DeltaRad);
		
		// 39 Sun's declination deg
		double DeltaDeg = Math.toDegrees(DeltaRad);
		SunDeclinationDeg = DeltaDeg;

		Assert.msg("DeltaDeg="+DeltaDeg);
		
		// 40 Sun's right ascension rad
		double AlphaRad = Math.atan2( Math.cos(EpsilonRad) * Math.sin(LambdaRad), Math.cos(LambdaRad));
		
		Assert.msg("AlfaRad="+AlphaRad);
		
		// 41 Sun's right ascension deg
		double AlphaDeg = Modulo(Math.toDegrees(AlphaRad), 360.0);
		
		Assert.msg("AlphaDeg="+AlphaDeg);
		
		// 43 Equation of time deg
		double EOTDeg = GMSTDeg - AlphaDeg - UTCDeg + 180.0;

		Assert.msg("EOTDeg="+EOTDeg);
		
		// 44,45 Equation of time astro deg
		double EOTAstroDeg = EOTDeg;
		if(EOTDeg < -180.0) EOTAstroDeg = EOTDeg + 360.0;
		if(EOTDeg > 180.0) EOTAstroDeg = EOTDeg - 360.0;

		Assert.msg("EOTAstroDeg="+EOTAstroDeg);
		
		EquationOfTimeMin = 4 * EOTAstroDeg;
		
		// 46 Equation of time gnomical deg
		double EOTGnomDeg = - EOTAstroDeg;
		
		Assert.msg("EOTGnomDeg="+EOTGnomDeg);
		
		// 47 Equation of time gnomical min
		double EOTGnomMin = 4 * EOTGnomDeg;

		Assert.msg("EOTGnomMin="+EOTGnomMin);

		// 48 Longitude correction degrees
		double SigmaDeg = _LongDeg - _TZ * 15.0;
		
		Assert.msg("SigmaDeg="+SigmaDeg);
		
		// 49 Longitude correction minutes
		double SigmaMin = SigmaDeg * 4.0;

		Assert.msg("SigmaMin="+SigmaMin);
		
		// 50 EOT Longitude corrected
		double EOTLocalMin = EOTGnomMin + SigmaMin;
		
		Assert.msg("EOTLocalMin="+EOTLocalMin);
		
		// I had to change the formula in the table to obtain the result
		// in the table (EquationOfTimeMin instead of EOTGnomMin).
		EOTLocalMin = EquationOfTimeMin + SigmaMin;
		
		Assert.msg("EOTLocalMin="+EOTLocalMin);
		
		// 51 Observer's true hour angle deg
		double HDeg = Modulo(GMSTDeg + _LongDeg - AlphaDeg, 360.0);
		
		Assert.msg("HDeg="+HDeg);
		
		// 52 Observer's true hour angle rad
		double HRad = Math.toRadians(HDeg);
		
		Assert.msg("HRad="+HRad);
		
		// 53 Observer's Latitude rad
		double FiRad = Math.toRadians(_LatDeg);

		Assert.msg("FiRad="+FiRad);
		
		// 54 Sun's altitude rad
		double ARad = Math.asin( Math.sin(FiRad) * Math.sin(DeltaRad)
			+ Math.cos(FiRad) * Math.cos(DeltaRad) * Math.cos(HRad) );
		SunElevationRad = ARad;
		
		Assert.msg("ARad="+ARad);
		
		// 55 Sun's altitude deg
		double ADeg = Math.toDegrees(ARad);
		SunElevationDeg = ADeg;

		Assert.msg("Altitude ADeg="+ADeg);
		
		// 57 sinAz
		double sinAz = - Math.cos(DeltaRad) * Math.sin(HRad) / Math.cos(ARad);
		
		Assert.msg("sinAz="+sinAz);
		
		// 58 cosAz
		double cosAz = ( Math.sin(DeltaRad) - Math.sin(ARad) * Math.sin(FiRad) )
				/ ( Math.cos(ARad) * Math.cos(FiRad));
		Assert.msg("cosAz="+cosAz);
		
		// 59 Sun's azimuth rad
		double AzRad = Math.atan2(sinAz, cosAz);
		SunAzimuthRad = AzRad;
		Assert.msg("AzRad="+AzRad);
		
		// 60 Sun's azimuth deg
		double AzDeg = Modulo(Math.toDegrees(AzRad), 360.0);
		SunAzimuthDeg = AzDeg;
		Assert.msg("Azimuth AzDeg="+AzDeg);
		
		// 66 Local Hr Ang, Sunrise/set
		double HSR_SSDeg = Math.acos(-Math.tan(FiRad) * Math.tan(DeltaRad)) * 180 / Math.PI;
		
		Assert.msg("HSR_SSDeg="+HSR_SSDeg);
		
		// 67 Time of sunrise
		double HSRhrs = 12 - HSR_SSDeg / 15.0 - EOTLocalMin / 60.0;
		
		Assert.msg("HSRhrs="+HSRhrs);
		
		TimeOfSunrise = HSRhrs;

		// 68 Time of sunset
		double HSShrs = 12 + HSR_SSDeg / 15.0 - EOTLocalMin / 60.0;
		
		Assert.msg("HSShrs="+HSShrs);
				
		TimeOfSunset = HSShrs;
		
		// 69 Sun’s Azimuth at Sunrise
		double AzSRDeg = Math.acos(Math.sin(DeltaRad) / Math.cos(FiRad)) * 180.0 / Math.PI;
		
		Assert.msg("AzSRDeg="+AzSRDeg);
		
		SunAzimuthAtSunrise = AzSRDeg;
		
		// 70 Sun’s Azimuth at Sunset
		double AzSSDeg = 360.0 - AzSRDeg;

		Assert.msg("AzSSDeg="+AzSSDeg);
		
		SunAzimuthAtSunset = AzSSDeg;
		
}
	

	public static double getSunDeclinationDeg()
	{
		return SunDeclinationDeg;
	}
	

	public static double getSunDeclinationRad()
	{
		return SunDeclinationRad;
	}
	
	
	public static double getEquationOfTimeMin()
	{
		return EquationOfTimeMin;
	}
	
	
	public static double getSunElevationDeg()
	{
		return SunElevationDeg;
	}
	

	public static double getSunElevationRad()
	{
		return SunElevationRad;
	}
	
	public static double getSunAzimuthDeg()
	{
		return SunAzimuthDeg;
	}
	

	public static double getSunAzimuthRad()
	{
		return SunAzimuthRad;
	}
	

	public static double getTimeOfSunrise()
	{
		return TimeOfSunrise;
	}


	public static double getTimeOfSunset()
	{
		return TimeOfSunset;
	}


	public static double getSunAzimuthAtSunrise()
	{
		return SunAzimuthAtSunrise;
	}
	

	public static double getSunAzimuthAtSunset()
	{
		return SunAzimuthAtSunset;
	}
}
