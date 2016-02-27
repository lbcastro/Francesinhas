//package pt.castro.francesinhas.communication.login;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.facebook.FacebookSdk;
//import com.facebook.login.widget.LoginButton;
//
//import pt.castro.francesinhas.R;
//
///**
// * Created by lourenco.castro on 07/06/15.
// */
//public class LoginFragment extends Fragment {
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
// savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
//        FacebookSdk.sdkInitialize(getActivity());
//        LoginButton loginButton = (LoginButton) rootView.findViewById(R.id
// .login_button);
//        loginButton.setReadPermissions("public_profile");
//        // If using in a fragment
//        loginButton.setFragment(this);
//        return rootView;
//
//    }
//}