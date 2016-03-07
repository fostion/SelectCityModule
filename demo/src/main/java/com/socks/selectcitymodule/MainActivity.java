package com.socks.selectcitymodule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.socks.selectcity.bean.CityBean;
import com.socks.selectcity.utils.CharacterParser;
import com.socks.selectcity.utils.PinyinComparator;
import com.socks.selectcity.view.SideBar;
import com.socks.selectcitymodule.adapter.SortAdapter;

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
    private LinearLayoutManager mLinearLayoutManager;

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
        //todo 拼音排序将中文转换成英文开头排序，执行一次就可以了(可以存储起来以后不必重新排序)
        Collections.sort(SourceDateList, new PinyinComparator());
        adapter = new SortAdapter(SourceDateList);
        mLinearLayoutManager = new LinearLayoutManager(this);
        sortListView.setLayoutManager(mLinearLayoutManager);
        sortListView.setAdapter(adapter);

        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                //滑动到相应位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mIndex = position;
                    moveToPosition(position);
                }

            }
        });

        sortListView.addOnScrollListener(new RecyclerViewListener());
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

    private boolean move = false;
    private int mIndex = 0;

    /** 滑动处理 */
    private void moveToPosition(int n) {
        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        int firstItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        int lastItem = mLinearLayoutManager.findLastVisibleItemPosition();
        //然后区分情况
        if (n <= firstItem ){
            //当要置顶的项在当前显示的第一个项的前面时
            sortListView.scrollToPosition(n);
        }else if ( n <= lastItem ){
            //当要置顶的项已经在屏幕上显示时
            int top = sortListView.getChildAt(n - firstItem).getTop();
            sortListView.scrollBy(0, top);
        }else{
            //当要置顶的项在当前显示的最后一项的后面时
            sortListView.scrollToPosition(n);
            //这里这个变量是用在RecyclerView滚动监听里面的
            move = true;
        }

    }


    /**　滑动处理将tab滑动首个位置　*/
    class RecyclerViewListener extends RecyclerView.OnScrollListener{
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (move){
                move = false;
                int n = mIndex - mLinearLayoutManager.findFirstVisibleItemPosition();
                if ( 0 <= n && n < sortListView.getChildCount()){
                    int top = sortListView.getChildAt(n).getTop();
                    sortListView.scrollBy(0, top);
                }
            }
        }
    }
}
