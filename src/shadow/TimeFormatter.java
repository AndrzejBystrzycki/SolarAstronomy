package shadow;

import sunastronomy.BasicSolarCalculator;

//import toolsdebug.*;
import tools.*;

/**
 * 
 * @author Andrzej Bystrzycki
 *
 */
public class TimeFormatter
{
	public static void main(String[] args)
	{
		System.out.println(formatHoursHHMMSSSmith(12.67));
		System.out.println(formatHoursHHMMSSSmith(2.67));
	}
	

	public static String formatHoursHHMMSSOld(double _hours)
	{
		int sign = 1;
		if(_hours < 0) sign = -1;
		_hours = Math.abs(_hours);
		
		int hours = (int) _hours;
		double dec_minutes = (_hours - hours) * 60;
		int minutes = (int) dec_minutes;
		double dec_seconds = (dec_minutes - minutes) * 60;
		int seconds = (int) dec_seconds;
		double remainder = dec_seconds - seconds;
		if(remainder >= 0.5) seconds += 1;
		return String.format("%02d:%02d:%02d", sign * hours, minutes, seconds);
	}


	public static String formatHoursHHMMSS(double _hours)
	{
		//int sign = 1;
		//if(_hours < 0) sign = -1;
		double abshours = Math.abs(_hours);
		
		int hours = (int) abshours;
		double dec_minutes = (abshours - hours) * 60;
		int minutes = (int) dec_minutes;
		double dec_seconds = (dec_minutes - minutes) * 60;
		int seconds = (int) dec_seconds;
		double remainder = dec_seconds - seconds;
		
		Assert.msg("hours="+hours+" minutes="+minutes+" dec_seconds="+dec_seconds);
		
		if(remainder >= 0.5)
		{
			seconds += 1;
			if(seconds == 60)
			{
				seconds = 0;
				minutes +=1;
				if(minutes == 60)
				{
					minutes = 0;
					hours += 1;
					// if hours == 24, it is OK.
				}
			}
		}
		
		String signs = "";
		if(_hours < 0) signs = "-";
		
		return String.format("%1s%02d:%02d:%02d", signs, hours, minutes, seconds);
	}


	/**
	 * "Practical Astronomy with your Calculator or Spreadsheet"
     * Peter Duffett-Smith and Jonathan Zwart
	 * It does not work for 10.766666666666668.
	 * It should return 10:46:00, but it returns 10:47:00.
	 * @param _hours
	 * @return
	 */
	public static String formatHoursHHMMSSSmith(double _hours)
	{
		double UnsignedDecimal = Math.abs(_hours);
		Assert.msg("UnsignedDecimal="+UnsignedDecimal);
		double TotalSeconds = UnsignedDecimal * 3600.0;
		Assert.msg("TotalSeconds="+TotalSeconds);
		int Seconds = (int)Math.round(BasicSolarCalculator.Modulo(TotalSeconds, 60.0));
		Assert.msg("Seconds="+Seconds);
		int CorrectedSeconds = Seconds;
		if(CorrectedSeconds == 60)
			CorrectedSeconds = 0;
		Assert.msg("CorrectedSeconds="+CorrectedSeconds);
		double CorrectedRemainder = TotalSeconds;
		if(Seconds == 60) CorrectedRemainder += 60;
		Assert.msg("CorrectedRemainder="+CorrectedRemainder);
		Assert.msg("1 "+CorrectedRemainder / 60);
		Assert.msg("2 "+Math.floor(CorrectedRemainder / 60));
		Assert.msg("3 "+BasicSolarCalculator.Modulo(Math.floor(CorrectedRemainder / 60), 60.0));
		int Minutes = (int)BasicSolarCalculator.Modulo(Math.floor(CorrectedRemainder / 60), 60.0);
		Assert.msg("Minutes="+Minutes);
		int UnsignedHours = (int)Math.floor(CorrectedRemainder / 3600);
		Assert.msg("UnsignedHours="+UnsignedHours);
		int SignedHours = UnsignedHours;
		if(_hours < 0) SignedHours = - SignedHours;
		Assert.msg("SignedHours="+SignedHours);
		
		return String.format("%02d:%02d:%02d", SignedHours, Minutes, CorrectedSeconds);
	}
	
	public static String formatHoursHHMM(double _hours)
	{
		int sign = 1;
		if(_hours < 0) sign = -1;
		_hours = Math.abs(_hours);

		int hours = (int) _hours;
		double dec_minutes = (_hours - hours) * 60;
		int minutes = (int) dec_minutes;
		double remainder = dec_minutes - minutes;
		if(remainder >= 0.5) minutes += 1;
		return String.format("%02d:%02d", sign * hours, minutes);
	}
	
	/**
	 * "Practical Astronomy with your Calculator or Spreadsheet"
     * Peter Duffett-Smith and Jonathan Zwart
	 * @param _Hours
	 * @param _Minutes
	 * @param _Seconds
	 * @return
	 */
	public static double convertHMStoDH(double _Hours, double _Minutes, double _Seconds)
	{
		double A = Math.abs(_Seconds) / 60;
		double B = (Math.abs(_Minutes) + A) / 60;
		double C = Math.abs(_Hours) + B;
		double D = C;
		if(_Hours < 0 || _Minutes < 0 || _Seconds < 0)
			D = -C;
		
		return D;
	}
	
	public static HMS convertDHToHMS(double _hours)
	{
		//int sign = 1;
		//if(_hours < 0) sign = -1;
		double abshours = Math.abs(_hours);
		
		int hours = (int) abshours;
		double dec_minutes = (abshours - hours) * 60;
		int minutes = (int) dec_minutes;
		double dec_seconds = (dec_minutes - minutes) * 60;
		int seconds = (int) dec_seconds;
		double remainder = dec_seconds - seconds;
		
		Assert.msg("hours="+hours+" minutes="+minutes+" dec_seconds="+dec_seconds);
		
		if(remainder >= 0.5)
		{
			seconds += 1;
			if(seconds == 60)
			{
				seconds = 0;
				minutes +=1;
				if(minutes == 60)
				{
					minutes = 0;
					hours += 1;
					// if hours == 24, it is OK.
				}
			}
		}
		
		if(_hours < 0)
		{
			if(hours != 0)
				hours = -hours;
			else if(minutes != 0)
				minutes = -minutes;
			else if(seconds != 0)
				seconds = -seconds;
		}
		
		HMS hms = new HMS();
		
		hms.hours = hours;
		hms.minutes = minutes;
		hms.seconds = seconds;
		
		return hms;
	}

	//Prints out HH:MM:SS
    public static String GetReadableTime(double finalBuildTime){

        int hours = (int) Math.floor(finalBuildTime);
        int remainderInSeconds = (int)(3600.0* (finalBuildTime - Math.floor(finalBuildTime)) );
        int seconds = remainderInSeconds % 60;
        int minutes = remainderInSeconds / 60;
        return  String.format("%02d:%02d:%02d", hours, minutes, seconds); 
    }
	
    //Prints out HH:MM:SS
    public static String GetReadableTime1(double finalBuildTime){

    	int hours = (int) finalBuildTime;
    	int minutes = (int) ((finalBuildTime - hours) * 60); // 53 ok
        int seconds = (int) ((finalBuildTime - hours) * 3600 - minutes * 60 + 0.5); // 24 ok
        return  String.format("%02d:%02d:%02d", hours, minutes, seconds); 
    }
	
    //Prints out HH:MM:SS
    public static String GetReadableTime2(double finalBuildTime)
    {
    	int hours = (int)finalBuildTime;
    	int minutes = (int)((finalBuildTime - hours) * 60);
    	int seconds = (int)((((finalBuildTime - hours) * 60) - minutes ) * 60);
        return  String.format("%02d:%02d:%02d", hours, minutes, seconds); 
    	
    }

}
