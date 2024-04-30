package com.chatoyment.ahoyapp.OnlyJava;

import android.os.AsyncTask;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;
import java.util.Date;

public class OnlineDate {

    private static Date lastFetchedDate;

    public interface OnDateFetchedListener {
        void onDateFetched(Date date);
    }

    public static void fetchDateAsync(OnDateFetchedListener listener) {
        new NtpDateFetchTask(listener).execute();
    }

    public static Date getDate() {
        return lastFetchedDate;
    }

    private static class NtpDateFetchTask extends AsyncTask<Void, Void, Date> {
        private OnDateFetchedListener mListener;

        public NtpDateFetchTask(OnDateFetchedListener listener) {
            mListener = listener;
        }

        @Override
        protected Date doInBackground(Void... voids) {
            try {
                String ntpServer = "time.windows.com";
                NTPUDPClient timeClient = new NTPUDPClient();
                timeClient.setDefaultTimeout(1000);

                InetAddress inetAddress = InetAddress.getByName(ntpServer);
                TimeInfo timeInfo = timeClient.getTime(inetAddress);

                long currentTimeMillis = timeInfo.getMessage().getTransmitTimeStamp().getTime();

                timeClient.close();
                return new Date(currentTimeMillis);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Date date) {
            lastFetchedDate = date;
            mListener.onDateFetched(date);
        }
    }
}
