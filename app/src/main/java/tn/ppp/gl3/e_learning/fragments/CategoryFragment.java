package tn.ppp.gl3.e_learning.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tn.ppp.gl3.e_learning.adapters.SimpleRecyclerViewAdapter;
import tn.ppp.gl3.e_learning.models.Category;
import tn.ppp.gl3.e_learning.R;
import tn.ppp.gl3.e_learning.services.DialogFactory;
import tn.ppp.gl3.e_learning.services.RetrofitServices;
import tn.ppp.gl3.e_learning.utils.Utils;

/**
 * Created by S4M37 on 02/06/2016.
 */
public class CategoryFragment extends Fragment {
    private View rootView;
    private RecyclerView listCategries;
    private RecyclerView.LayoutManager layoutManager;
    private RetrofitServices retrofitServices;
    private ProgressDialog progressDialog;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_simple_list, container, false);
        retrofitServices = Utils.getRetrofitServices();
        inisiliazeView();
        getCategories();
        return rootView;
    }

    private void getCategories() {
        progressDialog.show();
        Call<ResponseBody> call = retrofitServices.getCategoriesWithTrainings();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("response");
                        Gson gson = new Gson();
                        Category[] categories = new Category[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            categories[i] = gson.fromJson(String.valueOf(jsonArray.get(i)), Category.class);
                        }
                        SimpleRecyclerViewAdapter categoryRecyclerViewAdapter = new SimpleRecyclerViewAdapter(getContext(), categories);
                        listCategries.setAdapter(categoryRecyclerViewAdapter);
                    } catch (JSONException | IOException | NullPointerException e) {
                        e.printStackTrace();
                        DialogFactory.showAlertDialog(getContext(), getString(R.string.server_error), getString(R.string.title_server_error));
                    }
                } else {
                    DialogFactory.showAlertDialog(getContext(), getString(R.string.server_error), getString(R.string.title_server_error));
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private void inisiliazeView() {
        listCategries = (RecyclerView) rootView.findViewById(R.id.list_categories);
        layoutManager = new LinearLayoutManager(getContext());
        listCategries.setLayoutManager(layoutManager);
        listCategries.setHasFixedSize(true);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.loading));
    }
}
