package com.bapan.torch;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GuideFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuideFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GuideFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Guide.
     */
    // TODO: Rename and change types and number of parameters
    public static GuideFragment newInstance(String param1, String param2) {
        GuideFragment fragment = new GuideFragment();
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_guide, container, false);
        sharedPrepData = new SharedPrepData(getContext());
        mainActivity = (MainActivity)getActivity();
        mainActivity.setScreenBrightness(0.4f); 
        initializeViews();
        initializeListeners();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initializeListeners() {
        listener = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if((v==closeBtn)){
                    getActivity().getSupportFragmentManager().popBackStack();
                    if(checkBox.isChecked()){
                        sharedPrepData.setGuideInt(0);
                    }
                    mainActivity.switchOnTorch(sharedPrepData.getSideType(),false);
                }
            }
        };
        mainFrame.setOnClickListener(null);
        closeBtn.setOnClickListener(listener);
        radioGroup.check(sharedPrepData.getSideType()?R.id.radio_id_2:R.id.radio_id_1);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if ((checkedId == R.id.radio_id_1)) {
                    sharedPrepData.setSideType(false);
                    mainActivity.switchOnTorch(false,false);
                } else {
                    sharedPrepData.setSideType(true);
                    mainActivity.setBackTorchMode(true);
                }
            }
        });
        radioGroup2.check(sharedPrepData.getNotificationType()?R.id.radioButtonB:R.id.radioButtonA);
        radioGroup2.setOnCheckedChangeListener((group, checkedId) -> {
            if ((checkedId == R.id.radioButtonA)) {
                sharedPrepData.setNotificationType(false);
            } else {
                sharedPrepData.setNotificationType(true);
                mainActivity.runNotification(true);
            }
        });
        radioGroup3.check(sharedPrepData.getSwitchSound()?R.id.radioButtonb:R.id.radioButtona);
        radioGroup3.setOnCheckedChangeListener((group, checkedId) -> {
            if ((checkedId == R.id.radioButtona)) {
                sharedPrepData.setSwitchSound(false);
            } else {
                sharedPrepData.setSwitchSound(true);
            }
        });
    }

    private void initializeViews() {
        closeBtn = view.findViewById(R.id.close_btn_id);
        mainFrame = view.findViewById(R.id.main_frame_id);
        checkBox = view.findViewById(R.id.checkBox);
        checkBox.setChecked(true);
        radioGroup = view.findViewById(R.id.radio_group_id);
        radioGroup2 = view.findViewById(R.id.radio_group_id2);
        radioGroup3 = view.findViewById(R.id.radio_group_id3);
    }

    private View view;
    private View.OnClickListener listener;
    private FrameLayout mainFrame;
    private TextView closeBtn;
    private CheckBox checkBox;
    private RadioGroup radioGroup,radioGroup2,radioGroup3;
    private SharedPrepData sharedPrepData;
    private MainActivity mainActivity;
}