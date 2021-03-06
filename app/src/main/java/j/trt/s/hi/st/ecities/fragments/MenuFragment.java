package j.trt.s.hi.st.ecities.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import j.trt.s.hi.st.ecities.R;
import j.trt.s.hi.st.ecities.activities.MainActivity;

public class MenuFragment extends Fragment implements View.OnClickListener {

    private Button btnNewGame, btnContinue, btnScores, btnRules, btnLibrary, btnLogout;
    private EditText etInputCity;

    private IOnMyMenuClickListener menuClickListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        menuClickListener = (IOnMyMenuClickListener) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        btnNewGame = (Button)view.findViewById(R.id.btnNewGame);
        btnContinue = (Button)view.findViewById(R.id.btnContinue);
        btnScores = (Button)view.findViewById(R.id.btnScores);
        btnRules = (Button)view.findViewById(R.id.btnRules);
        btnLibrary = (Button)view.findViewById(R.id.btnLibrary);
        btnLogout = (Button)view.findViewById(R.id.btnExit);
        etInputCity = (EditText)view.findViewById(R.id.etInputCity);

        btnNewGame.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        btnScores.setOnClickListener(this);
        btnRules.setOnClickListener(this);
        btnLibrary.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

        btnContinue.setVisibility(View.GONE);

        //TODO Load scores
        btnScores.setVisibility(View.GONE);

//        btnContinue.setTextColor(getResources().getColor(R.color.mainActivityLight));

        if(MainActivity.hasGame) {
            btnContinue.setVisibility(View.VISIBLE);
//            btnContinue.setTextColor(getResources().getColor(R.color.textInactiveColor));
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNewGame:
                menuClickListener.onNewGameButtonClick();
                break;

            case R.id.btnContinue:
                menuClickListener.onContinueButtonClick();
                break;

            case R.id.btnScores:
                menuClickListener.onScoresButtonClick();
                break;

            case R.id.btnRules:
                menuClickListener.onRulesButtonClick();
                break;

            case R.id.btnLibrary:
                menuClickListener.onLibraryButtonClick();
                break;

            case R.id.btnExit:
                menuClickListener.onLogoutButtonClick();
                break;

        }
    }

    public void enableContinue() {
        btnContinue.setEnabled(true);
    }

    public static final MenuFragment newInstance(boolean b)
    {
        MenuFragment f = new MenuFragment();
        Bundle bdl = new Bundle(1);
        bdl.putBoolean("enableContinue", b);
        f.setArguments(bdl);
        return f;
    }

    public interface IOnMyMenuClickListener {
        void onNewGameButtonClick();
        void onContinueButtonClick();
        void onScoresButtonClick();
        void onRulesButtonClick();
        void onLibraryButtonClick();
        void onLogoutButtonClick();
    }

}
