package com.example.sid.ennapannradhuneterila;

import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;
import com.twitter.sdk.android.tweetui.UserTimeline;
import com.twitter.sdk.android.core.models.Tweet;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.language.v1.CloudNaturalLanguage;
import com.google.api.services.language.v1.CloudNaturalLanguageRequestInitializer;
import com.google.api.services.language.v1.model.AnnotateTextRequest;
import com.google.api.services.language.v1.model.AnnotateTextResponse;
import com.google.api.services.language.v1.model.Document;
import com.google.api.services.language.v1.model.Features;
import com.google.api.services.language.v1.model.Sentiment;

import java.io.IOException;
import java.time.LocalDate;

import butterknife.ButterKnife;

import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;
import com.twitter.sdk.android.tweetui.UserTimeline;
import com.twitter.sdk.android.core.models.Tweet;

public class TweetCount extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    public static final String API_KEY = "AIzaSyDxHG2TLlUt65zP82goNc9VKz4SYpJe77o";

    private CloudNaturalLanguage naturalLanguageService;
    private Document document;

    private Features features;

    public Double score = 0.0;
    public Double tot=0.0;

    public TextView lala;
    public String s = "";

    public int len;

    public int i, count;

    public float[] depressionScore;
    public float[] happyScore;

    public LineGraphSeries<DataPoint> series1;
    public LineGraphSeries<DataPoint> series2;
    public GraphView graph;
    public Viewport view;

    //public String text;
    //public String tweet;

    public boolean fl = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_count);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b==null)
            return;
        String use = (String)b.get("username");

        graph = (GraphView)findViewById(R.id.graph);
        view = graph.getViewport();
        view.setXAxisBoundsManual(true);
        view.setMinX(-1);
        view.setMaxX(13);
        view.setYAxisBoundsManual(true);
        view.setMinY(0);
        view.setMaxY(5);

        graph.getGridLabelRenderer().setVerticalAxisTitle("Sentiment Score");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Month");

        view.setScrollable(true);

        depressionScore = new float[12];
        happyScore = new float[12];
        for(int i=0; i<12; i++)
        {
            depressionScore[i] = 0.0f;
            happyScore[i] = 0.0f;
        }
        count=0;


        naturalLanguageService = new CloudNaturalLanguage.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                null
        ).setCloudNaturalLanguageRequestInitializer(
                new CloudNaturalLanguageRequestInitializer(API_KEY)
        ).build();

        document = new Document();
        document.setType("PLAIN_TEXT");
        document.setLanguage("en-US");

        features = new Features();
        features.setExtractEntities(true);
        features.setExtractSyntax(true);
        features.setExtractDocumentSentiment(true);

        final AnnotateTextRequest request = new AnnotateTextRequest();
        request.setDocument(document);
        request.setFeatures(features);


        final UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName(use)
                .build();
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(this)
                .setTimeline(userTimeline)
                .build();

        adapter.registerDataSetObserver(new DataSetObserver() {

            public String getMonth(String s)
            {
                if(s.equals("Jan"))
                    return "01";
                if(s.equals("Feb"))
                    return "02";
                if(s.equals("Mar"))
                    return "03";
                if(s.equals("Apr"))
                    return "04";
                if(s.equals("May"))
                    return "05";
                if(s.equals("Jun"))
                    return "06";
                if(s.equals("Jul"))
                    return "07";
                if(s.equals("Aug"))
                    return "08";
                if(s.equals("Sep"))
                    return "09";
                if(s.equals("Oct"))
                    return "10";
                if(s.equals("Nov"))
                    return "11";
                if(s.equals("Dec"))
                    return "12";
                return "00";
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged() {

                if(fl)
                    return;
                super.onChanged();
                len = adapter.getCount();

                fl = true;

                LocalDate date = LocalDate.now();
                String dateStr = date.toString();
                String[] d = dateStr.split("\\-");

                final int currMonth = Integer.parseInt(d[1]);
                int currYear = Integer.parseInt(d[0]);

                StaticLabelsFormatter ls = new StaticLabelsFormatter(graph);
                switch(currMonth)
                {
                    case 12:
                        ls.setHorizontalLabels(new String[] {"Jan", "Feb", "Mar", "Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"});
                        break;
                    case 11:
                        ls.setHorizontalLabels(new String[] {"Dec","Jan", "Feb", "Mar", "Apr","May","Jun","Jul","Aug","Sep","Oct","Nov"});
                        break;
                    case 10:
                        ls.setHorizontalLabels(new String[] {"Nov","Dec","Jan", "Feb", "Mar", "Apr","May","Jun","Jul","Aug","Sep","Oct"});
                        break;
                    case 9:
                        ls.setHorizontalLabels(new String[] {"Oct","Nov","Dec","Jan", "Feb", "Mar", "Apr","May","Jun","Jul","Aug","Sep"});
                        break;
                    case 8:
                        ls.setHorizontalLabels(new String[] {"Sep","Oct","Nov","Dec","Jan", "Feb", "Mar", "Apr","May","Jun","Jul","Aug"});
                        break;
                    case 7:
                        ls.setHorizontalLabels(new String[] {"Aug","Sep","Oct","Nov","Dec","Jan", "Feb", "Mar", "Apr","May","Jun","Jul"});
                        break;
                    case 6:
                        ls.setHorizontalLabels(new String[] {"Jul","Aug","Sep","Oct","Nov","Dec","Jan", "Feb", "Mar", "Apr","May","Jun"});
                        break;
                    case 5:
                        ls.setHorizontalLabels(new String[] {"Jun","Jul","Aug","Sep","Oct","Nov","Dec","Jan", "Feb", "Mar", "Apr","May"});
                        break;
                    case 4:
                        ls.setHorizontalLabels(new String[] {"May","Jun","Jul","Aug","Sep","Oct","Nov","Dec","Jan", "Feb", "Mar", "Apr"});
                        break;
                    case 3:
                        ls.setHorizontalLabels(new String[] {"Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec","Jan", "Feb", "Mar"});
                        break;
                    case 2:
                        ls.setHorizontalLabels(new String[] {"Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec","Jan", "Feb"});
                        break;
                    case 1:
                        ls.setHorizontalLabels(new String[] {"Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec","Jan"});
                        break;

                }

                graph.getGridLabelRenderer().setLabelFormatter(ls);

                //lala = (TextView)findViewById(R.id.textView);
                //lala.setText(new Integer(len).toString()+"\n");

                boolean flag = true;
                if(len>200)
                    len = 200;
                for(i=0; i<len; i++)
                {
                    flag = true;
                    String text = adapter.getItem(i).createdAt;
                    String tweet = adapter.getItem(i).text;
                    s+="\n";
                    String[] parts = text.split("\\ ");

                    parts[1] = getMonth(parts[1]);
                    final int mon = Integer.parseInt(parts[1]);
                    int year = Integer.parseInt(parts[5]);

                    if(year==currYear)
                    {
                        s+=parts[1];
                        s+=" ";
                        s+=parts[5];
                        s+="\n";
                        flag = false;
                    }
                    if(year==currYear-1)
                    {
                        if(mon > currMonth)
                        {
                            s+=parts[1];
                            s+=" ";
                            s+=parts[5];
                            s+="\n";
                            flag = false;
                        }
                    }

                    if(flag)
                    {
                        count+=1;
                        continue;
                    }

                    //lala.append(tweet+"\n");

                    if (!TextUtils.isEmpty(tweet)) {
                        document.setContent(tweet);

                        final AnnotateTextRequest request = new AnnotateTextRequest();
                        request.setDocument(document);
                        request.setFeatures(features);

                        try
                        {
                            new AsyncTask<Object, Void, AnnotateTextResponse>() {

                                @Override
                                protected AnnotateTextResponse doInBackground(Object... params) {
                                    AnnotateTextResponse response = null;
                                    try {
                                        response = naturalLanguageService.documents().annotateText(request).execute();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    return response;
                                }

                                @SuppressLint("SetTextI18n")
                                @Override
                                protected void onPostExecute(AnnotateTextResponse response)
                                {
                                    super.onPostExecute(response);
                                    if (response != null) {
                                        Sentiment sent = response.getDocumentSentiment();
                                        score+=sent.getScore();
                                        tot+=1;
                                        //lala.append(new Double(sent.getScore()).toString()+" , "+new Double(sent.getMagnitude()).toString()+"\n");
                                        int pos;
                                        if(currMonth >= mon)
                                        {
                                            pos = currMonth-mon;
                                            //pos = 1;
                                        }
                                        else
                                        {
                                            pos = 12-(mon-currMonth);
                                            //pos = 11;
                                        }
                                        if(sent.getScore() < 0)
                                            depressionScore[pos]+=sent.getScore()*sent.getMagnitude();
                                        else
                                            happyScore[pos] += sent.getScore()*sent.getMagnitude();
                                        count+=1;
                                    }
                                    if(count==len)
                                    {
                                        //lala.append("Lalala "+new Float(happyScore[j]).toString()+" "+new Float(depressionScore[j]).toString()+"\n");
                                        series1 = new LineGraphSeries<DataPoint>();
                                        series2 = new LineGraphSeries<DataPoint>();
                                        series1.setColor(Color.BLUE);
                                        series2.setColor(Color.RED);
                                        series1.setThickness(2);
                                        series2.setThickness(3);
                                        double max=0;
                                        for(int k=0; k<12; k++)
                                        {
                                            series1.appendData(new DataPoint(k, new Double(happyScore[11-k])), true, 40);
                                            series2.appendData(new DataPoint(k, new Double(-depressionScore[11-k])), true, 40);
                                            if(happyScore[k]>max)
                                                max = happyScore[k];
                                            if(depressionScore[k]>max)
                                                max = depressionScore[k];
                                        }
                                        view.setMaxY(max+1);
                                        graph.addSeries(series1);
                                        graph.addSeries(series2);
                                    }
                                }

                            }.execute().get();
                        }

                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }


                    }

                }

            }
        });
    }
}
