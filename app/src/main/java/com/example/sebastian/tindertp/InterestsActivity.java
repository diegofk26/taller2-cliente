package com.example.sebastian.tindertp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.commonTools.ActivityStarter;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;
import com.example.sebastian.tindertp.commonTools.HeaderBuilder;
import com.example.sebastian.tindertp.commonTools.AdapterHashMap;
import com.example.sebastian.tindertp.commonTools.ViewIdGenerator;
import com.example.sebastian.tindertp.internetTools.InfoDownloaderClient;

import java.util.Map;

public class InterestsActivity extends AppCompatActivity {

    private String user;
    private String pass;
    private AdapterHashMap editTextMap;
    private Animation animationAplha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        animationAplha = AnimationUtils.loadAnimation(this,R.anim.alpha);

        editTextMap = new AdapterHashMap();

        if (getIntent().hasExtra(Common.USER_KEY) && getIntent().hasExtra(Common.PASS_KEY)) {
            user = getIntent().getStringExtra(Common.USER_KEY);
            pass = getIntent().getStringExtra(Common.PASS_KEY);
        }

    }

    public void goToRegister(View v) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String tokenGCM = sharedPreferences.getString(Common.TOKEN_GCM, "");
        Map<String,String> values = HeaderBuilder.forRegister(user, pass, tokenGCM);
        TextView text = (TextView)findViewById(R.id.textView17);
        String url = ((TinderTP) this.getApplication()).getUrl();

        if (!url.isEmpty()) {
            ConnectionStruct conn = new ConnectionStruct(Common.REGISTER,Common.PUT,url);
            InfoDownloaderClient info = new InfoDownloaderClient(text, this, values, conn);
            info.runInBackground();

        } else {
            ActivityStarter.startClear(this, UrlActivity.class);
            this.finish();
        }
    }

    private EditText editText(int newID, int editTextID, String hint, TextView text, ImageView imgBelow) {

        EditText newEditText = new EditText(this);

        newEditText.setId(newID);
        newEditText.setHint(hint);
        newEditText.setInputType(InputType.TYPE_CLASS_TEXT);

        RelativeLayout.LayoutParams textParams = (RelativeLayout.LayoutParams) text.getLayoutParams();
        textParams.addRule(RelativeLayout.BELOW, newEditText.getId());
        text.setLayoutParams(textParams);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        params.addRule(RelativeLayout.ALIGN_RIGHT, editTextID);
        params.addRule(RelativeLayout.LEFT_OF, imgBelow.getId());
        params.addRule(RelativeLayout.BELOW, editTextID);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        newEditText.setLayoutParams(params);

        RelativeLayout relative = (RelativeLayout) findViewById(R.id.relative);
        relative.addView(newEditText, params);

        return newEditText;
    }

    private EditText editText(int newID, int editTextID, String hint) {

        EditText newEditText = new EditText(this);

        newEditText.setId(newID);
        newEditText.setHint(hint);
        newEditText.setInputType(InputType.TYPE_CLASS_TEXT);

        Button btnReg = (Button) findViewById(R.id.button8);
        RelativeLayout.LayoutParams btnParams = (RelativeLayout.LayoutParams) btnReg.getLayoutParams();
        btnParams.addRule(RelativeLayout.BELOW,newEditText.getId());
        btnReg.setLayoutParams(btnParams);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        params.addRule(RelativeLayout.ALIGN_RIGHT, editTextID);
        params.addRule(RelativeLayout.BELOW, editTextID);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        newEditText.setLayoutParams(params);

        RelativeLayout relative = (RelativeLayout) findViewById(R.id.relative);
        relative.addView(newEditText, params);

        return newEditText;
    }

    private int getIDfromRules( ViewGroup.LayoutParams params, int rule) {
        int[] editRules = ((RelativeLayout.LayoutParams) params).getRules();
        Log.i("asd", "RULE" + editRules[rule]);
        return editRules[rule];
    }

    private void setNewEditView(View v) {
        EditText editTextRightOf;

        int editID = getIDfromRules(v.getLayoutParams(), RelativeLayout.RIGHT_OF);

        editTextRightOf = (EditText) findViewById(editID);
        String hint = String.valueOf(editTextRightOf.getHint());

        if (editTextMap.hasKey(hint)) {
            editTextRightOf = editTextMap.getLast(hint);
        }

        EditText newEditText;

        if(v.getId() != R.id.imageView10) {

            int imgID = getIDfromRules(editTextRightOf.getLayoutParams(), RelativeLayout.LEFT_OF);
            ImageView imgBelow = (ImageView) findViewById(imgID);

            int editBelowID = getIDfromRules(imgBelow.getLayoutParams(), RelativeLayout.RIGHT_OF);
            EditText editTextBelow = (EditText) findViewById(editBelowID);

            int headerBelowID = getIDfromRules(editTextBelow.getLayoutParams(), RelativeLayout.BELOW);
            TextView text = (TextView) findViewById(headerBelowID);

            newEditText = editText(ViewIdGenerator.generateViewId(), editTextRightOf.getId(), hint, text, imgBelow);
        } else {
            newEditText = editText(ViewIdGenerator.generateViewId(), editTextRightOf.getId(), hint);
        }

        editTextMap.put(hint, newEditText);

        ImageView img = (ImageView) findViewById(v.getId());
        RelativeLayout.LayoutParams imgParams = (RelativeLayout.LayoutParams)img.getLayoutParams();
        imgParams.addRule(RelativeLayout.ALIGN_TOP, newEditText.getId());
        imgParams.addRule(RelativeLayout.RIGHT_OF, newEditText.getId());
        imgParams.addRule(RelativeLayout.ALIGN_BOTTOM, newEditText.getId());
        img.setLayoutParams(imgParams);
    }

    public void more(final View v) {

        v.startAnimation(animationAplha);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setNewEditView(v);
            }
        },200);
    }
}
