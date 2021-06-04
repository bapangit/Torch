package com.bapan.torch;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

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

    private void initializeListeners() {
        listener = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if((v==closeBtn)){
                    getActivity().getSupportFragmentManager().popBackStack();
                        sharedPrepData.setGuideStatus(!checkBox.isChecked());
                    if(checkBox.isChecked()){
                        sharedPrepData.setGuideInt(0);
                    }
                    mainActivity.switchOnTorch(sharedPrepData.getTorchType());
                }
            }
        };
        mainFrame.setOnClickListener(null);
        closeBtn.setOnClickListener(listener);
        radioGroup.check(sharedPrepData.getTorchType()?R.id.radio_id_2:R.id.radio_id_1);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if ((checkedId == R.id.radio_id_1)) {
                    sharedPrepData.setTorchType(false);
                    mainActivity.switchOnTorch(false);
                } else {
                    sharedPrepData.setTorchType(true);
                    mainActivity.setBackTorchMode(true);
                }
            }
        });
    }

    private void initializeViews() {
        closeBtn = view.findViewById(R.id.close_btn_id);
        mainFrame = view.findViewById(R.id.main_frame_id);
        checkBox = view.findViewById(R.id.checkBox);
        radioGroup = view.findViewById(R.id.radio_group_id);
    }

    private View view;
    private View.OnClickListener listener;
    private FrameLayout mainFrame;
    private Button closeBtn;
    private CheckBox checkBox;
    private RadioGroup radioGroup;
    private SharedPrepData sharedPrepData;
    private MainActivity mainActivity;
}