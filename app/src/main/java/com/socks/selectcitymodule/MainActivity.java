package com.socks.selectcitymodule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.socks.selectcitymodule.view.CharacterParser;
import com.socks.selectcitymodule.view.CityBean;
import com.socks.selectcitymodule.view.PinyinComparator;
import com.socks.selectcitymodule.view.SideBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 有一个问题无法将tag显示到第一个
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private CharacterParser characterParser;
    private List<CityBean> SourceDateList;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("选择城市");

        initViews();
    }

    private void initViews() {
        characterParser = CharacterParser.getInstance();
        sortListView = (RecyclerView) findViewById(R.id.country_lvcountry);
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        SourceDateList = filledJsonData(readFileToJson("city.json"));
        //拼音排序将中文转换成英文开头排序，执行一次就可以了
        Collections.sort(SourceDateList, new PinyinComparator());
        adapter = new SortAdapter(SourceDateList);
        sortListView.setLayoutManager(new LinearLayoutManager(this));
        sortListView.setAdapter(adapter);

        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                //滑动到相应位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.scrollToPosition(position);
                }

            }
        });

        adapter.setOnItemClickListener(new SortAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CityBean cityBean) {
                Toast.makeText(getApplication(), cityBean.getName(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private JSONArray readFileToJson(String fileName){
        JSONArray cityObjs;
        String result = "";
        try {
            String line;
            InputStreamReader inputStreamReader = new InputStreamReader(this.getAssets().open(fileName));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while ((line = bufferedReader.readLine()) != null){
                result += line;
            }
            cityObjs = new JSONArray(result);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return cityObjs;
    }

    private List<CityBean> filledJsonData(JSONArray cityArray) {
        List<CityBean> mSortList = new ArrayList<>();
        ArrayList<String> indexString = new ArrayList<>();

        try{
            for (int i = 0; i < cityArray.length(); i++) {
                JSONObject tempJsonObj = cityArray.getJSONObject(i);
                CityBean sortModel = new CityBean();
                sortModel.setId(tempJsonObj.getString("id"));
                sortModel.setName(tempJsonObj.getString("name"));
                sortModel.setLevel(tempJsonObj.getString("level"));
                sortModel.setWepiao_id(tempJsonObj.getString("wepiao_id"));
                sortModel.setParent_id(tempJsonObj.getString("parent_id"));
                String pinyin = characterParser.getSelling(sortModel.getName());
                String sortString = pinyin.substring(0, 1).toUpperCase();
                if (sortString.matches("[A-Z]")) {

                    //对重庆多音字做特殊处理
                    if (pinyin.startsWith("zhongqing")) {
                        sortString = "C";
                        sortModel.setSortLetters("C");
                    } else {
                        sortModel.setSortLetters(sortString.toUpperCase());
                    }

                    if (!indexString.contains(sortString)) {
                        indexString.add(sortString);
                    }
                }

                mSortList.add(sortModel);
            }
            Collections.sort(indexString);
            sideBar.setIndexText(indexString);
        } catch (Exception e){
            e.printStackTrace();
        }
        return mSortList;

    }
}
