package com.example.dbms_app;


public interface Server {
    String serverURL = "http://192.168.43.165:5000/";
//    static boolean checkServer(){
//        final boolean[] result = new boolean[1];
//        AsyncTask.execute(() -> {
//            try {
//                URL url = new URL(serverURL);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setConnectTimeout(2000);
//                connection.connect();
//                result[0] =  true;
//                connection.disconnect();
//            }catch (IOException e) {
//                result[0] = false;
//            }
//        });
//
//        return result[0];
//    }
}
