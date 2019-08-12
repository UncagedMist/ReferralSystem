package kk.techbytecare.referralsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

public class MainActivity extends AppCompatActivity {

    Button btnRefer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRefer = findViewById(R.id.btnShare);


        btnRefer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateLink();
            }
        });
    }


    private void generateLink() {

        Log.e("main", "generateLink" );

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://techbytecarebykk.blogspot.com/"))
                .setDynamicLinkDomain("referralsystem.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();

        Log.e("main", "Long Referral Link : "+dynamicLinkUri);


        //Manually generate Links
        String shareLink = "http://referralsystem.page.link/?"+
                "link=https://techbytecarebykk.blogspot.com/"+
                "&apn="+getPackageName()+
                "&st="+"My Refer Link"+
                "&sd="+"Reward coins 20"+
                "&si="+"image url";


        //shorten the url
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(dynamicLinkUri)    //this for firefase
                //.setLongLink(Uri.parse(shareLink))  //this for manual link
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();

                            Log.e("main", "Shorted Link: "+shortLink );
                            Log.e("main", "FlowCharted Link: "+flowchartLink );

                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT,shortLink.toString());
                            intent.setType("text/plain");
                            startActivity(Intent.createChooser(intent,"Share this app with"));

                        } else {
                            Log.e("main", "Error :  "+task.getException() );
                        }
                    }
                });
    }
}
