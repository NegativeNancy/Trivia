package com.example.ivodenhertog.trivia;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class TriviaHelper implements Response.Listener<JSONArray>, Response.ErrorListener {

    private final Context globalContext;
    private Callback globalActivity;

    /**
     * Callback to be used by other activities
     **/
    public interface Callback {
        void gotQuestion(Question question);
        void gotError(String message);
    }

    /**
     * Get the current context and make it global.
     * @param context used to set globalContext
     **/
    public TriviaHelper(Context context) {
        globalContext = context;
    }

    /**
     * Request new question.
     * @param activity used to set globalActivity
     */
    public void getNextQuestion(Callback activity) {
        globalActivity = activity;

        RequestQueue queue = Volley.newRequestQueue(globalContext);
        String URL_MENU = "http://jservice.io/api/random";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL_MENU,
                null, this, this);
        queue.add(jsonArrayRequest);
    }


    /**
     * On successful JSON response store data in JSON String Array.
     **/
    @Override
    public void onResponse(JSONArray response) {
        try {
            JSONObject questionObject = response.getJSONObject(0);

            String resivedQuestion = questionObject.getString("question");
            String answer = questionObject.getString("answer");

            Question question = new Question(resivedQuestion, null, answer);

            globalActivity.gotQuestion(question);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * On error return error message.
     **/
    @Override
    public void onErrorResponse(VolleyError error) {
        globalActivity.gotError(error.getMessage());
    }
}

