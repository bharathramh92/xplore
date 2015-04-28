package com.example.bharathramh.xplore;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CodeVerification.CodeVerificationFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CodeVerification extends Fragment {

    private CodeVerificationFragmentInteractionListener mListener;

    Bundle bundle;

    String code, phoneNumber, countryCode;

    Toast failureToast;

    public CodeVerification() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        return inflater.inflate(R.layout.fragment_code_verification, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final EditText enteredCodeEditText = (EditText) getView().findViewById(R.id.codeVerEditText);

        bundle = getArguments();


        getView().findViewById(R.id.codeVerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    code = bundle.getString(constants.CODE);
                    phoneNumber = bundle.getString(constants.PHONENUMBER);
                    countryCode = bundle.getString(constants.COUNTRY_CODE);
                }catch (NullPointerException e){
                    showToast("Unexpected error occured at this time. Try again Later.");
                    Log.d("CodeVer", "NullPointerException while setting the arguments in fragement");
                    return;
                }

                String enteredText = enteredCodeEditText.getText().toString();
                String msg = "";
                if(enteredText==null || enteredText==""){
                    msg = "Enter the 4 digit number";
                }else if(enteredText.length()!=4){
                    msg = "Entered code is not a 4 digit number";
                }else {
                    try {
                        Integer.parseInt(enteredText);
                        if (!enteredText.equals(code)) {
                            msg = "Entered code is not matching, try again";
                        }
                    } catch (NumberFormatException e) {
                        msg = "Not a valid number, enter the code which you have received";
                        Log.d("CodeVer", "Number format exception");
                    }

                }


                if(!msg.equals("") ){
                    showToast(msg);
                    return;
                }

                //Success
                Log.d("codeVer", "control from code verification to main "+ bundle.toString());
                mListener.codeVerificationSubmit(bundle);
//                failureToast = Toast.makeText(getActivity().getBaseContext(), "Your number is verified", Toast.LENGTH_LONG);


            }
        });



    }

    public void showToast(String msg){
        if(failureToast==null || !failureToast.getView().isShown()){
            failureToast = Toast.makeText(getActivity().getBaseContext(), msg, Toast.LENGTH_LONG);
            failureToast.show();
        }
        return;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (CodeVerificationFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface CodeVerificationFragmentInteractionListener {
        // TODO: Update argument type and name
        public void codeVerificationSubmit(Bundle userData);
    }

}
