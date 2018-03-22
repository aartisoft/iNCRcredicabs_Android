package com.ncr.interns.codecatchers.incredicabs;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;
import java.util.StringTokenizer;

import mehdi.sakout.aboutpage.Element;


public class AboutPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  simulateDayNight(/* DAY */ 0);
        Element adsElement = new Element();
        adsElement.setTitle("iNCRediCabs");
        String description = getString(R.string.app_description);
        // TODO: 3/23/2018 Add NCR ICON
        View aboutPage = new mehdi.sakout.aboutpage.AboutPage(this)
                .isRTL(false).setImage(R.drawable.ic_contact_transport_sidemenu).
                        setDescription(description)

                .addItem(new Element().setTitle("Developed by iNCRedible Interns 2018 batch."))
                .addItem(new Element().setTitle("Version 1.0"))
                .addItem(adsElement)
                .addGroup("Connect With us").addEmail("Gaurav.sati@ncr.com")
                .addGitHub("Gauti_neo")
                .addWebsite("bit.ly/iNCRedicabs")
                .addItem(getCopyRightElement())
                .create();

        setContentView(aboutPage);
    }

    private Element getCopyRightElement() {
        Element copyRightsElement = new Element();
        final String copyrights = getString(R.string.login_copy);
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.about_icon_link);
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutPage.this, copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }

    void simulateDayNight(int currentSetting) {
        final int DAY = 0;
        final int NIGHT = 1;
        final int FOLLOW_SYSTEM = 3;

        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        if (currentSetting == DAY && currentNightMode != Configuration.UI_MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        } else if (currentSetting == NIGHT && currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        } else if (currentSetting == FOLLOW_SYSTEM) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
