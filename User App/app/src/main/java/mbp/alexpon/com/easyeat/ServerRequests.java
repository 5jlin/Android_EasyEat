package mbp.alexpon.com.easyeat;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by apple on 2015/10/14.
 */
public class ServerRequests {
    private ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "http://ponpon88810.netii.net/";

    public ServerRequests(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("連接資料中");
        progressDialog.setMessage("請稍候...");
    }

    public void fetchStoreInBackground(GetStoreCallBack serverCallBack) {
        progressDialog.show();
        new fetchStoreAsyncTask(serverCallBack).execute();
    }

    public class fetchStoreAsyncTask extends AsyncTask<Void, Void, Store> {

        GetStoreCallBack serverCallBack;

        public fetchStoreAsyncTask(GetStoreCallBack serverCallBack) {
            this.serverCallBack = serverCallBack;
        }

        @Override
        protected Store doInBackground(Void... params) {

            Store returnedStoreList = null;
            String result = "";

            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);
            HttpClient client = new DefaultHttpClient(httpParams);
            HttpPost httpPost = new HttpPost(SERVER_ADDRESS + "EasyEat_FetchStore.php");

            try {
                HttpResponse httpResponse = client.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                result = EntityUtils.toString(httpEntity);
                JSONArray jsonArray = new JSONArray(result);
                if (jsonArray.length() == 0) {
                    returnedStoreList = null;
                } else {
                    int index = jsonArray.length();
                    String[] id = new String[jsonArray.length()];
                    String[] course = new String[jsonArray.length()];
                    for (int i = 0; i < index; i++) {
                        JSONObject stock_data = jsonArray.getJSONObject(i);
                        id[i] = stock_data.getString("store_id");
                        course[i] = stock_data.getString("store_name");
                    }
                    returnedStoreList = new Store(index, id, course);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return returnedStoreList;
        }

        @Override
        protected void onPostExecute(Store returnedStoreList) {
            progressDialog.dismiss();
            serverCallBack.done(returnedStoreList);
            super.onPostExecute(returnedStoreList);
        }
    }

    public void fetchMenuInBackground(String store_name, GetMenuCallBack serverCallBack) {
        progressDialog.show();
        new fetchMenuAsyncTask(store_name, serverCallBack).execute();
    }

    public class fetchMenuAsyncTask extends AsyncTask<Void, Void, Menus> {

        GetMenuCallBack serverCallBack;
        String store_name;

        public fetchMenuAsyncTask(String store_name, GetMenuCallBack serverCallBack) {
            this.serverCallBack = serverCallBack;
            this.store_name = store_name;
        }

        @Override
        protected Menus doInBackground(Void... params) {

            Menus returnedStoreList = null;
            String result = "";

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            Log.i("TTT", store_name);
            dataToSend.add(new BasicNameValuePair("store", store_name));

            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);
            HttpClient client = new DefaultHttpClient(httpParams);
            HttpPost httpPost = new HttpPost(SERVER_ADDRESS + "EasyEat_FetchMenu.php");

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                result = EntityUtils.toString(httpEntity);
                JSONArray jsonArray = new JSONArray(result);
                if (jsonArray.length() == 0) {
                    returnedStoreList = null;
                } else {
                    int index = jsonArray.length();
                    String[] id = new String[jsonArray.length()];
                    String[] food = new String[jsonArray.length()];
                    int[] money = new int[jsonArray.length()];

                    for (int i = 0; i < index; i++) {
                        JSONObject stock_data = jsonArray.getJSONObject(i);
                        id[i] = stock_data.getString("food_id");
                        food[i] = stock_data.getString("food_name");
                        money[i] = stock_data.getInt("money");
                    }
                    returnedStoreList = new Menus(index, id, food, money);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return returnedStoreList;
        }

        @Override
        protected void onPostExecute(Menus returnedStoreList) {
            progressDialog.dismiss();
            serverCallBack.done(returnedStoreList);
            super.onPostExecute(returnedStoreList);
        }
    }

    public void fetchNumberInBackground(String order, GetNumberCallBack numberCallBack) {
        progressDialog.show();
        new fetchNumberAsyncTask(order, numberCallBack).execute();
    }

    public class fetchNumberAsyncTask extends AsyncTask<Void, Void, String[]> {

        GetNumberCallBack numberCallBack;
        String store, person, order;

        public fetchNumberAsyncTask(String order, GetNumberCallBack numberCallBack) {
            this.numberCallBack = numberCallBack;
            store = "001";
            person = "ALEX";
            this.order = order;
        }

        @Override
        protected String[] doInBackground(Void... params) {

            String returnedNum[] = {"0", "0"};
            String result = "";

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("store", store));
            dataToSend.add(new BasicNameValuePair("person", person));
            dataToSend.add(new BasicNameValuePair("order", order));

            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);
            HttpClient client = new DefaultHttpClient(httpParams);
            HttpPost httpPost = new HttpPost(SERVER_ADDRESS + "EasyEat_Order.php");

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                Log.i("FUCK", "HI");
                result = EntityUtils.toString(httpEntity);
                Log.i("FUCK", result);
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.length() == 0) {
                    returnedNum = null;
                } else {
                    returnedNum[0] = jsonObject.getString("num");
                    returnedNum[1] = jsonObject.getString("now");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return returnedNum;
        }
        @Override
        protected void onPostExecute(String[] returnedNum) {
            progressDialog.dismiss();
            numberCallBack.done(returnedNum);
            super.onPostExecute(returnedNum);
        }

    }
}