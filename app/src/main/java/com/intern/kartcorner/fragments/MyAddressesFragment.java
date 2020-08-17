package com.intern.kartcorner.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.koushikdutta.ion.Ion;
import com.intern.kartcorner.R;
import com.intern.kartcorner.adapters.AddressesAdapter;
import com.intern.kartcorner.entities.MyAddressDAO;
import com.intern.kartcorner.helper.PrefManager;
import com.intern.kartcorner.helper.ProgressDailog;
import com.intern.kartcorner.helper.ShowToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.intern.kartcorner.app.Constants.BASE_URL;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyAddressesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyAddressesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyAddressesFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,View.OnClickListener, CompoundButton.OnCheckedChangeListener ,GoogleApiClient.OnConnectionFailedListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ProgressDailog progressDailog;
    private ShowToast showToast;
    private ArrayList<MyAddressDAO> myAddressDAOS = new ArrayList<>();
    private OnFragmentInteractionListener mListener;
    private RecyclerView addressrecyclerview;
    private LinearLayout addnewaddresslayout;
    private AppCompatButton addnewAddress;
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;
    @BindView(R.id.addressnicknameedit)
    EditText nickName;
    @BindView(R.id.addresspersonnameedit)
    EditText personname;
    @BindView(R.id.addresshousenoedit)
    EditText addresshouseno;
    @BindView(R.id.addressstreetedit)
    EditText addressstreet;
    @BindView(R.id.addressareaedit)
    EditText addressarea;
    @BindView(R.id.addressapartmentnameedit)
    EditText addressapartmentname;
    @BindView(R.id.addresslandmarkedit)
    EditText addresslandmark;
    @BindView(R.id.addresspincodeedit)
    EditText addresspincode;
    @BindView(R.id.addresscityedit)
    EditText addresscity;
    @BindView(R.id.default_type)
    CheckBox default_type;
    private int condition = 0;
    private String latlong;
    public PrefManager prefManager;
    public MyAddressesFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyAddressesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyAddressesFragment newInstance(String param1, String param2) {
        MyAddressesFragment fragment = new MyAddressesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_addresses, container, false);
        ButterKnife.bind(this,view);
        intiviews(view);
        return view;
    }

    private void intiviews(View view) {
        addressrecyclerview = view.findViewById(R.id.addressesRecycler);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        addressrecyclerview.setLayoutManager(llm);
        addressrecyclerview.setHasFixedSize(true);
        addnewaddresslayout = view.findViewById(R.id.addresseditlayout);
        progressDailog = new ProgressDailog(getContext());
        showToast = new ShowToast(getContext());
        prefManager = new PrefManager(getContext());
        addnewAddress = view.findViewById(R.id.addnew_address);
        getaddresses(prefManager.getUserId());
        default_type.setOnCheckedChangeListener(this);
        personname.setText(prefManager.getName());
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void getaddresses(String userId) {
        progressDailog.showDailog();
        Ion.with(getContext())
                .load("POST",BASE_URL+"getaddresses")
                .setMultipartParameter("userid",userId)
                .asString()
                .setCallback((e, result) -> {
                    if(e!=null){
                        progressDailog.dismissDailog();
                        showToast.showErrorToast("Something went wrong");
                    }else {
                        try {
                            myAddressDAOS.clear();
                            Object json = new JSONTokener(result).nextValue();
                            if (json instanceof JSONObject){
                                JSONObject jsonObject = new JSONObject(result);
                                JSONArray jsonArrray = jsonObject.getJSONArray("myaddresses");
                                for (int i=0;i<jsonArrray.length();i++){
                                    MyAddressDAO myAddressDAO = new MyAddressDAO();
                                    JSONObject jsonObject1 = jsonArrray.getJSONObject(i);
                                    myAddressDAO.setAddressid(jsonObject1.getString("addressid"));
                                    myAddressDAO.setAddress(jsonObject1.getString("address"));
                                    myAddressDAO.setAddressnickname(jsonObject1.getString("addressnickname"));
                                    myAddressDAO.setPersonname(jsonObject1.getString("personname"));
                                    myAddressDAO.setDefaulttype(jsonObject1.getString("defaulttype"));
                                    myAddressDAO.setLatlong(jsonObject1.getString("latlong"));
                                    myAddressDAOS.add(myAddressDAO);
                                }
                                Collections.reverse(myAddressDAOS);
                                AddressesAdapter addressesAdapter = new AddressesAdapter(getContext(),myAddressDAOS,MyAddressesFragment.this,1);
                                addressrecyclerview.setAdapter(addressesAdapter);
                                addressesAdapter.notifyDataSetChanged();
                                progressDailog.dismissDailog();
                            }
                            //you have an object
                            else if (json instanceof JSONArray){
                                JSONArray jsonArray = new JSONArray(result);
                                if(jsonArray.length()==0){
                                    progressDailog.dismissDailog();
                                    showaddaddresslayout();
                                    return;
                                }
                            }


                        } catch (JSONException e1) {
                            progressDailog.dismissDailog();
                            e1.printStackTrace();
                        }
                    }
                });
    }

    @OnClick(R.id.addnew_address)
    void addnewAddress(){
        showaddaddresslayout();
    }
    @OnClick(R.id.saveAddress)
    void saveAddress(){
        progressDailog.showDailog();
        String addressnickname = nickName.getText().toString();
        String personName = personname.getText().toString();
        String houseNo = addresshouseno.getText().toString();
        String streetName = addressstreet.getText().toString();
        String area = addressarea.getText().toString();
        String apartmentname = addressapartmentname.getText().toString();
        String landmark = addresslandmark.getText().toString();
        String pincode = addresspincode.getText().toString();
        String city = addresscity.getText().toString();
        if(addressnickname.isEmpty()||personName.isEmpty()||landmark.isEmpty()||pincode.isEmpty()||city.isEmpty()){
            showToast.showInfoToast("Should be fill Nick name,Person Name,Pincode,City, Landmark");
            progressDailog.dismissDailog();
            return;
        }
        StringBuilder addressstring = new StringBuilder();
        if(!houseNo.isEmpty()){
            addressstring.append(houseNo);
            addressstring.append(",");
        }
        if(!streetName.isEmpty()){
            addressstring.append(streetName);
            addressstring.append(",");
        }
        if(!apartmentname.isEmpty()){
            addressstring.append(apartmentname);
            addressstring.append(",");
        }
        addressstring.append(area);
        addressstring.append(",");
        addressstring.append(landmark);
        addressstring.append(",");
        addressstring.append(city);
        addressstring.append(",");
        addressstring.append(pincode);
        String address = addressstring.toString();
        addAddress(prefManager.getUserId(),addressnickname,personName,address,condition,latlong);
    }

    private void addAddress(String userId, String addressnickname, String personName, String address, int condition, String latlong) {
        Ion.with(getContext())
                .load("POST",BASE_URL+"addaddress")
                .setMultipartParameter("userid",userId)
                .setMultipartParameter("defaulttype",String.valueOf(condition))
                .setMultipartParameter("address",address)
                .setMultipartParameter("addressnickname",addressnickname)
                .setMultipartParameter("personname",personName)
                .setMultipartParameter("latlong","none")
                .asString()
                .setCallback((e, result) -> {
                    if(e!=null){
                        progressDailog.dismissDailog();
                        showToast.showErrorToast("Something went wrong");
                    }else {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String error = jsonObject.getString("error");
                            String msg  = jsonObject.getString("message");
                            if(error.equals("false")){
                                showToast.showSuccessToast(msg);
                                recyclershow();
                                getaddresses(prefManager.getUserId());
                                nickName.setText("");
                                addresscity.setText("");
                                addresspincode.setText("");
                                addressarea.setText("");
                                addresslandmark.setText("");

                            }else {
                                showToast.showWarningToast(msg);
                            }
                            progressDailog.dismissDailog();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            progressDailog.dismissDailog();
                        }
                    }
                });

    }

    @OnClick(R.id.cancelAddress)
    void cancelAddress(){
        recyclershow();
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addnew_address:
                showaddaddresslayout();
                break;
        }
    }

    private void showaddaddresslayout() {
        View forgotLayout = getActivity().findViewById(R.id.addresseditlayout);
        forgotLayout.setAnimation(AnimationUtils.makeInChildBottomAnimation(getActivity()));
        forgotLayout.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> getActivity().findViewById(R.id.addressesRecycler).setVisibility(View.GONE), 500);
    }
    private void recyclershow(){
        View forgotLayout = getActivity().findViewById(R.id.addressesRecycler);
        forgotLayout.setAnimation(AnimationUtils.makeInChildBottomAnimation(getActivity()));
        forgotLayout.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> getActivity().findViewById(R.id.addresseditlayout).setVisibility(View.GONE), 500);

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(b){
            condition = 1;
        }else {
            condition = 0;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

}
