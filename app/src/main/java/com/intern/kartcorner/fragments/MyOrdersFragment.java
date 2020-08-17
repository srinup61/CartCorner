package com.intern.kartcorner.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.intern.kartcorner.R;
import com.intern.kartcorner.adapters.PlaceOrderAdapter;
import com.intern.kartcorner.entities.PlaceOrderCommonClass;
import com.intern.kartcorner.helper.PrefManager;
import com.intern.kartcorner.helper.ProgressDailog;
import com.intern.kartcorner.helper.ShowToast;
import com.intern.kartcorner.ui.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

import static com.intern.kartcorner.app.Constants.BASE_URL;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyOrdersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyOrdersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyOrdersFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.ordersRecycler)
    RecyclerView ordersRecycler;
    @BindView(R.id.ordersrecycler_layout)
    LinearLayout ordersRecyclerlayout;
    @BindView(R.id.ordersStart)
    LinearLayout ordersStartlayout;
    private ArrayList<PlaceOrderCommonClass> commonClasses = new ArrayList<>();
    private PrefManager prefManager;
    private SpotsDialog progressDailog;
    private ShowToast showToast;

    public MyOrdersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyOrdersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyOrdersFragment newInstance(String param1, String param2) {
        MyOrdersFragment fragment = new MyOrdersFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);
        ButterKnife.bind(this,view);

        prefManager = new PrefManager(getContext());
        progressDailog = new SpotsDialog(getContext());
        showToast = new ShowToast(getContext());

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        ordersRecycler.setLayoutManager(llm);
        ordersRecycler.setHasFixedSize(true);
        getorders(prefManager.getUserId());
        return view;
    }

    private void getorders(String userId) {
        progressDailog.show();
        Ion.with(getContext())
                .load("POST",BASE_URL+"getorders")
                .setMultipartParameter("userid",userId)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if(e!=null){
                            progressDailog.dismiss();
                            showToast.showErrorToast("Something went wrong");
                        }else {
                            try {
                                Object json = new JSONTokener(result).nextValue();
                                if (json instanceof JSONObject){
                                    JSONObject jsonObject = new JSONObject(result);
                                    JSONArray jsonArray = jsonObject.getJSONArray("orders");
                                    for(int i=0;i<jsonArray.length();i++){
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                        PlaceOrderCommonClass orderCommonClass = new PlaceOrderCommonClass();
                                        orderCommonClass.setOrderid(jsonObject1.getString("orderid"));
                                        orderCommonClass.setDeliverydate(jsonObject1.getString("deliverydate"));
                                        orderCommonClass.setDeliverytime(jsonObject1.getString("deliverytime"));
                                        orderCommonClass.setTotalamount(jsonObject1.getString("totalamount"));
                                        orderCommonClass.setDeliverycharges(jsonObject1.getString("deliverycharges"));
                                        orderCommonClass.setDiscountamount(jsonObject1.getString("discountamount"));
                                        orderCommonClass.setOrderstatus(jsonObject1.getString("orderstatus"));
                                        orderCommonClass.setProducts(jsonObject1.getString("products"));
                                        commonClasses.add(orderCommonClass);
                                    }
                                    Collections.reverse(commonClasses);
                                    PlaceOrderAdapter placeOrderAdapter = new PlaceOrderAdapter(getActivity(),commonClasses);
                                    ordersRecycler.setAdapter(placeOrderAdapter);
                                    placeOrderAdapter.notifyDataSetChanged();
                                    progressDailog.dismiss();
                                }
                                else if (json instanceof JSONArray){
                                    JSONArray jsonArray = new JSONArray(result);
                                    if(jsonArray.length()==0){
                                        progressDailog.dismiss();
                                        shownoorderslayout();
                                        return;
                                    }
                                }
                            } catch (JSONException e1) {
                                progressDailog.dismiss();
                                e1.printStackTrace();
                            }
                        }
                    }
                });
    }
    @OnClick(R.id.startShoppingNow)
    void startshopping(){
        Intent intent = new Intent(getContext(),MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
    @OnClick(R.id.emptycart_image)
    void startshop(){
        startshopping();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private void shownoorderslayout() {
        View forgotLayout = getActivity().findViewById(R.id.ordersStart);
        forgotLayout.setAnimation(AnimationUtils.makeInChildBottomAnimation(getActivity()));
        forgotLayout.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> getActivity().findViewById(R.id.ordersrecycler_layout).setVisibility(View.GONE), 500);
    }
    private void recyclershowlayout(){
        View forgotLayout = getActivity().findViewById(R.id.ordersrecycler_layout);
        forgotLayout.setAnimation(AnimationUtils.makeInChildBottomAnimation(getActivity()));
        forgotLayout.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> getActivity().findViewById(R.id.ordersStart).setVisibility(View.GONE), 500);

    }
}
