package com.ad.zakatrizki.fragment;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ad.zakatrizki.R;
import com.ad.zakatrizki.Zakat;
import com.ad.zakatrizki.activity.ActionDonasiBaruActivity;
import com.ad.zakatrizki.adapter.PhotoAdapter;
import com.ad.zakatrizki.model.CalonMustahiq;
import com.ad.zakatrizki.model.Mustahiq;
import com.ad.zakatrizki.model.Photo;
import com.ad.zakatrizki.utils.ApiHelper;
import com.ad.zakatrizki.utils.CustomVolley;
import com.ad.zakatrizki.utils.TextUtils;
import com.ad.zakatrizki.widget.RobotoBoldTextView;
import com.ad.zakatrizki.widget.RobotoLightTextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import agency.tango.android.avatarview.loader.PicassoLoader;
import agency.tango.android.avatarview.views.AvatarView;
import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DonasiDetailFragment extends Fragment implements AddRatingFragment.RatingListener, CustomVolley.OnCallbackResponse, OnMapReadyCallback, PhotoAdapter.OnPhotoItemClickListener {

    private static final String TAG_DETAIL = "TAG_DETAIL";
    private static final String TAG_REKOMENDASI = "TAG_REKOMENDASI";
    @BindBool(R.bool.is_tablet)
    boolean isTablet;
    // Toolbar
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_text_holder)
    View toolbarTextHolder;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar_subtitle)
    TextView toolbarSubtitle;
    // Main views
    @BindView(R.id.progress_circle)
    View progressCircle;
    @BindView(R.id.error_message)
    View errorMessage;
    @BindView(R.id.text_error)
    RobotoLightTextView textError;
    @BindView(R.id.try_again)
    RobotoBoldTextView tryAgain;
    @BindView(R.id.movie_detail_holder)
    NestedScrollView movieHolder;

    @BindView(R.id.fab_navigasi)
    FloatingActionButton fabNavigasi;
    @BindView(R.id.fab_rating)
    FloatingActionButton fabRating;
    @BindView(R.id.fab_action)
    FloatingActionButton fabAction;
    @BindView(R.id.fab_rekomendasi)
    FloatingActionButton fabRekomendasi;


    // Basic info
    @BindView(R.id.foto_profil)
    AvatarView fotoProfil;
    @BindView(R.id.nama_calon_mustahiq)
    RobotoBoldTextView namaCalonMustahiq;
    @BindView(R.id.alamat_calon_mustahiq)
    RobotoLightTextView alamatCalonMustahiq;
    @BindView(R.id.no_identitas_calon_mustahiq)
    RobotoLightTextView noIdentitasCalonMustahiq;
    @BindView(R.id.no_telp_calon_mustahiq)
    RobotoLightTextView noTelpCalonMustahiq;
    @BindView(R.id.jumlah_anak_calon_mustahiq)
    RobotoLightTextView jumlahAnakCalonMustahiq;
    @BindView(R.id.status_pernikahan_calon_mustahiq)
    RobotoLightTextView statusPernikahanCalonMustahiq;
    @BindView(R.id.status_tempat_tinggal_calon_mustahiq)
    RobotoLightTextView statusTempatTinggalCalonMustahiq;
    @BindView(R.id.status_pekerjaan_calon_mustahiq)
    RobotoLightTextView statusPekerjaanCalonMustahiq;
    @BindView(R.id.nama_perekomendasi_calon_mustahiq)
    RobotoLightTextView namaPerekomendasiCalonMustahiq;
    @BindView(R.id.nama_validasi_amil_zakat)
    RobotoLightTextView namaAmilZakat;
    @BindView(R.id.nama_type_validasi_mustahiq)
    RobotoLightTextView namaTypeValidasiMstahiq;
    @BindView(R.id.status_mustahiq)
    RobotoLightTextView statusMustahiq;
    @BindView(R.id.waktu_terakhir_donasi)
    RobotoLightTextView waktuTerakhirDonasi;

    @BindView(R.id.layout_rating)
    LinearLayout layoutRating;

    @BindView(R.id.rating)
    AppCompatRatingBar rating;



    @BindView(R.id.rating_amil_zakat)
    AppCompatRatingBar ratingAmilZakat;

    private Unbinder unbinder;
    private String id_msthq;
    private Mustahiq mustahiq;
    private PicassoLoader imageLoader;
    private CustomVolley customVolley;
    private RequestQueue queue;
    private View mapView;
    private GoogleMap mMap;
    private ProgressDialog dialogProgress;

    @OnClick(R.id.fab_rating)
    void AddRating() {
        FragmentManager fragmentManager = getChildFragmentManager();
        AddRatingFragment add = new AddRatingFragment();
        add.setTargetFragment(this, 0);
        add.setData(mustahiq);
        add.show(fragmentManager, "Add Rating");
    }

    @OnClick(R.id.fab_navigasi)
    void NavigasiMuzaki() {
        {
            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f(%s)", Double.valueOf(mustahiq.latitude_calon_mustahiq), Double.valueOf(mustahiq.longitude_calon_mustahiq), mustahiq.nama_calon_mustahiq) + "'s Location";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            try {
                startActivity(intent);


            } catch (ActivityNotFoundException ex) {
                try {
                    Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(unrestrictedIntent);


                } catch (ActivityNotFoundException innerEx) {
                    Toast.makeText(getActivity(), "Please install a maps application", Toast.LENGTH_LONG).show();
                }
            }

        }
    }


    @BindView(R.id.recyclerview_foto)
    RecyclerView recyclerView;

    private ArrayList<Photo> dataPhotos = new ArrayList<>();
    private PhotoAdapter adapterPhoto;
    private LinearLayoutManager mLayoutManager;

    @OnClick(R.id.fab_action)
    void DonasiMustahiq() {
        Intent i = new Intent(getActivity(), ActionDonasiBaruActivity.class);
        i.putExtra(Zakat.calon_mustahiq, mustahiq);
        getActivity().startActivityForResult(i, 1);
    }

    // Fragment lifecycle
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mustahiq_detail, container, false);
        unbinder = ButterKnife.bind(this, v);
        customVolley = new CustomVolley(getActivity());
        customVolley.setOnCallbackResponse(this);
        imageLoader = new PicassoLoader();

        // Setup toolbar
        toolbar.setTitle(R.string.loading);
        if (!isTablet) {
            toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.action_home));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });
        }

        //setup fab
        fabNavigasi.setImageDrawable(
                new IconDrawable(getActivity(), MaterialIcons.md_navigation)
                        .colorRes(R.color.white)
                        .actionBarSize());
        fabRating.setImageDrawable(
                new IconDrawable(getActivity(), MaterialIcons.md_star)
                        .colorRes(R.color.white)
                        .actionBarSize());
        fabRekomendasi.setImageDrawable(
                new IconDrawable(getActivity(), MaterialIcons.md_check)
                        .colorRes(R.color.white)
                        .actionBarSize());
        fabAction.setImageDrawable(
                new IconDrawable(getActivity(), MaterialIcons.md_attach_money)
                        .colorRes(R.color.white)
                        .actionBarSize());


        //inisial adapterMustahiq
        adapterPhoto = new PhotoAdapter(getActivity(), dataPhotos);
        adapterPhoto.setOnPhotoItemClickListener(this);

        //recyclerView
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        // set layout manager
        recyclerView.setLayoutManager(mLayoutManager);

        // set adapterPhoto
        recyclerView.setAdapter(adapterPhoto);

        // Download calon_mustahiq details if new instance, else restore from saved instance
        if (savedInstanceState == null || !(savedInstanceState.containsKey(Zakat.MUSTAHIQ_ID)
                && savedInstanceState.containsKey(Zakat.CALON_MUSTAHIQ_OBJECT))) {
            id_msthq = getArguments().getString(Zakat.MUSTAHIQ_ID);
            if (TextUtils.isNullOrEmpty(id_msthq)) {
                progressCircle.setVisibility(View.GONE);
                toolbarTextHolder.setVisibility(View.GONE);
                toolbar.setTitle("");
            } else {
                downloadLokasiDetails(id_msthq);
            }
        } else {
            id_msthq = savedInstanceState.getString(Zakat.MUSTAHIQ_ID);
            mustahiq = savedInstanceState.getParcelable(Zakat.CALON_MUSTAHIQ_OBJECT);
            onDownloadSuccessful();
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Send screen id_user_kru to analytics
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mustahiq != null && id_msthq != null) {
            outState.putString(Zakat.MUSTAHIQ_ID, id_msthq);
            outState.putParcelable(Zakat.CALON_MUSTAHIQ_OBJECT, mustahiq);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (queue != null) {
            queue.cancelAll(TAG_DETAIL);
            queue.cancelAll(TAG_REKOMENDASI);
        }

    }


    // JSON parsing and display
    private void downloadLokasiDetails(String id) {
        String urlToDownload = ApiHelper.getMustahiqDetailLink(getActivity(), id);
        queue = customVolley.Rest(Request.Method.GET, urlToDownload, null, TAG_DETAIL);
    }

    private void onDownloadSuccessful() {

        // Toggle visibility
        progressCircle.setVisibility(View.GONE);
        errorMessage.setVisibility(View.GONE);
        movieHolder.setVisibility(View.VISIBLE);
        fabNavigasi.setVisibility(View.VISIBLE);
        fabRating.setVisibility(View.VISIBLE);
        fabAction.setVisibility(View.VISIBLE);
        fabRekomendasi.setVisibility(View.GONE);

        toolbar.setTitle(mustahiq.nama_calon_mustahiq);
        toolbarTextHolder.setVisibility(View.GONE);

        imageLoader.loadImage(fotoProfil, mustahiq.nama_calon_mustahiq, mustahiq.nama_calon_mustahiq);
        namaCalonMustahiq.setText("Nama : " + mustahiq.nama_calon_mustahiq);
        alamatCalonMustahiq.setText("Alamat : " + (TextUtils.isNullOrEmpty(mustahiq.alamat_calon_mustahiq) ? "-" : mustahiq.alamat_calon_mustahiq));
        noIdentitasCalonMustahiq.setText("No Identitas : " + (TextUtils.isNullOrEmpty(mustahiq.no_identitas_calon_mustahiq) ? "-" : mustahiq.no_identitas_calon_mustahiq));
        noTelpCalonMustahiq.setText("No Telp : " + (TextUtils.isNullOrEmpty(mustahiq.no_telp_calon_mustahiq) ? "-" : mustahiq.no_telp_calon_mustahiq));
        jumlahAnakCalonMustahiq.setText("Jumlah Anak : " + (TextUtils.isNullOrEmpty(mustahiq.jumlah_anak_calon_mustahiq) ? "-" : mustahiq.jumlah_anak_calon_mustahiq));
        statusPernikahanCalonMustahiq.setText("Status Pernikahan : " + (TextUtils.isNullOrEmpty(mustahiq.status_pernikahan_calon_mustahiq) ? "-" : mustahiq.status_pernikahan_calon_mustahiq));

        statusTempatTinggalCalonMustahiq.setText("Status Tempat Tinggal : " + (TextUtils.isNullOrEmpty(mustahiq.status_tempat_tinggal_calon_mustahiq) ? "-" : mustahiq.status_tempat_tinggal_calon_mustahiq));
        statusPekerjaanCalonMustahiq.setText("Status Pekerjaan : " + (TextUtils.isNullOrEmpty(mustahiq.status_pekerjaan_calon_mustahiq) ? "-" : mustahiq.status_pekerjaan_calon_mustahiq));

        namaPerekomendasiCalonMustahiq.setText("Nama Perekomendasi : " + (TextUtils.isNullOrEmpty(mustahiq.nama_perekomendasi_calon_mustahiq) ? "-" : mustahiq.nama_perekomendasi_calon_mustahiq));

        statusMustahiq.setText(Html.fromHtml("Status Aktif : " + (mustahiq.status_mustahiq.equalsIgnoreCase("aktif") ? "<font color='#002800'>Aktif</font>" : "<font color='red'>Tidak Aktif</font>")));
        namaAmilZakat.setText("Validasi Amil Zakat Zakat : " + mustahiq.nama_validasi_amil_zakat);
        namaTypeValidasiMstahiq.setText("Type Validasi : " + mustahiq.nama_type_validasi_mustahiq);

        waktuTerakhirDonasi.setText("Waktu Terakhir Menerima Donasi : " + (TextUtils.isNullOrEmpty(mustahiq.waktu_terakhir_donasi) ? "-" : mustahiq.waktu_terakhir_donasi));

        layoutRating.setVisibility(View.VISIBLE);
        float rt = 0;
        try {

            rt = Float.parseFloat(mustahiq.jumlah_rating);
        } catch (Exception ignored) {
        }
        rating.setRating(rt);

        float rtam = 0;
        try {

            rtam = Float.parseFloat(mustahiq.jumlah_rating_amil_zakat);
        } catch (Exception ignored) {
        }
        ratingAmilZakat.setRating(rtam);

        dataPhotos.add(new Photo(mustahiq.photo_1, mustahiq.caption_photo_1));
        dataPhotos.add(new Photo(mustahiq.photo_2, mustahiq.caption_photo_2));
        dataPhotos.add(new Photo(mustahiq.photo_3, mustahiq.caption_photo_3));

        adapterPhoto.notifyDataSetChanged();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(DonasiDetailFragment.this);

    }

    private void onDownloadFailed() {
        errorMessage.setVisibility(View.VISIBLE);
        progressCircle.setVisibility(View.GONE);
        movieHolder.setVisibility(View.GONE);
        fabRating.setVisibility(View.GONE);
        toolbarTextHolder.setVisibility(View.GONE);
        toolbar.setTitle("");
    }

    private void onNotAvailable(String message) {
        errorMessage.setVisibility(View.VISIBLE);
        textError.setText(message);
        tryAgain.setVisibility(View.GONE);
        progressCircle.setVisibility(View.GONE);
        movieHolder.setVisibility(View.GONE);
        fabNavigasi.setVisibility(View.GONE);
        fabRating.setVisibility(View.GONE);
        fabRekomendasi.setVisibility(View.GONE);
        fabAction.setVisibility(View.GONE);
        toolbarTextHolder.setVisibility(View.GONE);
        toolbar.setTitle("");
    }

    @OnClick(R.id.try_again)
    public void onTryAgainClicked() {
        errorMessage.setVisibility(View.GONE);
        progressCircle.setVisibility(View.VISIBLE);
        downloadLokasiDetails(id_msthq);
    }


    @Override
    public void onVolleyStart(String TAG) {
        if (TAG.equalsIgnoreCase(TAG_REKOMENDASI)) {
            progress(true);
        }
    }

    @Override
    public void onVolleyEnd(String TAG) {
        if (TAG.equalsIgnoreCase(TAG_REKOMENDASI)) {
            progress(false);
        }
    }

    @Override
    public void onVolleySuccessResponse(String TAG, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String isSuccess = jsonObject.getString(Zakat.isSuccess);
            String message = jsonObject.getString(Zakat.message);

            if (Boolean.valueOf(isSuccess)) {
                if (!TAG.equals(TAG_REKOMENDASI)) {

                    JSONObject obj = new JSONObject(jsonObject.getString(Zakat.mustahiq));
                    String id_mustahiq = obj.getString(Zakat.id_mustahiq);
                    String id_calon_mustahiq = obj.getString(Zakat.id_calon_mustahiq);
                    String nama_calon_mustahiq = obj.getString(Zakat.nama_calon_mustahiq);
                    String alamat_calon_mustahiq = obj.getString(Zakat.alamat_calon_mustahiq);
                    String latitude_calon_mustahiq = obj.getString(Zakat.latitude_calon_mustahiq);
                    String longitude_calon_mustahiq = obj.getString(Zakat.longitude_calon_mustahiq);
                    String no_identitas_calon_mustahiq = obj.getString(Zakat.no_identitas_calon_mustahiq);
                    String no_telp_calon_mustahiq = obj.getString(Zakat.no_telp_calon_mustahiq);
                    String jumlah_anak_calon_mustahiq = obj.getString(Zakat.jumlah_anak_calon_mustahiq);
                    String status_pernikahan_calon_mustahiq = obj.getString(Zakat.status_pernikahan_calon_mustahiq);

                    String status_tempat_tinggal_calon_mustahiq = obj.getString(Zakat.status_tempat_tinggal_calon_mustahiq);
                    String status_pekerjaan_calon_mustahiq = obj.getString(Zakat.status_pekerjaan_calon_mustahiq);
                    String nama_perekomendasi_calon_mustahiq = obj.getString(Zakat.nama_perekomendasi_calon_mustahiq);
                    String alasan_perekomendasi_calon_mustahiq = obj.getString(Zakat.alasan_perekomendasi_calon_mustahiq);

                    String photo_1 = obj
                            .getString(Zakat.photo_1);
                    String photo_2 = obj
                            .getString(Zakat.photo_2);
                    String photo_3 = obj
                            .getString(Zakat.photo_3);
                    String caption_photo_1 = obj
                            .getString(Zakat.caption_photo_1);
                    String caption_photo_2 = obj
                            .getString(Zakat.caption_photo_2);
                    String caption_photo_3 = obj
                            .getString(Zakat.caption_photo_3);
                    String status_mustahiq = obj.getString(Zakat.status_mustahiq);
                    String jumlah_rating = obj.getString(Zakat.jumlah_rating);
                    String jumlah_rating_amil_zakat = obj.getString(Zakat.jumlah_rating_amil_zakat);
                    String id_nama_validasi_amil_zakat = obj.getString(Zakat.id_nama_validasi_amil_zakat);
                    String nama_validasi_amil_zakat = obj.getString(Zakat.nama_validasi_amil_zakat);
                    String nama_type_validasi_mustahiq = obj.getString(Zakat.nama_type_validasi_mustahiq);
                    String waktu_terakhir_donasi = obj.getString(Zakat.waktu_terakhir_donasi);

                    mustahiq = new Mustahiq(id_mustahiq, id_calon_mustahiq, nama_calon_mustahiq, alamat_calon_mustahiq,
                            latitude_calon_mustahiq,
                            longitude_calon_mustahiq,
                            no_identitas_calon_mustahiq,
                            no_telp_calon_mustahiq,
                            jumlah_anak_calon_mustahiq,
                            status_pernikahan_calon_mustahiq,
                            status_tempat_tinggal_calon_mustahiq,
                            status_pekerjaan_calon_mustahiq,
                            nama_perekomendasi_calon_mustahiq,
                            alasan_perekomendasi_calon_mustahiq,
                            photo_1,
                            photo_2,
                            photo_3,
                            caption_photo_1,
                            caption_photo_2,
                            caption_photo_3,
                            status_mustahiq, jumlah_rating,jumlah_rating_amil_zakat,
                            id_nama_validasi_amil_zakat,nama_validasi_amil_zakat,nama_type_validasi_mustahiq, waktu_terakhir_donasi);

                    if (Boolean.parseBoolean(isSuccess))
                        onDownloadSuccessful();
                    else
                        onNotAvailable(message);
                } else {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                    downloadLokasiDetails(id_msthq);
                }
            }

        } catch (Exception ex) {
            // Parsing error
            if (!TAG.equals(TAG_REKOMENDASI)) {
                onDownloadFailed();
            } else {
                Toast.makeText(getActivity(), "Ada kesalahan parsing", Toast.LENGTH_SHORT).show();
            }
            Log.d("Parse Error", ex.getMessage(), ex);
        }

    }

    private void progress(boolean status) {
        if (status) {
            dialogProgress = ProgressDialog.show(getActivity(), "Submit",
                    "Harap menunggu...", true);
        } else {

            if (dialogProgress.isShowing())
                dialogProgress.dismiss();
        }
    }


    @Override
    public void onVolleyErrorResponse(String TAG, String response) {

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);

        LatLng Posisi = new LatLng(Double.parseDouble(mustahiq.latitude_calon_mustahiq.equalsIgnoreCase("null") ? "0.0" : mustahiq.latitude_calon_mustahiq), Double.parseDouble(mustahiq.longitude_calon_mustahiq.equalsIgnoreCase("null") ? "0.0" : mustahiq.longitude_calon_mustahiq));
        MarkerOptions marker = new MarkerOptions()
                .position(Posisi)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mustahiq));

        Marker m = mMap.addMarker(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Posisi, 14));


    }


    @Override
    public void onFinishRating(Mustahiq mustahiq) {
        this.mustahiq = mustahiq;
        onDownloadSuccessful();
    }

    @Override
    public void onFinishRating(CalonMustahiq calonMustahiq) {

    }

    @Override
    public void onActionClick(View v, int position) {

    }

    @Override
    public void onRootClick(View v, int position) {
        FragmentManager ft = getChildFragmentManager();
        DialogViewSinggleImageFragment newFragment = DialogViewSinggleImageFragment.newInstance(ApiHelper.getBaseUrl(getActivity())+adapterPhoto.data.get(position).getPhoto(), adapterPhoto.data.get(position).getCaption_photo());
        newFragment.setTargetFragment(this, 0);
        newFragment.show(ft, "slideshow");
    }
}