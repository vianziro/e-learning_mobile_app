package tn.ppp.gl3.e_learning.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tn.ppp.gl3.e_learning.activities.MainActivity;
import tn.ppp.gl3.e_learning.R;
import tn.ppp.gl3.e_learning.services.DialogFactory;
import tn.ppp.gl3.e_learning.services.RetrofitServices;
import tn.ppp.gl3.e_learning.utils.Utils;

/**
 * Created by S4M37 on 19/04/2016.
 */
public class InscriptionFragment extends Fragment {
    private View rootView;
    private EditText inputName;
    private EditText inputEmail;
    private EditText inputPassword;
    private CheckBox checkBox;
    private Button submit;
    private RetrofitServices retrofitServices = Utils.getRetrofitServices();
    private EditText inputRegion;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_inscription, container, false);
        inisializeView();
        return rootView;
    }

    private void inisializeView() {
        inputEmail = (EditText) rootView.findViewById(R.id.email);
        inputPassword = (EditText) rootView.findViewById(R.id.password);
        inputName = (EditText) rootView.findViewById(R.id.name);
        inputRegion = (EditText) rootView.findViewById(R.id.region);
        checkBox = (CheckBox) rootView.findViewById(R.id.checkbox);
        submit = (Button) rootView.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = inputName.getText().toString();
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                String region = inputRegion.getText().toString();
                if (name.equals("") || email.equals("") || password.equals("")) {
                    DialogFactory.showAlertDialog(getContext(), getString(R.string.empty_field), "Oups");
                } else {
                    final ProgressDialog dialog = new ProgressDialog(getContext());
                    dialog.setMessage(getString(R.string.loading));
                    dialog.show();
                    Utils.hideKeyboard((AppCompatActivity) getActivity());
                    Call<ResponseBody> call = retrofitServices.signup(name, email, password, region);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            dialog.dismiss();
                            Log.d("code", response.code() + "");
                            if (response.code() == 405) {
                                DialogFactory.showAlertDialog(getContext(), getString(R.string.user_exists), "Oups");
                            } else {
                                DialogFactory.showAlertDialog(getContext(), getString(R.string.email_confirmation), "Succé");
                                ((MainActivity) getActivity()).showFragment(new MainFragment());
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    inputPassword.setTransformationMethod(null);
                } else {
                    inputPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });
    }
}
