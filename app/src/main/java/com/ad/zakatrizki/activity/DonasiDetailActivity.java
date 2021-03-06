package com.ad.zakatrizki.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.ad.zakatrizki.R;
import com.ad.zakatrizki.Zakat;
import com.ad.zakatrizki.fragment.DialogDetailDonasiFragment;
import com.ad.zakatrizki.fragment.DonasiDetailFragment;
import com.ad.zakatrizki.model.LaporanDonasi;
import com.ad.zakatrizki.utils.Prefs;

public class DonasiDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_detail);
        String mustahiqId = getIntent().getStringExtra(Zakat.MUSTAHIQ_ID);
        loadDonasiDetailsOf(mustahiqId);
    }

    private void loadDonasiDetailsOf(String mustahiqId) {
        DonasiDetailFragment fragment = new DonasiDetailFragment();
        Bundle args = new Bundle();
        args.putString(Zakat.MUSTAHIQ_ID, mustahiqId);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_detail_container, fragment).commit();
    }

    @SuppressLint("CommitPrefEdits")
    private void loadDonasiOfType(int viewType) {
        Prefs.putLastSelected(this, Zakat.VIEW_TYPE_DONASI);
        startActivity(new Intent(this, DrawerActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                LaporanDonasi laporanDonasi = data.getParcelableExtra(Zakat.LAPORAN_DONASI_OBJECT);
                if (laporanDonasi != null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    DialogDetailDonasiFragment dialogDetailDonasiFragment = new DialogDetailDonasiFragment();
                    dialogDetailDonasiFragment.setCancelable(false);
                    dialogDetailDonasiFragment.setData(laporanDonasi);
                    ft.add(dialogDetailDonasiFragment, null);
                    ft.commitAllowingStateLoss();
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

}
