package com.example.aztlan.aztlan_iot;

import android.app.DownloadManager;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

/**
 * Created by Aztlan on 11/08/2017.
 */

//Classe para montar o "request Queue"


public class MySingleton {

    private static MySingleton mInstance;
    private RequestQueue requestQueue;
    private static Context mCtx;

    //Construtor
    private MySingleton (Context context)
    {
        mCtx = context;
        requestQueue = getRequestQueue();
    }

    //Retorna a instancia da classe
    public static synchronized MySingleton getInstance(Context context)

    {
        if (mInstance == null){
            mInstance = new MySingleton(context);
        }

        return mInstance;
    }

    public RequestQueue getRequestQueue(){

        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue( mCtx.getApplicationContext());
        }

        return requestQueue;
    }


    public <T>void addToRequestQueue(Request<T> request)
    {
        requestQueue.add(request);

    }




}
