package shadow;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

import net.e175.klaus.solarpositioning.SPA;
import sunastronomy.BasicSolarCalculator;
import sunastronomy.BasicSolarShadow;
import toolsdebug.Assert;

/**
 * 
 * @author Andrzej Bystrzycki
 *
 */
public class BarrierShadowGeometry
{

	public static void main(String[] args)
	{
		int Year = 2014;
		int Month = 4;
		int Day = 1;
		int TZ = -5;
		int DST = 0;
		// photo 2 coordinates of the bridge
		double Latitude = 8.812407; 
		double Longitude = -82.425896;
		// see solarpositioning-master\deltat.data
		// difference between Universal Time and Terrestrial Time
		double deltaT = 67.39; 
		
		GregorianCalendar time = new GregorianCalendar(
				new SimpleTimeZone(((TZ + DST)) * 60 * 60 * 1000, "Panama"));
		time.set(Year, Month - 1, Day, 12, 0, 0);
		GregorianCalendar[] ss = SPA.calculateSunriseTransitSet(time, Latitude, Longitude, deltaT);
		Assert.msg("sunrise=" + ss[0].getTime());
		Assert.msg("solar noon=" + ss[1].getTime());
		Assert.msg("sunset=" + ss[2].getTime());

		double TSunriseHours = TimeFormatter.convertHMStoDH(ss[0].get(Calendar.HOUR_OF_DAY), ss[0].get(Calendar.MINUTE),
				ss[0].get(Calendar.SECOND));
		System.out.println("sunrise hours=" + TSunriseHours);

		// The approximation of the azimuth of the bridge
		double AzB = 90 + Math.toDegrees(Math.atan(78.0/41.0));
		Assert.msg("AzB="+AzB);
		double HoursFromAzimuth = BasicSolarShadow.calculateTimeUsingAzimuthSunriseset(Year, Month, Day, Latitude, Longitude, TZ, DST, AzB, ss);
		
		// photo 2 (Kris on the bridge)
		double ShadowLength = 51.0;
		double ObjectHeight = 162.0;
		calculateTime(Year, Month, Day, Latitude, Longitude, TZ, DST, AzB, ShadowLength, ObjectHeight, TSunriseHours, HoursFromAzimuth);
	}

	private static double shadowLen(double _SunElev, double _SunAz, double _PlaneAz, double _ObjHeight)
	{
		return Math.abs(_ObjHeight / Math.tan(_SunElev) * Math.sin(_PlaneAz - _SunAz));
	}

	public static void calculateTime(int _Year, int _Month, int _Day, double _LatDeg, double _LongDeg, double _TZ,
			double _DST, double _ObjAzimuth, double _ShadowLen, double _ObjectHeight, double _FromHour, double _ToHour)
	{
		double ObjAzimuth = Math.toRadians(_ObjAzimuth);
		
		Calendar Date = Calendar.getInstance();
		Date.set(_Year, _Month - 1, _Day);
		Date.set(Calendar.HOUR_OF_DAY, 0);
		Date.set(Calendar.MINUTE, 0);
		Date.set(Calendar.SECOND, 0);
		Date.set(Calendar.MILLISECOND, 0);


		double ShadowLenDiff = Double.MAX_VALUE;

		HMS hms = new HMS();
		// TimeFormatter.convertDHToHMS(SolarNoonHours);

		HMS FoundHour = null;
		double Hour = _FromHour;
		double AzFound = 0.0;
		double ElevFound = 0.0;
		double ShadowLenFound = 0.0;

		// Find Sun's position for minimum difference between shadow lengths.
		// Hour passes from HoursForAzimuth to sunrise.
		int Counter = 0;
		double ShadowLen = 0.0;
		while (Hour <= _ToHour)
		{
			hms = TimeFormatter.convertDHToHMS(Hour);
			Date.set(Calendar.HOUR_OF_DAY, hms.hours);
			Date.set(Calendar.MINUTE, hms.minutes);
			Date.set(Calendar.SECOND, hms.seconds);

			BasicSolarCalculator.calculateSunPosition(Date, _LatDeg, _LongDeg, _TZ, _DST);
			double Elevation = BasicSolarCalculator.getSunElevationRad();
			double Az = BasicSolarCalculator.getSunAzimuthRad();
			ShadowLen = shadowLen(Elevation, Az, ObjAzimuth, _ObjectHeight);

			double ShadowLenDiffLoop = Math.abs(_ShadowLen - ShadowLen);
			Counter++;
			if (Counter < 10)
			{
				Assert.msg("ShadowLen=" + ShadowLen + " _ShadowLen=" + _ShadowLen + " ShadowLenDiffLoop=" + ShadowLenDiffLoop);
			}

			if (ShadowLenDiffLoop < ShadowLenDiff)
			{
				ShadowLenDiff = ShadowLenDiffLoop;
				AzFound = Az;
				ElevFound = Elevation;
				ShadowLenFound = ShadowLen;
				FoundHour = hms;
			}

			// Increase Hour by 1 second
			Hour = Hour + 1.0 / 3600.0;
		}
		System.out.println(
				"found time " + String.format("%02d:%02d:%02d", FoundHour.hours, FoundHour.minutes, FoundHour.seconds));
		System.out.println("ShadowLenFound=" + ShadowLenFound);
		double azdeg = BasicSolarCalculator.Modulo(Math.toDegrees(AzFound), 360.0);
		System.out.println("azdeg=" + azdeg);
		double az1 = Math.toRadians(azdeg);
		System.out.println("1 az1=" + az1);
		double b = ShadowLenFound / Math.abs(Math.sin(ObjAzimuth - az1));
		System.out.println("b=" + b);
		System.out.println("ElevFound=" + Math.toDegrees(ElevFound) + " AzFound=" + azdeg);

	}
	
}
