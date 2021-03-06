package j.trt.s.hi.st.ecities.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import j.trt.s.hi.st.ecities.CityInfo;
import j.trt.s.hi.st.ecities.Constants;
import j.trt.s.hi.st.ecities.Game;
import j.trt.s.hi.st.ecities.R;
import j.trt.s.hi.st.ecities.User;
import j.trt.s.hi.st.ecities.data.AuthResponse;
import j.trt.s.hi.st.ecities.data.AuthTask;
import j.trt.s.hi.st.ecities.data.GetCityInfoResponse;
import j.trt.s.hi.st.ecities.data.GetCityInfoTask;
import j.trt.s.hi.st.ecities.data.GetGameStoryResponse;
import j.trt.s.hi.st.ecities.data.GetGameStoryTask;
import j.trt.s.hi.st.ecities.data.GetLibraryResponse;
import j.trt.s.hi.st.ecities.data.GetLibraryTask;
import j.trt.s.hi.st.ecities.data.GiveUpTask;
import j.trt.s.hi.st.ecities.data.LogoutResponse;
import j.trt.s.hi.st.ecities.data.LogoutTask;
import j.trt.s.hi.st.ecities.data.NewGameResponse;
import j.trt.s.hi.st.ecities.data.NewGameTask;
import j.trt.s.hi.st.ecities.data.RegistrationResponse;
import j.trt.s.hi.st.ecities.data.RegistrationTask;
import j.trt.s.hi.st.ecities.data.SendCityResponse;
import j.trt.s.hi.st.ecities.data.SendCityTask;
import j.trt.s.hi.st.ecities.fragments.AuthFragment;
import j.trt.s.hi.st.ecities.fragments.CityFragment;
import j.trt.s.hi.st.ecities.fragments.GameFragment;
import j.trt.s.hi.st.ecities.fragments.LibraryFragment;
import j.trt.s.hi.st.ecities.fragments.MenuFragment;
import j.trt.s.hi.st.ecities.fragments.RegistrationFragment;
import j.trt.s.hi.st.ecities.fragments.RulesFragment;
import j.trt.s.hi.st.ecities.fragments.ScoresFragment;

public class MainActivity extends AppCompatActivity implements AuthFragment.IOnMyEnterClickListener,
        RegistrationFragment.IOnMyRegisterClickListener, MenuFragment.IOnMyMenuClickListener,
        GameFragment.IOnMyGameClickListener, LibraryFragment.IOnMyLibraryClickListener, CityFragment.IOnMyCityInfoClickListener,
        AuthResponse, NewGameResponse, GetLibraryResponse, SendCityResponse, RegistrationResponse, LogoutResponse, GetCityInfoResponse, GetGameStoryResponse {

    private long startTime = 0;
    private boolean rememberUser = false;
    private String inputCity, login, password, email, name, surname, city;
    private int city_id = 0;
    private JSONObject cityinf = null;
    private LinkedList<CityInfo> savedCitiesList;
    private LinkedList<CityInfo> libraryList;
    private String cityName = "";
    private String cityPopulation = "";
    private String cityEstablishment = "";
    private String cityUrl = "";
    private String cityArms = "";
    private TextView tvTimer, tvOpponentTurn;
    private EditText etLogin, etPassword, etInputCity;
    private Button btnUpdateCityList;
    private Button btnContinue;
    private Fragment authFragment, registrationFragment, scoresFragment, rulesFragment, libraryFragment, cityFragment;
    private GameFragment gameFragment;
    private MenuFragment menuFragment;
    private SharedPreferences settings;

    public static boolean hasGame;

    private FragmentTransaction fTrans;

    public static User user;
    public static Game myGame;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        authFragment = new AuthFragment();
        fTrans = getSupportFragmentManager().beginTransaction();

        etLogin = (EditText) findViewById(R.id.etLogin);
        etPassword = (EditText) findViewById(R.id.etPassword);

        //Load login and password if any saved
        settings = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);

        if (settings.contains(Constants.APP_PREFERENCES_LOGIN) |
                settings.contains(Constants.APP_PREFERENCES_PASSWORD)) {
            String rememberedLogin = settings.getString(Constants.APP_PREFERENCES_LOGIN, "");
            String rememberedPassword = settings.getString(Constants.APP_PREFERENCES_PASSWORD, "");
            rememberUser = true;

            Bundle b = new Bundle();
            b.putString("login", rememberedLogin);
            b.putString("pass", rememberedPassword);
            b.putBoolean("checked", rememberUser);
            authFragment.setArguments(b);
        }

        fTrans.replace(R.id.flFragmentContainer, authFragment);
        fTrans.commit();
    }

    /**
     * Login button click
     *
     * @param c remember user
     */
    @Override
    public void onEnterButtonClick(boolean c) {
        etLogin = (EditText) findViewById(R.id.etLogin);
        etPassword = (EditText) findViewById(R.id.etPassword);

        String login = etLogin.getText().toString();
        String password = etPassword.getText().toString();

        //Save login and password
        if (c) {
            rememberUser = true;
            settings = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(Constants.APP_PREFERENCES_LOGIN, login);
            editor.putString(Constants.APP_PREFERENCES_PASSWORD, password);
            editor.putBoolean(Constants.APP_PREFERENCES_CHECKED, true);
            editor.apply();
        } else {
            settings = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.apply();
        }

        user = new User(login, password);
        if (!user.login.equals("") && !user.password.equals("")) {
            new AuthTask(this).execute(user.authCertificate);
        } else if (user.login.equals("")) {
            Toast.makeText(this, "Please enter login", Toast.LENGTH_SHORT).show();
        } else if (user.password.equals("")) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Auth Fragment register text click
     */
    @Override
    public void onRegistrationTextClick() {
        registrationFragment = new RegistrationFragment();
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.replace(R.id.flFragmentContainer, registrationFragment);
        fTrans.addToBackStack("AuthFragment");
        fTrans.commit();
    }

    /**
     * Registration Fragment form submit click
     */
    @Override
    public void onRegisterButtonClick(String login, String password, String email, String name,
                                      String surname, String city) {
        this.login = login;
        this.password = password;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.city = city;

        getSupportFragmentManager().popBackStack();

        new RegistrationTask(this).execute(login, password, email, name,
                surname, city);

        Log.d(Constants.LOG_TAG, "Registration data: " + login + " " + password + " " + email + " " +
                name + " " + surname + " " + city);
    }

    /**
     * Continue game button click
     */
    @Override
    public void onContinueButtonClick() {
        gameFragment = GameFragment.newInstance(savedCitiesList);
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.replace(R.id.flFragmentContainer, gameFragment);
        fTrans.addToBackStack("AuthFragment");
        fTrans.commit();
        timer.start();
    }

    /**
     * High Scores button click
     */
    @Override
    public void onScoresButtonClick() {
        scoresFragment = new ScoresFragment();
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.replace(R.id.flFragmentContainer, scoresFragment);
        fTrans.addToBackStack("MenuFragment");
        fTrans.commit();
    }

    /**
     * Rules button click
     */
    @Override
    public void onRulesButtonClick() {
        rulesFragment = new RulesFragment();
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.replace(R.id.flFragmentContainer, rulesFragment);
        fTrans.addToBackStack("MenuFragment");
        fTrans.commit();
    }

    /**
     * Library button click
     */
    @Override
    public void onLibraryButtonClick() {
        new GetLibraryTask(this).execute();

    }


    @Override
    public void onSendButtonClick() {
        etInputCity = (EditText) findViewById(R.id.etInputCity);
        inputCity = etInputCity.getText().toString();
        if (!inputCity.equals("")) {
            new SendCityTask(this).execute(inputCity);
            etInputCity.setText("");
        } else {
            Toast.makeText(this, inputCity + "Please enter a city", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void authIsDone(String output) {

        Log.d(Constants.LOG_TAG, "Auth result = <<<" + output + ">>>");
        myGame = new Game();

        JSONObject jsauth = null;
        JSONObject jsgameStatus = null;
        if (output.equals("http://ecity.org.ua:8080/game/status")) {
            Toast.makeText(this, "Вам не удалось авторизироваться. Введите пожалуйста корректные авторизационные данные.", Toast.LENGTH_SHORT).show();
            authFragment = new AuthFragment();
            fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.flFragmentContainer, authFragment);
            fTrans.commit();
        } else if (output.equals("failed to connect to ecity.org.ua/195.211.153.45 (port 8080): connect failed: ECONNREFUSED (Connection refused)")) {
            Toast.makeText(this, "Нет ответа от сервера. Попробуйте позже.", Toast.LENGTH_SHORT).show();
        } else {
            try {
                jsauth = new JSONObject(output);
                myGame.id = jsauth.getString(Constants.ID);
                jsgameStatus = (JSONObject) jsauth.getJSONObject("gameStatus");
                myGame.code = jsgameStatus.getString("code");
                myGame.message = jsgameStatus.getString("message");
                Log.d(Constants.LOG_TAG, "game ID = " + myGame.id + "; code = " + myGame.code + "; message = " + myGame.message);
                if (myGame.id != null & myGame.message.equals("Game exists") & myGame.code.equals("0")) {
                    new GetGameStoryTask(this).execute(user.authCertificate, myGame.id);
                    Toast.makeText(this, "Welcome " + user.login, Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "У Вас есть созданная игра", Toast.LENGTH_SHORT).show();
                    //when user has created game
                    hasGame = true;
                    menuFragment = new MenuFragment();
                    fTrans = getSupportFragmentManager().beginTransaction();
                    fTrans.replace(R.id.flFragmentContainer, menuFragment);
                    fTrans.addToBackStack("AuthFragment");
                    fTrans.commit();
                } else if (myGame.id.equals("null") & myGame.message.equals("Game doesn't exist") & myGame.code.equals("1")) {
                    //when user hasn't games
                    menuFragment = new MenuFragment();
                    fTrans = getSupportFragmentManager().beginTransaction();
                    fTrans.replace(R.id.flFragmentContainer, menuFragment);
                    fTrans.addToBackStack("AuthFragment");
                    fTrans.commit();
                    Toast.makeText(this, "Welcome " + user.login, Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "У Вас нет начатой игры", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(MainActivity.this, Constants.AUTH_FAIL, Toast.LENGTH_SHORT);
                Log.d(Constants.LOG_TAG, "JSAuthError = " + e.toString());

            }


        }


    }

    @Override
    public void onNewGameButtonClick() {
        new NewGameTask(this).execute(user.authCertificate);
    }

    @Override
    public void newGameId(String newGameId) {
        Log.d(Constants.LOG_TAG, "new Game id = " + newGameId);
        try {
            JSONObject jsonId = new JSONObject(newGameId);
            myGame.id = jsonId.getString(Constants.ID);
            Log.d(Constants.LOG_TAG, "new Game id = " + myGame.id);
            Toast.makeText(this, "New Game Id = " + myGame.id, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Log.e(Constants.LOG_TAG, e.toString());
        }
        gameFragment = new GameFragment();
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.replace(R.id.flFragmentContainer, gameFragment);
        fTrans.addToBackStack("MenuFragment");
        fTrans.commit();
    }

    @Override
    public void getLibrary(String library) {
        libraryList = new LinkedList<CityInfo>();
        Log.d(Constants.LOG_TAG, "library" + library);
        JSONArray libraryArray = null;
        JSONObject cityJS = null;
        try {
            libraryArray = new JSONArray(library);
            for (int i = 0; i < libraryArray.length(); i++) {
                CityInfo libraryCity = new CityInfo();
                cityJS = libraryArray.getJSONObject(i);
                libraryCity.id = Integer.parseInt(cityJS.getString(Constants.ID));
                libraryCity.name = cityJS.getString(Constants.NAME);
                Log.d(Constants.LOG_TAG, "library = " + i + " ; " + libraryCity.id + " ; " + libraryCity.name);
                libraryList.add(i, libraryCity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        libraryFragment = LibraryFragment.newInstance(libraryList);
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.replace(R.id.flFragmentContainer, libraryFragment);
        fTrans.addToBackStack("LibraryFragment");
        fTrans.commit();
//        libraryFragment = new LibraryFragment();
//        Bundle b = new Bundle();
//        b.putStringArray("library", library);
//        libraryFragment.setArguments(b);
//        fTrans = getSupportFragmentManager().beginTransaction();
//        fTrans.replace(R.id.flFragmentContainer, libraryFragment);
//        fTrans.addToBackStack("MenuFragment");
//        fTrans.commit();
    }

    //Timer
    CountDownTimer timer = new CountDownTimer(60000, 1000) {

        public void onTick(long millisUntilFinished) {
            tvTimer = (TextView) findViewById(R.id.tvTimer);
            if (tvTimer != null)
                tvTimer.setText("" + millisUntilFinished / 1000);
        }

        public void onFinish() {
            tvTimer = (TextView) findViewById(R.id.tvTimer);
            if (tvTimer != null)
                tvTimer.setText("--");

            gameOver();
        }
    };

    @Override
    public void sendCityResponse(String r) {
//Server returns next city
//        Статусы игры после хода:
//        - 0 - всё прошло хорошо (сервер вернул следующий город)
//        - 1 - игры не существует
//        - 10 - город не существует в базе
//        - 11 - город уже был назван
//        - 12 - введен неправильный город (не на ту букву)
//        - 20 - выиграл пользователь
//        - 21 - выиграл компьютер
        // -22 закончилось время
        Log.d(Constants.LOG_TAG, "ответ сервера на наш ход = " + r);

        JSONObject response = null;
        JSONObject gameStatus = null;
        JSONObject city = null;
        JSONObject clientCity = null;
        String serverCity = "";
        String cityClient = "";

        try {
            response = new JSONObject(r);
            gameStatus = response.getJSONObject(Constants.GAME_STATUS);
            myGame.gameStatusCode = gameStatus.getString(Constants.GAME_STATUS_CODE);
            myGame.gameStatusMessage = gameStatus.getString(Constants.GAME_STATUS_MESSAGE);
            Log.d(Constants.LOG_TAG, "myGame.code" + myGame.gameStatusCode + myGame.gameStatusMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        - 0 - всё прошло хорошо (сервер вернул следующий город)
        if (myGame.gameStatusCode.equals("0")) {
            Log.d(Constants.LOG_TAG, "myGame.responsecode = " + myGame.code);
            try {
                city = response.getJSONObject(Constants.CITY);
                serverCity = city.getString(Constants.NAME);
                CityInfo opponentTurn = new CityInfo();
                opponentTurn.id = Integer.parseInt(city.getString(Constants.ID));
                opponentTurn.name = city.getString(Constants.NAME);


                clientCity = response.getJSONObject(Constants.CITY_CLIENT);
                cityClient = clientCity.getString(Constants.NAME);
                CityInfo clientMove = new CityInfo();
                clientMove.id = Integer.parseInt(clientCity.getString(Constants.ID));
                clientMove.name = clientCity.getString(Constants.NAME);

                tvOpponentTurn = (TextView) findViewById(R.id.tvOpponentTurn);

                //Last valid letter colour setup
                SpannableStringBuilder sb = new SpannableStringBuilder(serverCity);
                ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.textSecondaryColor));
                String lastChar = null;
                if (city != null) {
                    try {
                        lastChar = city.getString(Constants.LAST_CHAR).toLowerCase();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                int last = serverCity.length();

                if (!String.valueOf(serverCity.charAt(last - 1)).equals(lastChar))
                    last -= 1;
                else if (!String.valueOf(serverCity.charAt(last - 1)).equals(lastChar))
                    last -= 2;

                sb.setSpan(fcs, last - 1, last, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                tvOpponentTurn.setText(sb);

                timer.start();
//                gameFragment.addCity(cityClient);
//                gameFragment.addCity(serverCity);
                gameFragment.addCity(clientMove);
                gameFragment.addCity(opponentTurn);
//                Toast.makeText(MainActivity.this, "Ответ сервера = " + serverCity, Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

//        - 20 - выиграл пользователь
        } else if (myGame.gameStatusCode.equals("20")) {
            hasGame = false;
            Toast.makeText(MainActivity.this, "Поздравляем Вас! Вы выиграли в этой игре!!!" + myGame.gameStatusMessage, Toast.LENGTH_LONG).show();
            fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.flFragmentContainer, menuFragment);
            fTrans.addToBackStack("MenuFragment");
            fTrans.commit();

//        - 1 - игры не существует
        } else if (myGame.gameStatusCode.equals("1")) {
            Toast.makeText(MainActivity.this, "Такой игры не существует!" + myGame.gameStatusMessage, Toast.LENGTH_LONG).show();
            fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.flFragmentContainer, menuFragment);
            fTrans.addToBackStack("MenuFragment");
            fTrans.commit();

//        - 10 - город не существует в базе
        } else if (myGame.gameStatusCode.equals("10")) {
            Toast.makeText(MainActivity.this, "Такой город родом не из Украины!" + myGame.gameStatusMessage, Toast.LENGTH_LONG).show();

//        - 11 - город уже был назван
        } else if (myGame.gameStatusCode.equals("11")) {
            Toast.makeText(MainActivity.this, "Этот город уже был назван!" + myGame.gameStatusMessage, Toast.LENGTH_LONG).show();

//        - 12 - введен неправильный город (не на ту букву)
        } else if (myGame.gameStatusCode.equals("12")) {
            Toast.makeText(MainActivity.this, "Ваш город начинается не на ту букву!" + myGame.gameStatusMessage, Toast.LENGTH_LONG).show();

//        - 21 - выиграл компьютер
        } else if (myGame.gameStatusCode.equals("21")) {
            hasGame = false;
            Toast.makeText(MainActivity.this, "К сожалению, Вы проиграли!" + myGame.gameStatusMessage, Toast.LENGTH_LONG).show();
            fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.flFragmentContainer, menuFragment);
            fTrans.addToBackStack("MenuFragment");
            fTrans.commit();

//        -22 закончилось время
        } else if (myGame.gameStatusCode.equals("22")) {
            hasGame = false;
            Toast.makeText(MainActivity.this, "Время вышло! Вы проирали!" + myGame.gameStatusMessage, Toast.LENGTH_LONG).show();
            fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.flFragmentContainer, menuFragment);
            fTrans.addToBackStack("MenuFragment");
            fTrans.commit();

        }
    }

    @Override
    public void onGiveUpButtonClick() {
        hasGame = false;
        timer.onFinish();
        new GiveUpTask(this).execute();
    }

    //Game and Library City click
    @Override
    public void onGameCityClick(String city_id) {
        Log.d(Constants.LOG_TAG, "onGameCityClick = <<<" + city_id + ">>>");
        cityFragment = new CityFragment();
        new GetCityInfoTask(this).execute(city_id);
    }

    @Override
    public void libraryCityInfo(String cityInfo) {
        Log.d(Constants.LOG_TAG, "get city info = " + cityInfo);
        try {
            cityinf = new JSONObject(cityInfo);

            cityName = cityinf.getString(Constants.NAME);
            cityPopulation = cityinf.getString(Constants.POPULATION);
            cityEstablishment = cityinf.getString(Constants.ESTABLISHMENT);
            cityUrl = cityinf.getString(Constants.URL);
            cityArms = cityinf.getString(Constants.ARMS);

            Log.d(Constants.LOG_TAG, "Library name: " + cityName);
            Log.d(Constants.LOG_TAG, "Library population: " + cityPopulation);
            Log.d(Constants.LOG_TAG, "Library establishment: " + cityEstablishment);
            Log.d(Constants.LOG_TAG, "Library url: " + cityUrl);
            Log.d(Constants.LOG_TAG, "Library arms: " + cityArms);

            //Open City Fragment
            Bundle bundle = new Bundle();
            bundle.putString("cityName", cityName);
            bundle.putString("cityPopulation", cityPopulation);
            bundle.putString("cityEstablishment", cityEstablishment);
            bundle.putString("cityUrl", cityUrl);
            bundle.putString("cityArms", cityArms);
            cityFragment.setArguments(bundle);
            fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.flFragmentContainer, cityFragment);
            fTrans.addToBackStack("MenuFragment");
            fTrans.commit();

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, "parsing cityInfo error: " + e.toString());
        }
    }

    //Game over
    private void gameOver() {
        hasGame = false;
        getSupportFragmentManager().popBackStack();
        Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void registrationComplete(String reg) {

        // {"code":31,"message":"User registration OK "}
        //{"gameStatus":{"code": 32,"message":" The player exist"}
        // {"gameStatus":{"code": 33,"message":" User doesn't enter password"}
        //{"gameStatus":{"code": 34,"message":" User doesn't enter login"}
        //{"gameStatus":{"code": 35,"message":" User enter incorrect e-mail"}
        //{"gameStatus":{"code": 36,"message":"User Login must be less than 20 character"}

        String code = "";
        Log.d(Constants.LOG_TAG, "registration response = " + reg);
        JSONObject registrationJSON;
        try {
            registrationJSON = new JSONObject(reg);
            code = registrationJSON.getString(Constants.GAME_STATUS_CODE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (code.equals("31")) {
            Toast.makeText(MainActivity.this, "Регистрация прошла успешно", Toast.LENGTH_LONG).show();
            fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.flFragmentContainer, authFragment);
            fTrans.addToBackStack("RegistrationFragment");
            fTrans.commit();

        } else if (code.equals("32")) {
            Toast.makeText(MainActivity.this, "Пользователь с таким именем уже существует", Toast.LENGTH_LONG).show();
            fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.flFragmentContainer, registrationFragment);
            fTrans.commit();

        } else if (code.equals("33")) {
            Toast.makeText(MainActivity.this, "Вы не ввели пароль", Toast.LENGTH_LONG).show();
            fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.flFragmentContainer, registrationFragment);
            fTrans.commit();

        } else if (code.equals("34")) {
            Toast.makeText(MainActivity.this, "Вы не ввели логин", Toast.LENGTH_LONG).show();
            fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.flFragmentContainer, registrationFragment);
            fTrans.commit();

        } else if (code.equals("35")) {
            Toast.makeText(MainActivity.this, "Вы ввели неправильный e-mail", Toast.LENGTH_LONG).show();
            fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.flFragmentContainer, registrationFragment);
            fTrans.commit();

        } else if (code.equals("36")) {
            Toast.makeText(MainActivity.this, "Длина имени пользователя не должна превышать 20 символов", Toast.LENGTH_LONG).show();
            fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.flFragmentContainer, registrationFragment);
            fTrans.commit();
        }
    }

    @Override
    public void onLogoutButtonClick() {
        new LogoutTask(this).execute();
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void logoutResponse(String out) {
        Log.d(Constants.LOG_TAG, "logout = " + out);
    }

    @Override
    public void onCityLinkClick(String link) {
        if (link != null) {
            Uri address = Uri.parse(link);
            Intent openlinkIntent = new Intent(Intent.ACTION_VIEW, address);
            startActivity(openlinkIntent);
        }
    }

    @Override
    public void getGameStoryResponse(String history) {
        Log.d(Constants.LOG_TAG, "history = <<<" + history + ">>>");
        JSONArray jahistory = null;
        JSONObject jsonObject = null;
        JSONObject jsonObject1 = null;
        savedCitiesList = new LinkedList<CityInfo>();
        try {
            jahistory = new JSONArray(history);
            for (int i = 0; i < jahistory.length(); i++) {
                jsonObject = jahistory.getJSONObject(i);
                jsonObject1 = jsonObject.getJSONObject(Constants.CITY);
                CityInfo cityInfo = new CityInfo();

                cityInfo.id = Integer.parseInt(jsonObject1.getString(Constants.ID));
                cityInfo.name = jsonObject1.getString(Constants.NAME);
                cityInfo.establishment = jsonObject1.getString(Constants.ESTABLISHMENT);
                cityInfo.url = jsonObject1.getString(Constants.URL);
                cityInfo.arms = jsonObject1.getString(Constants.ARMS);
                cityInfo.lastChar = jsonObject1.getString(Constants.LAST_CHAR);
                Log.d(Constants.LOG_TAG, "move " + i + "=" + cityInfo.name + ";" + cityInfo.establishment + ";" + cityInfo.url + ";" + cityInfo.arms + ";" + cityInfo.lastChar);

                //Add city to Saved Cities List for Continue Game
                savedCitiesList.add(i, cityInfo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}